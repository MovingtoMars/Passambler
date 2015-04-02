package passambler.parser;

import passambler.parser.expression.ExpressionParser;
import passambler.exception.ParserException;
import passambler.exception.ErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import passambler.lexer.Lexer;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.pack.Package;
import passambler.pack.file.PackageFile;
import passambler.pack.json.PackageJson;
import passambler.pack.math.PackageMath;
import passambler.pack.net.PackageNet;
import passambler.pack.os.PackageOs;
import passambler.pack.std.PackageStd;
import passambler.pack.thread.PackageThread;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.feature.*;
import passambler.value.Value;
import passambler.value.ValueError;

public class Parser {
    private List<Feature> features = new ArrayList<>();
    private List<Package> defaultPackages = new ArrayList<>();

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

        defaultPackages.add(new PackageStd());
        defaultPackages.add(new PackageMath());
        defaultPackages.add(new PackageFile());
        defaultPackages.add(new PackageOs());
        defaultPackages.add(new PackageNet());
        defaultPackages.add(new PackageThread());
        defaultPackages.add(new PackageJson());
    }

    public void setCatch(Block block, String errorName) {
        this.catchBlock = block;
        this.catchErrorName = errorName;
    }

    public List<Package> getDefaultPackages() {
        return defaultPackages;
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

        int braces = 0, paren = 0, brackets = 0;

        for (Token token : tokens) {
            Token peekToken = tokens.indexOf(token) == tokens.size() - 1 ? null : tokens.get(tokens.indexOf(token) + 1);

            if (token.getType() == TokenType.LBRACE) {
                braces++;
            } else if (token.getType() == TokenType.RBRACE) {
                braces--;
            } else if (token.getType() == TokenType.LPAREN) {
                paren++;
            } else if (token.getType() == TokenType.RPAREN) {
                paren--;
            } else if (token.getType() == TokenType.LBRACKET) {
                brackets++;
            } else if (token.getType() == TokenType.RBRACKET) {
                brackets--;
            }

            subTokens.add(token);

            if (braces == 0 && paren == 0 && brackets == 0 && (token.getType() == TokenType.SEMI_COL || token.getType() == TokenType.RBRACE)) {
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
                        if (result instanceof ValueError) {
                            throw new ErrorException((ValueError) result);
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

        stream.match(TokenType.LBRACE);

        stream.next();

        int braces = 1;

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.LBRACE) {
                braces++;
            } else if (stream.current().getType() == TokenType.RBRACE) {
                braces--;

                if (braces == 0) {
                    break;
                }
            }

            block.getTokens().add(stream.current());

            stream.next();
        }

        stream.match(TokenType.RBRACE);

        return block;
    }

    public List<Token> parseExpressionTokens(TokenStream stream, TokenType... endingTokens) throws EngineException {
        int braces = 0, paren = 0, brackets = 0;

        List<Token> valueTokens = new ArrayList<>();

        while (stream.hasNext()) {
            Token token = stream.current();

            if (Arrays.asList(endingTokens).contains(token.getType()) && braces == 0 && paren == 0 && brackets == 0) {
                break;
            }

            if (token.getType() == TokenType.LBRACE) {
                braces++;
            } else if (token.getType() == TokenType.RBRACE) {
                braces--;
            } else if (token.getType() == TokenType.LPAREN) {
                paren++;
            } else if (token.getType() == TokenType.RPAREN) {
                paren--;
            } else if (token.getType() == TokenType.LBRACKET) {
                brackets++;
            } else if (token.getType() == TokenType.RBRACKET) {
                brackets--;
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
        stream.match(TokenType.LPAREN);

        stream.next();

        List<ArgumentDefinition> arguments = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.RPAREN) {
                break;
            } else {
                stream.match(TokenType.IDENTIFIER);

                ArgumentDefinition definition = new ArgumentDefinition();

                definition.setName(stream.current().getValue());

                stream.next();

                if (stream.current().getType() == TokenType.ASSIGN) {
                    stream.next();

                    definition.setDefaultValue(parseExpression(stream, TokenType.RPAREN, TokenType.COMMA));
                }

                if (stream.current().getType() != TokenType.RPAREN) {
                    stream.match(TokenType.COMMA);

                    stream.next();
                }

                arguments.add(definition);
            }
        }

        stream.match(TokenType.RPAREN);

        return arguments;
    }
}
