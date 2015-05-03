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
import passambler.lexer.TokenList;
import passambler.bundle.Bundle;
import passambler.bundle.db.DbBundle;
import passambler.bundle.file.FileBundle;
import passambler.bundle.json.JsonBundle;
import passambler.bundle.math.MathBundle;
import passambler.bundle.net.NetBundle;
import passambler.bundle.os.OsBundle;
import passambler.bundle.regex.RegexBundle;
import passambler.bundle.std.StdBundle;
import passambler.bundle.std.value.OutValue;
import passambler.bundle.thread.ThreadBundle;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenPosition;
import passambler.lexer.TokenType;
import passambler.parser.feature.*;
import passambler.util.Constants;
import passambler.value.BooleanValue;
import passambler.value.Value;
import passambler.value.ErrorValue;
import passambler.value.function.CloseFunction;
import passambler.value.function.ReadFunction;
import passambler.value.function.ThrowFunction;
import passambler.value.function.UsingFunction;
import passambler.value.function.WriteFunction;

public class Parser {
    private List<Feature> features = new ArrayList<>();
    private List<Bundle> bundles = new ArrayList<>();
    private Map<String, Value> globals = new HashMap();

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

        bundles.add(new StdBundle());
        bundles.add(new MathBundle());
        bundles.add(new FileBundle());
        bundles.add(new OsBundle());
        bundles.add(new NetBundle());
        bundles.add(new ThreadBundle());
        bundles.add(new JsonBundle());
        bundles.add(new RegexBundle());
        bundles.add(new DbBundle());

        globals.put("true", Constants.VALUE_TRUE);
        globals.put("false", Constants.VALUE_FALSE);
        globals.put("nil", Constants.VALUE_NIL);
        globals.put("using", new UsingFunction());
        globals.put("throw", new ThrowFunction());
        globals.put("write", new WriteFunction(new OutValue(), false));
        globals.put("writeln", new WriteFunction(new OutValue(), true));
        globals.put("read", new ReadFunction(false));
        globals.put("readln", new ReadFunction(true));
        globals.put("close", new CloseFunction());
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    public Map<String, Value> getGlobals() {
        return globals;
    }

    public Scope getScope() {
        return scope;
    }

    public Value parse(TokenList tokens) throws EngineException {
        for (Feature feature : features) {
            if (feature.canPerform(this, tokens)) {
                return feature.perform(this, tokens);
            }
        }

        return null;
    }

    public Value parse(Lexer lexer) throws EngineException {
        return parse(lexer.tokenize());
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

                if (subTokens.size() > 0) {
                    Value result = parse(new TokenList(subTokens));

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

    public Block parseBlock(TokenList tokens) throws EngineException {
        Block block = new Block(scope);

        tokens.match(TokenType.LEFT_BRACE, TokenType.DOUBLE_ARROW);

        boolean equalArrow = tokens.current().getType() == TokenType.DOUBLE_ARROW;

        tokens.next();

        int depth = 1;

        while (tokens.hasNext()) {
            if (!equalArrow) {
                if (tokens.current().getType() == TokenType.LEFT_BRACE) {
                    depth++;
                } else if (tokens.current().getType() == TokenType.RIGHT_BRACE) {
                    depth--;

                    if (depth == 0) {
                        break;
                    }
                }
            }

            block.getTokens().add(tokens.current());

            tokens.next();
        }

        if (!equalArrow) {
            tokens.match(TokenType.RIGHT_BRACE);
        }

        return block;
    }

    public List<Token> parseExpressionTokens(TokenList tokens, TokenType... endingTokens) throws EngineException {
        int depth = 0;

        List<Token> valueTokens = new ArrayList<>();

        while (tokens.hasNext()) {
            Token token = tokens.current();

            if (Arrays.asList(endingTokens).contains(token.getType()) && depth == 0) {
                break;
            }

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            valueTokens.add(token);

            tokens.next();
        }

        return valueTokens;
    }

    public Value parseExpression(TokenList tokens, TokenType... endingTokens) throws EngineException {
        return new ExpressionParser(this, new TokenList(parseExpressionTokens(tokens, endingTokens))).parse();
    }

    public BooleanValue parseBooleanExpression(TokenList tokens, TokenType... endingTokens) throws EngineException {
        TokenPosition position = tokens.current().getPosition();

        Value value = parseExpression(tokens, endingTokens);

        if (!(value instanceof BooleanValue)) {
            throw new ParserException(ParserExceptionType.NOT_A_BOOLEAN, position);
        }

        return (BooleanValue) value;
    }

    public List<Argument> parseArgumentDefinition(TokenList tokens) throws EngineException {
        tokens.match(TokenType.LEFT_PAREN);

        tokens.next();

        List<Argument> arguments = new ArrayList<>();

        while (tokens.hasNext()) {
            if (tokens.current().getType() == TokenType.RIGHT_PAREN) {
                break;
            } else {
                tokens.match(TokenType.IDENTIFIER);

                Argument definition = new Argument();

                definition.setName(tokens.current().getValue());

                tokens.next();

                if (tokens.current().getType() == TokenType.ASSIGN) {
                    tokens.next();

                    definition.setDefaultValue(parseExpression(tokens, TokenType.RIGHT_PAREN, TokenType.COMMA));
                }

                if (tokens.current().getType() != TokenType.RIGHT_PAREN) {
                    tokens.match(TokenType.COMMA);

                    tokens.next();
                }

                arguments.add(definition);
            }
        }

        tokens.match(TokenType.RIGHT_PAREN);

        return arguments;
    }
}
