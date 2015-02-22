package passambler.parser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import passambler.Main;
import passambler.extension.Extension;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.function.Function;
import passambler.value.IndexedValue;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class Parser {
    private ParserRules rules = ParserRules.RULES_NONE;

    private Scope scope;

    public Parser() {
        this(new Scope());
    }

    public Parser(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public ParserRules getParserRules() {
        return rules;
    }

    public void setParserRules(ParserRules rules) {
        this.rules = rules;
    }

    public Value parse(TokenStream stream) throws ParserException {
        if (stream.size() == 0) {
            return null;
        }

        if (stream.first().getType() == Token.Type.IMPORT) {
            if (!rules.isImportStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            stream.next();
            
            if (stream.current().getType() == Token.Type.IDENTIFIER) {
                String name = stream.current().getValue();
                
                Extension extension = Main.EXTENSIONS.stream().filter(ext -> ext.getId().equals(name)).findFirst().orElseThrow(() -> new ParserException(ParserException.Type.UNDEFINED_EXTENSION, stream.current().getPosition(), name));
                
                Map<String, Value> symbols = new HashMap();
                
                extension.addSymbols(scope, symbols);
                
                Value extensionValue = new Value();
                
                for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                    extensionValue.setProperty(symbol.getKey(), symbol.getValue());
                }
                
                scope.setSymbol(name, extensionValue);
            } else {
                Value value = new ExpressionParser(this, new TokenStream(stream.rest())).parse();

                if (!(value instanceof ValueStr)) {
                    throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "expected string");
                }

                try {
                    parse(new Lexer(String.join("\n", Files.readAllLines(Paths.get(((ValueStr) value).getValue())))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (stream.first().getType() == Token.Type.IF) {
            if (!rules.isIfStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            List<Token> tokens = new ArrayList<>();

            Map<ValueBool, Block> cases = new HashMap();

            ValueBool currentCondition = null;
            Block currentConditionBlock = null;

            boolean condition = true;

            boolean elseCondition = false;

            int braces = 0;

            stream.next();

            while (stream.hasNext()) {
                if (condition) {
                    if (stream.current().getType() == Token.Type.LBRACE) {
                        condition = false;

                        if (!elseCondition) {
                            Value value = new ExpressionParser(this, new TokenStream(tokens)).parse();

                            if (!(value instanceof ValueBool)) {
                                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "expected a bool");
                            }

                            currentCondition = (ValueBool) value;
                        } else {
                            currentCondition = new ValueBool(true);
                        }

                        currentConditionBlock = new Block(scope);

                        braces = 1;

                        tokens.clear();
                    } else {
                        tokens.add(stream.current());
                    }
                } else {
                    if (stream.current().getType() == Token.Type.LBRACE) {
                        braces++;
                    } else if (stream.current().getType() == Token.Type.RBRACE) {
                        braces--;
                    }

                    if (braces == 0) {
                        cases.put(currentCondition, currentConditionBlock);

                        currentCondition = null;
                        currentConditionBlock = null;
                        condition = true;

                        if (stream.peek() != null) {
                            if (elseCondition == true) {
                                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "else should be the last statement");
                            }

                            stream.next();

                            stream.match(Token.Type.ELSE, Token.Type.ELSEIF);

                            if (stream.current().getType() == Token.Type.ELSE) {
                                elseCondition = true;
                            }
                        }
                    } else {
                        currentConditionBlock.getTokens().add(stream.current());
                    }
                }

                stream.next();
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
            if (!rules.isVariableAssignmentAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

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
            if (!rules.isWhileStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            stream.next();

            List<Token> tokens = new ArrayList<>();

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE) {
                    break;
                }

                tokens.add(stream.current());

                stream.next();
            }

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
            if (!rules.isForStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            stream.next();

            List<Token> tokens = new ArrayList<>();

            List<String> arguments = new ArrayList<>();

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE) {
                    break;
                } else if (stream.current().getType() == Token.Type.COL) {
                    stream.next();

                    while (stream.current().getType() != Token.Type.LBRACE) {
                        stream.match(Token.Type.IDENTIFIER);

                        arguments.add(stream.current().getValue());

                        if (stream.peek().getType() != Token.Type.LBRACE) {
                            stream.next();

                            stream.match(Token.Type.COMMA);
                        }

                        stream.next();
                    }
                } else {
                    tokens.add(stream.current());

                    stream.next();
                }
            }

            Block callback = block(stream);

            Value value = new ExpressionParser(this, new TokenStream(tokens)).parse();

            if (!(value instanceof IndexedValue)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }

            IndexedValue indexedValue = (IndexedValue) value;

            for (int i = 0; i < indexedValue.getIndexCount(); ++i) {
                callback.getParser().getScope().setSymbol(arguments.size() >= 1 ? arguments.get(0) : "_", indexedValue.getIndex(new ValueNum(i)));
                callback.getParser().getScope().setSymbol(arguments.size() >= 2 ? arguments.get(1) : "__", new ValueNum(i));

                Value result = callback.invoke();

                if (result != null) {
                    return result;
                }
            }
        } else if (stream.first().getType() == Token.Type.RETURN) {
            if (!rules.isReturnStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            stream.next();

            return new ExpressionParser(this, new TokenStream(stream.rest())).parse();
        } else if (stream.first().getType() == Token.Type.FN) {
            if (!rules.isFunctionDeclarationAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

            stream.next();
            
            stream.match(Token.Type.IDENTIFIER);
            String name = stream.current().getValue();
            
            stream.next();

            List<String> argumentIds = new ArrayList<>();

            if (stream.current().getType() == Token.Type.COL) {
                while (stream.hasNext()) {
                    stream.next();

                    if (stream.current().getType() == Token.Type.LBRACE) {
                        break;
                    } else {
                        stream.match(Token.Type.IDENTIFIER);

                        argumentIds.add(stream.current().getValue());

                        if (stream.peek().getType() != Token.Type.LBRACE) {
                            stream.next();

                            stream.match(Token.Type.COMMA);
                        }
                    }
                }
            }

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
            if (!rules.isEvaluationAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }

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
                if (peekToken != null && (peekToken.getType() == Token.Type.ELSE || peekToken.getType() == Token.Type.ELSEIF)) {
                    continue;
                }

                if (token.getType() == Token.Type.SEMI_COL) {
                    subTokens.remove(subTokens.size() - 1);
                }

                Value result = parse(new TokenStream(subTokens));

                subTokens.clear();

                if (result != null) {
                    return result;
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
