package passambler.parser;

import passambler.parser.expression.ExpressionParser;
import passambler.exception.ParserException;
import passambler.exception.ErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.pack.Package;
import passambler.pack.file.FilePackage;
import passambler.pack.json.JsonPackage;
import passambler.pack.math.MathPackage;
import passambler.pack.net.NetPackage;
import passambler.pack.os.OsPackage;
import passambler.pack.std.StdPackage;
import passambler.pack.thread.ThreadPackage;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.feature.*;
import passambler.parser.typehint.StandardTypehint;
import passambler.parser.typehint.Typehint;
import passambler.value.BooleanValue;
import passambler.value.DictValue;
import passambler.value.Value;
import passambler.value.ErrorValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;

public class Parser {
    private List<Feature> features = new ArrayList<>();
    private List<Package> packages = new ArrayList<>();
    private Map<String, Typehint> typehints = new HashMap();

    private Block catchBlock;
    private String catchErrorName;

    private Scope scope;

    public Parser() {
        this(new Scope());
    }

    public Parser(Scope scope) {
        this.scope = scope;

        features.add(new IfFeature());
        features.add(new WhileFeature());
        features.add(new ForFeature());
        features.add(new ReturnFeature());
        features.add(new FunctionFeature());
        features.add(new ClassFeature());
        features.add(new TryFeature());
        features.add(new AssignmentFeature());
        features.add(new ExpressionFeature());

        packages.add(new StdPackage());
        packages.add(new MathPackage());
        packages.add(new FilePackage());
        packages.add(new OsPackage());
        packages.add(new NetPackage());
        packages.add(new ThreadPackage());
        packages.add(new JsonPackage());

        typehints.put("num", new StandardTypehint(NumberValue.class));
        typehints.put("str", new StandardTypehint(StringValue.class));
        typehints.put("bool", new StandardTypehint(BooleanValue.class));
        typehints.put("list", new StandardTypehint(ListValue.class));
        typehints.put("dict", new StandardTypehint(DictValue.class));
        typehints.put("err", new StandardTypehint(ErrorValue.class));
    }

    public void setCatch(Block block, String errorName) {
        this.catchBlock = block;
        this.catchErrorName = errorName;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public Map<String, Typehint> getTypehints() {
        return typehints;
    }

    public Scope getScope() {
        return scope;
    }

    public Value parse(TokenStream stream) throws EngineException {
        try {
            for (Feature feature : features) {
                if (feature.canPerform(this, stream)) {
                    Value result = feature.perform(this, stream);

                    if (result != null) {
                        return result;
                    }

                    break;
                }
            }
        } catch (ErrorException e) {
            if (catchBlock != null) {
                catchBlock.getParser().getScope().setSymbol(catchErrorName, e.getError());

                Value catchResult = catchBlock.invoke();

                return catchResult == null ? Value.VALUE_NIL : catchResult;
            } else {
                return e.getError();
            }
        }

        return null;
    }

    public Value parse(Lexer lexer) throws EngineException {
        return parse(lexer.scan());
    }

    public Value parse(List<Token> tokens) throws EngineException {
        List<Token> subTokens = new ArrayList<>();

        int depth = 0;

        for (Token token : tokens) {
            Token peekToken = tokens.indexOf(token) == tokens.size() - 1 ? null : tokens.get(tokens.indexOf(token) + 1);

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            subTokens.add(token);

            if (depth == 0 && (token.getType() == TokenType.SEMI_COL || token.getType() == TokenType.RIGHT_BRACE)) {
                if (peekToken != null && peekToken.getType().isLineInsensitive()) {
                    continue;
                }

                if (token.getType() == TokenType.SEMI_COL) {
                    subTokens.remove(subTokens.size() - 1);
                }

                if (subTokens.size() > 0) {
                    Value result = parse(new TokenStream(subTokens));

                    subTokens.clear();

                    if (result != null) {
                        if (result instanceof ErrorValue) {
                            throw new ErrorException((ErrorValue) result);
                        }

                        return result;
                    }
                }
            }
        }

        if (!subTokens.isEmpty()) {
            throw new ParserException(ParserExceptionType.UNEXPECTED_EOF);
        }

        return null;
    }

    public Block parseBlock(TokenStream stream) throws EngineException {
        Block block = new Block(scope);

        stream.match(TokenType.LEFT_BRACE);

        stream.next();

        int braces = 1;

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.LEFT_BRACE) {
                braces++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACE) {
                braces--;

                if (braces == 0) {
                    break;
                }
            }

            block.getTokens().add(stream.current());

            stream.next();
        }

        stream.match(TokenType.RIGHT_BRACE);

        return block;
    }

    public List<Token> parseExpressionTokens(TokenStream stream, TokenType... endingTokens) throws EngineException {
        int depth = 0;

        List<Token> valueTokens = new ArrayList<>();

        while (stream.hasNext()) {
            Token token = stream.current();

            if (Arrays.asList(endingTokens).contains(token.getType()) && depth == 0) {
                break;
            }

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            valueTokens.add(token);

            stream.next();
        }

        return valueTokens;
    }

    public Value parseExpression(TokenStream stream, TokenType... endingTokens) throws EngineException {
        return new ExpressionParser(this, new TokenStream(parseExpressionTokens(stream, endingTokens))).parse();
    }

    public List<ArgumentDefinition> parseArgumentDefinition(TokenStream stream) throws EngineException {
        stream.match(TokenType.LEFT_PAREN);

        stream.next();

        List<ArgumentDefinition> arguments = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.RIGHT_PAREN) {
                break;
            } else {
                stream.match(TokenType.IDENTIFIER);

                ArgumentDefinition definition = new ArgumentDefinition();

                definition.setName(stream.current().getValue());

                stream.next();

                if (stream.current().getType() == TokenType.IDENTIFIER) {
                    if (!typehints.containsKey(definition.getName())) {
                        throw new ParserException(ParserExceptionType.UNDEFINED_TYPEHINT, stream.current().getPosition(), definition.getName());
                    }

                    definition.setTypehint(typehints.get(definition.getName()));
                    definition.setName(stream.current().getValue());

                    stream.next();
                }

                if (stream.current().getType() == TokenType.ASSIGN) {
                    stream.next();

                    definition.setDefaultValue(parseExpression(stream, TokenType.RIGHT_PAREN, TokenType.COMMA));
                }

                if (stream.current().getType() != TokenType.RIGHT_PAREN) {
                    stream.match(TokenType.COMMA);

                    stream.next();
                }

                arguments.add(definition);
            }
        }

        stream.match(TokenType.RIGHT_PAREN);

        return arguments;
    }
}