package passambler.parser;

import passambler.parser.statement.WhileStatement;
import passambler.parser.statement.LeaveStatement;
import passambler.parser.statement.IfStatement;
import passambler.parser.statement.TryStatement;
import passambler.parser.statement.ImportStatement;
import passambler.parser.statement.ForStatement;
import passambler.parser.statement.Statement;
import passambler.parser.statement.DeferStatement;
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
import passambler.module.Module;
import passambler.module.db.DbModule;
import passambler.module.file.FileModule;
import passambler.module.json.JsonModule;
import passambler.module.math.MathModule;
import passambler.module.net.NetModule;
import passambler.module.os.OsModule;
import passambler.module.regex.RegexModule;
import passambler.module.std.StdModule;
import passambler.module.std.value.InValue;
import passambler.module.std.value.OutValue;
import passambler.module.thread.ThreadModule;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenPosition;
import passambler.lexer.TokenType;
import passambler.util.ValueConstants;
import passambler.value.BooleanValue;
import passambler.value.Value;
import passambler.value.ErrorValue;
import passambler.value.function.CloseFunction;
import passambler.value.function.ReadFunction;
import passambler.value.function.ThrowFunction;
import passambler.value.function.WriteFunction;

public class Parser {
    private List<Statement> statements = new ArrayList<>();
    private List<Module> modules = new ArrayList<>();
    private Map<String, Value> globals = new HashMap();
    private List<List<Token>> defers = new ArrayList<>();

    private Scope scope;

    public Parser() {
        this(new Scope());
    }

    public Parser(Scope scope) {
        this.scope = scope;

        statements.add(new IfStatement());
        statements.add(new WhileStatement());
        statements.add(new ForStatement());
        statements.add(new LeaveStatement());
        statements.add(new TryStatement());
        statements.add(new DeferStatement());
        statements.add(new ImportStatement());

        modules.add(new StdModule());
        modules.add(new MathModule());
        modules.add(new FileModule());
        modules.add(new OsModule());
        modules.add(new NetModule());
        modules.add(new ThreadModule());
        modules.add(new JsonModule());
        modules.add(new RegexModule());
        modules.add(new DbModule());

        globals.put("true", ValueConstants.TRUE);
        globals.put("false", ValueConstants.FALSE);
        globals.put("nil", ValueConstants.NIL);
        globals.put("throw", new ThrowFunction());
        globals.put("write", new WriteFunction(new OutValue(), false));
        globals.put("writeln", new WriteFunction(new OutValue(), true));
        globals.put("read", new ReadFunction(new InValue(), false));
        globals.put("readln", new ReadFunction(new InValue(), true));
        globals.put("close", new CloseFunction());
    }

    public List<Module> getModules() {
        return modules;
    }

    public Map<String, Value> getGlobals() {
        return globals;
    }

    public List<List<Token>> getDefers() {
        return defers;
    }

    public Scope getScope() {
        return scope;
    }

    public Value parse(TokenList tokens) throws EngineException {
        for (Statement statement : statements) {
            if (statement.canPerform(this, tokens)) {
                return statement.perform(this, tokens);
            }
        }

        // Fall back to an expression
        new ExpressionParser(this, tokens).parse();

        return null;
    }

    public Value parse(Lexer lexer) throws EngineException {
        return parse(lexer.tokenize());
    }

    public Value parse(List<Token> tokens) throws EngineException {
        List<Token> section = new ArrayList<>();

        int depth = 0;

        for (int i = 0; i < tokens.size(); ++i) {
            Token token = tokens.get(i);
            Token peek = i + 1 < tokens.size() ? tokens.get(i + 1) : null;

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            section.add(token);

            if (depth == 0 && (token.getType() == TokenType.NEW_LINE || token.getType() == TokenType.RIGHT_BRACE || peek == null)) {
                if (peek != null && peek.getType().isAfterRightBrace()) {
                    continue;
                }

                if (section.size() > 0) {
                    Value result = parse(new TokenList(section));

                    section.clear();

                    if (result != null) {
                        // Unhandled error
                        if (result instanceof ErrorValue) {
                            throw new ErrorException((ErrorValue) result);
                        }

                        return result;
                    }
                }
            }
        }

        if (!section.isEmpty()) {
            throw new ParserException(ParserExceptionType.UNEXPECTED_EOF);
        }

        return null;
    }

    public Block parseBlock(TokenList tokens) throws EngineException {
        Block block = new Block(scope);

        tokens.match(TokenType.LEFT_BRACE, TokenType.ARROW);

        boolean arrow = tokens.current().getType() == TokenType.ARROW;

        tokens.next();

        int depth = 1;

        while (tokens.hasNext()) {
            if (!arrow) {
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

        if (!arrow) {
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

        while (tokens.current() != null && tokens.current().getType() != TokenType.RIGHT_PAREN) {
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

        tokens.match(TokenType.RIGHT_PAREN);

        return arguments;
    }
}
