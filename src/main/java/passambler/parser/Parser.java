package passambler.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.function.FunctionUser;
import passambler.pack.Package;
import passambler.pack.file.PackageFile;
import passambler.pack.json.PackageJson;
import passambler.pack.math.PackageMath;
import passambler.pack.net.PackageNet;
import passambler.pack.os.PackageOs;
import passambler.pack.std.PackageStd;
import passambler.pack.thread.PackageThread;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class Parser {
    private List<Package> defaultPackages = new ArrayList<>();

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

    public List<Package> getDefaultPackages() {
        return defaultPackages;
    }

    public Scope getScope() {
        return scope;
    }

    public Value parse(TokenStream stream) throws ParserException {
        if (stream.first().getType() == Token.Type.IF) {
            stream.next();

            boolean elseCondition = false;

            Map<ValueBool, Block> cases = new LinkedHashMap();

            while (stream.hasNext()) {
                if (!elseCondition) {
                    stream.match(Token.Type.LPAREN);
                    stream.next();

                    List<Token> tokens = expressionTokens(stream, Token.Type.RPAREN);

                    stream.match(Token.Type.RPAREN);

                    Value condition = new ExpressionParser(this, new TokenStream(tokens)).parse();

                    if (!(condition instanceof ValueBool)) {
                        throw new ParserException(ParserException.Type.EXPECTED_A_BOOL, tokens.get(0).getPosition());
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
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.first().getPosition(), "else should be the last statement");
                    }

                    stream.match(Token.Type.ELSE, Token.Type.ELSEIF);

                    if (stream.current().getType() == Token.Type.ELSE) {
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
        } else if (stream.first().getType() == Token.Type.WHILE) {
            stream.next();

            stream.match(Token.Type.LPAREN);
            stream.next();

            List<Token> tokens = expressionTokens(stream, Token.Type.RPAREN);

            stream.match(Token.Type.RPAREN);

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
        } else if (stream.first().getType() == Token.Type.FOR) {
            stream.next();

            stream.match(Token.Type.LPAREN);

            int paren = 1;

            stream.next();

            List<Token> tokens = new ArrayList<>();

            List<String> arguments = new ArrayList<>();

            while (stream.hasNext()) {
                if (stream.rest().stream().anyMatch(t -> t.getType() == Token.Type.COL)) {
                    while (stream.hasNext()) {
                        arguments.add(stream.current().getValue());

                        stream.next();

                        if (stream.current().getType() == Token.Type.COL) {
                            stream.next();

                            break;
                        } else {
                            stream.match(Token.Type.COMMA);

                            stream.next();
                        }
                    }

                    continue;
                } else if (stream.current().getType() == Token.Type.LPAREN) {
                    paren++;
                } else if (stream.current().getType() == Token.Type.RPAREN) {
                    paren--;

                    if (paren == 0) {
                        break;
                    }
                }

                tokens.add(stream.current());

                stream.next();
            }

            stream.match(Token.Type.RPAREN);

            stream.next();

            Value value = new ExpressionParser(this, new TokenStream(tokens)).parse();

            Block callback = block(stream);

            if (value instanceof ValueList) {
                ValueList list = (ValueList) value;

                for (int i = 0; i < list.getValue().size(); ++i) {
                    if (arguments.size() == 1) {
                        callback.getParser().getScope().setSymbol(arguments.get(0), list.getValue().get(i));
                    } else if (arguments.size() == 2) {
                        callback.getParser().getScope().setSymbol(arguments.get(0), new ValueNum(i));
                        callback.getParser().getScope().setSymbol(arguments.get(1), list.getValue().get(i));
                    } else if (arguments.size() > 2) {
                        throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), 2, arguments.size());
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
                        throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), 2, arguments.size());
                    }

                    Value result = callback.invoke();

                    if (result != null) {
                        return result;
                    }
                }
            } else {
                throw new ParserException(ParserException.Type.CANNOT_ITERATE, stream.first().getPosition());
            }
        } else if (stream.first().getType() == Token.Type.RETURN) {
            stream.next();

            return new ExpressionParser(this, new TokenStream(stream.rest())).parse();
        } else if (stream.first().getType() == Token.Type.FN) {
            stream.next();

            stream.match(Token.Type.IDENTIFIER);

            String name = stream.current().getValue();

            stream.next();

            List<ArgumentDefinition> arguments = argumentDefinitions(stream);

            if (stream.peek() == null) {
                scope.setSymbol(name, new FunctionUser(new Block(scope), arguments));
            } else {
                stream.next();

                Block callback = block(stream);

                scope.setSymbol(name, new FunctionUser(callback, arguments));
            }
        } else if (AssignmentParser.isAssignment(stream.copyAtCurrentPosition())) {
            AssignmentParser assignmentParser = new AssignmentParser(this, stream);

            assignmentParser.parse();
        } else {
            new ExpressionParser(this, stream).parse();
        }

        return null;
    }

    public Value parse(Lexer lexer) throws LexerException, ParserException {
        return parse(lexer.scan());
    }

    public Value parse(List<Token> tokens) throws ParserException {
        List<Token> subTokens = new ArrayList<>();

        int braces = 0, paren = 0, brackets = 0;

        for (Token token : tokens) {
            Token peekToken = tokens.indexOf(token) == tokens.size() - 1 ? null : tokens.get(tokens.indexOf(token) + 1);

            if (token.getType() == Token.Type.LBRACE) {
                braces++;
            } else if (token.getType() == Token.Type.RBRACE) {
                braces--;
            } else if (token.getType() == Token.Type.LPAREN) {
                paren++;
            } else if (token.getType() == Token.Type.RPAREN) {
                paren--;
            } else if (token.getType() == Token.Type.LBRACKET) {
                brackets++;
            } else if (token.getType() == Token.Type.RBRACKET) {
                brackets--;
            }

            subTokens.add(token);

            if (braces == 0 && paren == 0 && brackets == 0 && (token.getType() == Token.Type.SEMI_COL || token.getType() == Token.Type.RBRACE)) {
                if (peekToken != null && (peekToken.getType() == Token.Type.ELSE || peekToken.getType() == Token.Type.ELSEIF || peekToken.getType().isOperator())) {
                    continue;
                }

                if (token.getType() == Token.Type.SEMI_COL) {
                    subTokens.remove(subTokens.size() - 1);
                }

                if (subTokens.size() > 0) {
                    Value result = parse(new TokenStream(subTokens));

                    subTokens.clear();

                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        if (!subTokens.isEmpty()) {
            throw new ParserException(ParserException.Type.UNEXPECTED_EOF);
        }

        return null;
    }

    public Block block(TokenStream stream) throws ParserException {
        Block block = new Block(scope);

        stream.match(Token.Type.LBRACE);

        stream.next();

        int braces = 1;

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.LBRACE) {
                braces++;
            } else if (stream.current().getType() == Token.Type.RBRACE) {
                braces--;

                if (braces == 0) {
                    break;
                }
            }

            block.getTokens().add(stream.current());

            stream.next();
        }

        stream.match(Token.Type.RBRACE);

        return block;
    }

    public List<Token> expressionTokens(TokenStream stream, Token.Type... endingTokens) throws ParserException {
        int braces = 0, paren = 0, brackets = 0;

        List<Token> valueTokens = new ArrayList<>();

        while (stream.hasNext()) {
            Token token = stream.current();

            if (Arrays.asList(endingTokens).contains(token.getType()) && braces == 0 && paren == 0 && brackets == 0) {
                break;
            }

            if (token.getType() == Token.Type.LBRACE) {
                braces++;
            } else if (token.getType() == Token.Type.RBRACE) {
                braces--;
            } else if (token.getType() == Token.Type.LPAREN) {
                paren++;
            } else if (token.getType() == Token.Type.RPAREN) {
                paren--;
            } else if (token.getType() == Token.Type.LBRACKET) {
                brackets++;
            } else if (token.getType() == Token.Type.RBRACKET) {
                brackets--;
            }

            valueTokens.add(token);

            stream.next();
        }

        return valueTokens;
    }

    public Value expression(TokenStream stream, Token.Type... endingTokens) throws ParserException {
        return new ExpressionParser(this, new TokenStream(expressionTokens(stream, endingTokens))).parse();
    }

    public List<ArgumentDefinition> argumentDefinitions(TokenStream stream) throws ParserException {
        stream.match(Token.Type.LPAREN);

        stream.next();

        List<ArgumentDefinition> arguments = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.RPAREN) {
                break;
            } else {
                stream.match(Token.Type.IDENTIFIER);

                ArgumentDefinition definition = new ArgumentDefinition();

                definition.setName(stream.current().getValue());

                stream.next();

                if (stream.current().getType() == Token.Type.ASSIGN) {
                    stream.next();

                    definition.setDefaultValue(expression(stream, Token.Type.RPAREN, Token.Type.COMMA));
                }

                if (stream.current().getType() != Token.Type.RPAREN) {
                    stream.match(Token.Type.COMMA);

                    stream.next();
                }

                arguments.add(definition);
            }
        }

        stream.match(Token.Type.RPAREN);

        return arguments;
    }
}
