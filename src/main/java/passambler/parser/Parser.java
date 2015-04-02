package passambler.parser;

import passambler.exception.ParserException;
import passambler.exception.ErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.function.FunctionUser;
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
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueClass;
import passambler.value.function.FunctionClassInitializer;
import passambler.value.ValueDict;
import passambler.value.ValueError;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class Parser {
    private List<Package> defaultPackages = new ArrayList<>();

    private Block catchBlock;
    private String catchErrorName;

    private Scope scope;

    public Parser() {
        this(new Scope());
    }

    public Parser(Scope scope) {
        this.scope = scope;

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
            if (stream.first().getType() == TokenType.IF) {
                stream.next();

                boolean elseCondition = false;

                Map<ValueBool, Block> cases = new LinkedHashMap();

                while (stream.hasNext()) {
                    if (!elseCondition) {
                        stream.match(TokenType.LPAREN);
                        stream.next();

                        List<Token> tokens = expressionTokens(stream, TokenType.RPAREN);

                        stream.match(TokenType.RPAREN);

                        Value condition = new ExpressionParser(this, new TokenStream(tokens)).parse();

                        if (!(condition instanceof ValueBool)) {
                            throw new ParserException(ParserExceptionType.EXPECTED_A_BOOL, tokens.get(0).getPosition());
                        }

                        stream.next();

                        cases.put((ValueBool) condition, block(stream));

                        tokens.clear();
                    } else {
                        cases.put(new ValueBool(true), block(stream));
                    }

                    stream.next();

                    if (stream.current() != null) {
                        if (elseCondition) {
                            throw new ParserException(ParserExceptionType.BAD_SYNTAX, stream.first().getPosition(), "else should be the last statement");
                        }

                        stream.match(TokenType.ELSE, TokenType.ELSEIF);

                        if (stream.current().getType() == TokenType.ELSE) {
                            elseCondition = true;
                        }

                        stream.next();
                    }
                }

                for (Map.Entry<ValueBool, Block> entry : cases.entrySet()) {
                    if (entry.getKey().getValue() == true) {
                        Value result = entry.getValue().invoke();

                        if (result != null) {
                            return result;
                        }

                        break;
                    }
                }
            } else if (stream.first().getType() == TokenType.WHILE) {
                stream.next();

                stream.match(TokenType.LPAREN);
                stream.next();

                List<Token> tokens = expressionTokens(stream, TokenType.RPAREN);

                stream.match(TokenType.RPAREN);

                stream.next();

                Value value = new ExpressionParser(this, new TokenStream(tokens)).parse();

                Block callback = block(stream);

                while (((ValueBool) value).getValue()) {
                    Value result = callback.invoke();

                    if (result != null) {
                        return result;
                    }

                    value = new ExpressionParser(this, new TokenStream(tokens)).parse();
                }
            } else if (stream.first().getType() == TokenType.FOR) {
                List<String> arguments = new ArrayList<>();

                stream.next();
                stream.match(TokenType.LPAREN);

                stream.next();

                TokenStream left = new TokenStream(expressionTokens(stream, TokenType.RPAREN, TokenType.COL));
                Value right = null;

                if (stream.current().getType() == TokenType.COL) {
                    stream.next();

                    right = expression(stream, TokenType.RPAREN);

                    stream.match(TokenType.RPAREN);
                }

                stream.next();

                Block callback = block(stream);

                if (right != null) {
                    while (left.hasNext()) {
                        left.match(TokenType.IDENTIFIER);

                        arguments.add(left.current().getValue());

                        if (left.peek() != null) {
                            left.next();

                            left.match(TokenType.COMMA);
                        }

                        left.next();
                    }
                }

                Value value = right == null ? new ExpressionParser(this, left.copy()).parse() : right;

                if (value instanceof ValueList) {
                    ValueList list = (ValueList) value;

                    for (int i = 0; i < list.getValue().size(); ++i) {
                        if (arguments.size() == 1) {
                            callback.getParser().getScope().setSymbol(arguments.get(0), list.getValue().get(i));
                        } else if (arguments.size() == 2) {
                            callback.getParser().getScope().setSymbol(arguments.get(0), new ValueNum(i));
                            callback.getParser().getScope().setSymbol(arguments.get(1), list.getValue().get(i));
                        } else if (arguments.size() > 2) {
                            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), 2, arguments.size());
                        }

                        Value result = callback.invoke();

                        if (result != null) {
                            return result;
                        }
                    }
                } else if (value instanceof ValueDict) {
                    ValueDict dict = (ValueDict) value;

                    for (Map.Entry<Value, Value> entry : dict.getValue().entrySet()) {
                        if (arguments.size() == 1) {
                            callback.getParser().getScope().setSymbol(arguments.get(0), entry.getValue());
                        } else if (arguments.size() == 2) {
                            callback.getParser().getScope().setSymbol(arguments.get(0), entry.getKey());
                            callback.getParser().getScope().setSymbol(arguments.get(1), entry.getValue());
                        } else if (arguments.size() > 2) {
                            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), 2, arguments.size());
                        }

                        Value result = callback.invoke();

                        if (result != null) {
                            return result;
                        }
                    }
                } else {
                    throw new ParserException(ParserExceptionType.CANNOT_ITERATE, stream.first().getPosition());
                }
            } else if (stream.first().getType() == TokenType.RETURN) {
                stream.next();

                return new ExpressionParser(this, new TokenStream(stream.rest())).parse();
            } else if (stream.first().getType() == TokenType.FN) {
                stream.next();

                stream.match(TokenType.IDENTIFIER);

                String name = stream.current().getValue();

                stream.next();

                List<ArgumentDefinition> arguments = argumentDefinitions(stream);

                if (stream.peek() == null) {
                    scope.setSymbol(name, new FunctionUser(null, arguments));
                } else {
                    stream.next();

                    Block callback = block(stream);

                    scope.setSymbol(name, new FunctionUser(callback, arguments));
                }
            } else if (stream.first().getType() == TokenType.CLASS) {
                stream.next();
                stream.match(TokenType.IDENTIFIER);

                String name = stream.current().getValue();

                stream.next();

                List<ArgumentDefinition> arguments = argumentDefinitions(stream);

                stream.next();

                List<FunctionClassInitializer> parents = new ArrayList<>();

                if (stream.current().getType() == TokenType.COL) {
                    stream.next();

                    while (stream.current().getType() != TokenType.LBRACE) {
                        Value expression = expression(stream, TokenType.COMMA, TokenType.RPAREN, TokenType.LBRACE);

                        if (!(expression instanceof ValueClass)) {
                            throw new ParserException(ParserExceptionType.NOT_A_CLASS, stream.current().getPosition());
                        }

                        // Here we add the class initializer (the name of the symbol is the name of the class expression)
                        parents.add((FunctionClassInitializer) scope.getSymbol(((ValueClass) expression).getName()));
                    }
                }

                FunctionClassInitializer child = new FunctionClassInitializer(name, block(stream), arguments);

                for (FunctionClassInitializer parent : parents) {
                    child.addParent(parent);
                }

                scope.setSymbol(name, child);
            } else if (stream.first().getType() == TokenType.TRY) {
                stream.next();

                Block tryBlock = block(stream);

                stream.next();

                stream.match(TokenType.CATCH);
                stream.next();

                stream.match(TokenType.LPAREN);
                stream.next();

                stream.match(TokenType.IDENTIFIER);
                String name = stream.current().getValue();

                stream.next();
                stream.match(TokenType.RPAREN);

                stream.next();

                tryBlock.getParser().setCatch(block(stream), name);

                Value result = tryBlock.invoke();

                if (result != null) {
                    return result;
                }
            } else if (AssignmentParser.isAssignment(stream.copyAtCurrentPosition())) {
                AssignmentParser assignmentParser = new AssignmentParser(this, stream);

                assignmentParser.parse();
            } else {
                new ExpressionParser(this, stream).parse();
            }
        } catch (ErrorException e) {
            if (catchBlock != null) {
                catchBlock.getParser().getScope().setSymbol(catchErrorName, e.getError());

                return catchBlock.invoke();
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

    public Block block(TokenStream stream) throws EngineException {
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

    public List<Token> expressionTokens(TokenStream stream, TokenType... endingTokens) throws EngineException {
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

    public Value expression(TokenStream stream, TokenType... endingTokens) throws EngineException {
        return new ExpressionParser(this, new TokenStream(expressionTokens(stream, endingTokens))).parse();
    }

    public List<ArgumentDefinition> argumentDefinitions(TokenStream stream) throws EngineException {
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

                    definition.setDefaultValue(expression(stream, TokenType.RPAREN, TokenType.COMMA));
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
