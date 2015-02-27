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
import passambler.pkg.math.PackageMath;
import passambler.pkg.net.PackageNet;
import passambler.pkg.os.PackageOs;
import passambler.pkg.std.PackageStd;
import passambler.value.IndexedValue;
import passambler.value.Value;
import passambler.value.ValueBool;
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
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, tokens.get(0).getPosition(), "condition should be a bool");
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
        } else if (stream.first().getType() == Token.Type.IDENTIFIER && stream.peek() != null && stream.peek().getType().isAssignmentOperator()) {
            String key = stream.current().getValue();

            stream.next();

            Token operatorToken = stream.current();

            stream.next();

            Value baseValue = new Value();

            if (scope.hasSymbol(key)) {
                baseValue = scope.getSymbol(key);
            }

            Value value = baseValue.onOperator(new ExpressionParser(this, new TokenStream(stream.rest())).parse(), operatorToken.getType());

            if (value == null) {
                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, operatorToken.getPosition(), operatorToken.getType());
            }

            scope.setSymbol(key, value);
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

            String variableName = null;

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.IDENTIFIER && stream.peek().getType() == Token.Type.COL) {
                    variableName = stream.current().getValue();

                    stream.next();
                    stream.next();

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

            if (!(value instanceof IndexedValue)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }

            IndexedValue indexedValue = (IndexedValue) value;

            for (int i = 0; i < indexedValue.getIndexCount(); ++i) {
                if (variableName != null) {
                    callback.getParser().getScope().setSymbol(variableName, indexedValue.getIndex(new ValueNum(i)));
                }

                Value result = callback.invoke();

                if (result != null) {
                    return result;
                }
            }
        } else if (stream.first().getType() == Token.Type.RETURN) {
            stream.next();

            return new ExpressionParser(this, new TokenStream(stream.rest())).parse();
        } else if (stream.first().getType() == Token.Type.FN) {
            stream.next();

            stream.match(Token.Type.IDENTIFIER);

            String name = stream.current().getValue();

            stream.next();

            stream.match(Token.Type.LPAREN);

            stream.next();

            List<String> argumentIds = new ArrayList<>();

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.RPAREN) {
                    break;
                } else {
                    stream.match(Token.Type.IDENTIFIER);

                    argumentIds.add(stream.current().getValue());

                    if (stream.peek().getType() != Token.Type.RPAREN) {
                        stream.next();

                        stream.match(Token.Type.COMMA);
                    }

                    stream.next();
                }
            }

            stream.match(Token.Type.RPAREN);

            stream.next();

            Block callback = block(stream);

            scope.setSymbol(name, new Function() {
                @Override
                public int getArguments() {
                    return argumentIds.size();
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    return argument < argumentIds.size();
                }

                @Override
                public Value invoke(Parser parser, Value... arguments) throws ParserException {
                    for (int i = 0; i < argumentIds.size(); ++i) {
                        callback.getParser().getScope().setSymbol(argumentIds.get(i), arguments[i]);
                    }

                    return callback.invoke();
                }
            });
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

        int braces = 0, paren = 0;

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
            }

            subTokens.add(token);

            if (braces == 0 && paren == 0 && (token.getType() == Token.Type.SEMI_COL || token.getType() == Token.Type.RBRACE)) {
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

    private Block block(TokenStream stream) throws ParserException {
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
}
