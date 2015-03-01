package passambler.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.function.Function;
import passambler.pkg.file.PackageFile;
import passambler.pkg.http.PackageHttp;
import passambler.pkg.math.PackageMath;
import passambler.pkg.net.PackageNet;
import passambler.pkg.os.PackageOs;
import passambler.pkg.std.PackageStd;
import passambler.pkg.thread.PackageThread;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class Parser {
    private Map<String, passambler.pkg.Package> internalPackages = new HashMap<>();

    private String packageName;

    private Scope scope;

    public Parser() {
        this(new Scope());
    }

    public Parser(Scope scope) {
        this.scope = scope;

        internalPackages.put("std", new PackageStd());
        internalPackages.put("math", new PackageMath());
        internalPackages.put("file", new PackageFile());
        internalPackages.put("os", new PackageOs());
        internalPackages.put("net", new PackageNet());
        internalPackages.put("thread", new PackageThread());
        internalPackages.put("http", new PackageHttp());
    }

    public Scope getScope() {
        return scope;
    }

    public String getPackageName() {
        return packageName;
    }

    public Value parse(TokenStream stream) throws ParserException {
        if (stream.first().getType() == Token.Type.PACKAGE && stream.peek().getType() == Token.Type.IDENTIFIER) {
            packageName = stream.peek().getValue();
        } else if (stream.first().getType() == Token.Type.IMPORT) {
            stream.next();

            stream.match(Token.Type.IDENTIFIER);

            String name = stream.current().getValue();

            String symbolName = null, symbolRename = null;

            boolean importAll = false;

            if (stream.peek() != null && stream.peek().getType() == Token.Type.PERIOD) {
                stream.next();
                stream.next();

                stream.match(Token.Type.IDENTIFIER, Token.Type.MULTIPLY);

                if (stream.current().getType() == Token.Type.MULTIPLY) {
                    importAll = true;
                } else {
                    symbolName = stream.current().getValue();
                }
            }

            if (stream.peek() != null && stream.peek().getType() == Token.Type.ASSIGN) {
                if (importAll) {
                    throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "cannot rename symbol when importing the whole package");
                }

                stream.next();
                stream.next();

                stream.match(Token.Type.IDENTIFIER);

                symbolRename = stream.current().getValue();
            }

            if (stream.peek() != null) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "end of statement expected");
            }

            Value packageValue = new Value();

            Map<String, Value> symbols = new HashMap();

            if (internalPackages.containsKey(name)) {
                internalPackages.get(name).addSymbols(symbols);
            } else {
                Parser parser = new Parser();

                try {
                    parser.parse(new Lexer(String.join("\n", Files.readAllLines(Paths.get(name)))));
                } catch (IOException | LexerException | ParserException e) {
                    throw new RuntimeException(e);
                }

                symbols = parser.getScope().getSymbols();

                name = parser.getPackageName();
            }

            for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                if (Character.isUpperCase(symbol.getKey().charAt(0))) {
                    if (symbolName != null) {
                        if (symbol.getKey().equals(symbolName)) {
                            scope.setSymbol(symbolRename != null ? symbolRename : symbol.getKey(), symbol.getValue());
                        }
                    } else if (importAll) {
                        scope.setSymbol(symbol.getKey(), symbol.getValue());
                    } else {
                        packageValue.setProperty(symbol.getKey(), symbol.getValue());
                    }
                }
            }

            scope.setSymbol(symbolName == null && symbolRename != null ? symbolRename : name, packageValue);
        } else if (stream.first().getType() == Token.Type.IF) {
            stream.next();

            boolean elseCondition = false;

            Map<ValueBool, Block> cases = new HashMap();

            List<Token> tokens = new ArrayList<>();

            while (stream.hasNext()) {
                if (!elseCondition) {
                    stream.match(Token.Type.LPAREN);

                    stream.next();

                    int paren = 1;

                    while (stream.hasNext()) {
                        if (stream.current().getType() == Token.Type.LPAREN) {
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

            int paren = 1;

            stream.next();

            List<Token> tokens = new ArrayList<>();

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LPAREN) {
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
                if (stream.current().getType() == Token.Type.IDENTIFIER && stream.peek().getType() != Token.Type.RPAREN) {
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
        } else if (stream.first().getType() == Token.Type.FN && stream.peek() != null && stream.peek().getType() == Token.Type.IDENTIFIER) {
            stream.next();

            stream.match(Token.Type.IDENTIFIER);

            String name = stream.current().getValue();

            stream.next();

            List<String> argumentNames = argumentNames(stream);

            stream.next();

            Block callback = block(stream);

            scope.setSymbol(name, new Function() {
                @Override
                public int getArguments() {
                    return argumentNames.size();
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    return argument < argumentNames.size();
                }

                @Override
                public Value invoke(Parser parser, Value... arguments) throws ParserException {
                    for (int i = 0; i < argumentNames.size(); ++i) {
                        callback.getParser().getScope().setSymbol(argumentNames.get(i), arguments[i]);
                    }

                    return callback.invoke();
                }
            });
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

    public List<String> argumentNames(TokenStream stream) throws ParserException {
        stream.match(Token.Type.LPAREN);

        stream.next();

        List<String> arguments = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.RPAREN) {
                break;
            } else {
                stream.match(Token.Type.IDENTIFIER);

                arguments.add(stream.current().getValue());

                if (stream.peek().getType() != Token.Type.RPAREN) {
                    stream.next();

                    stream.match(Token.Type.COMMA);
                }

                stream.next();
            }
        }

        stream.match(Token.Type.RPAREN);

        return arguments;
    }
}
