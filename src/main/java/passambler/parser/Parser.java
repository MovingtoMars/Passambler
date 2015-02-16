package passambler.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import passambler.function.Function;
import passambler.function.FunctionSimple;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.IndexedValue;
import passambler.value.Value;
import passambler.value.ValueBlock;
import passambler.value.ValueBool;
import passambler.value.ValueClass;
import passambler.value.ValueNum;

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

        if (stream.first().getType() == Token.Type.CLASS) {
            if (!rules.isClassDeclarationAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }
            
            stream.next();
            
            stream.match(Token.Type.IDENTIFIER);
            
            String className = stream.current().getValue();
            
            ValueBlock block = new ValueBlock(null, new ArrayList());
            
            block.getParser().setParserRules(ParserRules.RULES_CLASS);
            block.getParser().getScope().addStd();
            
            stream.next();
            
            stream.match(Token.Type.LBRACE);
            
            int braces = 1;
            
            while (stream.hasNext()) {
                stream.next();
                
                if (stream.current().getType() == Token.Type.LBRACE) {
                    braces++;
                } else if (stream.current().getType() == Token.Type.RBRACE) {
                    braces--;
                }

                if (braces == 0) {
                    break;
                } else {
                    block.addToken(stream.current());
                }
            }
            
            ValueClass classValue = new ValueClass();
            
            block.invoke(null, new Value[] {});
            
            for (Map.Entry<String, Value> entry : block.getParser().getScope().getSymbols().entrySet()) {
                if (entry.getKey().equals(className)) {
                    classValue.setConstructor((Function) entry.getValue());
                } else {
                    classValue.setProperty(entry.getKey(), entry.getValue());
                }
            }
            
            scope.setSymbol(className, new Function() {
                @Override
                public int getArguments() {
                    if (classValue.getConstructor() != null) {
                        return classValue.getConstructor().getArguments();
                    }
                    
                    return 0;
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    if (classValue.getConstructor() != null) {
                        return classValue.getConstructor().isArgumentValid(value, argument);
                    }
                    
                    return false;
                }

                @Override
                public Value invoke(Parser parser, Value... arguments) throws ParserException {
                    if (classValue.getConstructor() != null) {
                        classValue.getConstructor().invoke(parser, arguments);
                    }
                    
                    return classValue;
                }
            });
        } else if (stream.first().getType() == Token.Type.IF) {
            if (!rules.isIfStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }
            
            List<Token> tokens = new ArrayList<>();

            Map<ValueBool, ValueBlock> cases = new HashMap();

            ValueBool currentCondition = null;
            ValueBlock currentConditionBlock = null;

            boolean condition = true;
            
            boolean elseCondition = false;

            int braces = 0;

            stream.next();

            while (stream.hasNext()) {
                if (condition) {
                    if (stream.current().getType() == Token.Type.LBRACE) {
                        condition = false;

                        if (!elseCondition) {
                            Value value = Evaluator.evaluate(this, new TokenStream(tokens));
                            
                            if (!(value instanceof ValueBool)) {
                                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "expected a bool");
                            }
                            
                            currentCondition = (ValueBool) value;
                        } else {
                            currentCondition = new ValueBool(true);
                        }

                        currentConditionBlock = new ValueBlock(scope, new ArrayList());

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
                        currentConditionBlock.addToken(stream.current());
                    }
                }

                stream.next();
            }

            for (Map.Entry<ValueBool, ValueBlock> entry : cases.entrySet()) {
                if (entry.getKey().getValue() == true) {
                    Value result = entry.getValue().invoke(this, new Value[] {});

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

            Value value = baseValue.onOperator(Evaluator.evaluate(this, new TokenStream(stream.rest())), operatorToken.getType());

            if (value == null) {
                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, operatorToken.getPosition(), operatorToken.getType());
            }

            if (value instanceof ValueBlock) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "cannot declare a block");
            } else {
                scope.setSymbol(key, value);
            }
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

            Value value = Evaluator.evaluate(this, new TokenStream(tokens));

            Value callbackValue = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (!(callbackValue instanceof ValueBlock)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "callback should be a block");
            }

            ValueBlock callback = (ValueBlock) callbackValue;

            if (!(value instanceof ValueBool)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "expecting bool");
            }

            while (((ValueBool) value).getValue()) {
                Value result = callback.invoke(this, new Value[] {});

                if (result != null) {
                    return result;
                }

                value = Evaluator.evaluate(this, new TokenStream(tokens));
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

            stream.match(Token.Type.LBRACE);

            Value callbackValue = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (!(callbackValue instanceof ValueBlock)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "callback should be a block");
            }

            ValueBlock callback = (ValueBlock) callbackValue;

            callback.getArgumentNames().addAll(arguments);

            Value value = Evaluator.evaluate(this, new TokenStream(tokens));

            if (!(value instanceof IndexedValue)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }

            IndexedValue indexedValue = (IndexedValue) value;

            if (callback.getArgumentNames().size() > 2) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "invalid argument count expected");
            }

            for (int i = 0; i < indexedValue.getIndexCount(); ++i) {
                Value result = callback.invoke(this, new Value[] { indexedValue.getIndex(i), new ValueNum(i) });

                if (result != null) {
                    return result;
                }
            }
        } else if (stream.first().getType() == Token.Type.RETURN) {
            if (!rules.isReturnStatementAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }
            
            if (!stream.hasNext()) {
                return null;
            }

            stream.next();

            return Evaluator.evaluate(this, new TokenStream(stream.rest()));
        } else if (stream.first().getType() == Token.Type.FN) {
            if (!rules.isFunctionDeclarationAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }
            
            stream.next();

            stream.match(Token.Type.IDENTIFIER);

            String name = stream.current().getValue();

            stream.next();

            List<String> arguments = new ArrayList<>();

            if (stream.current().getType() == Token.Type.COL) {
                while (stream.hasNext()) {
                    stream.next();

                    if (stream.current().getType() == Token.Type.LBRACE) {
                        break;
                    } else {
                        stream.match(Token.Type.IDENTIFIER);

                        arguments.add(stream.current().getValue());

                        if (stream.peek().getType() != Token.Type.LBRACE) {
                            stream.match(Token.Type.COMMA);
                        }
                    }
                }
            }

            stream.match(Token.Type.LBRACE);

            Value callbackValue = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (!(callbackValue instanceof ValueBlock)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "callback should be a block");
            }

            ValueBlock callback = (ValueBlock) callbackValue;

            callback.getArgumentNames().addAll(arguments);

            scope.setSymbol(name, callback);
        } else {
            if (!rules.isEvaluationAllowed()) {
                throw new ParserException(ParserException.Type.NOT_ALLOWED, stream.first().getPosition());
            }
            
            return Evaluator.evaluate(this, stream);
        }

        return null;
    }

    public Value parseLexer(Lexer scanner) throws LexerException, ParserException {
        return parseLines(scanner.scan());
    }

    public Value parseLines(List<Token> tokens) throws ParserException {
        List<Token> subTokens = new ArrayList<>();

        int braces = 0;

        for (Token token : tokens) {
            if (token.getType() == Token.Type.LBRACE) {
                braces++;
            } else if (token.getType() == Token.Type.RBRACE) {
                braces--;
            }

            subTokens.add(token);

            if (braces == 0 && (token.getType() == Token.Type.NEW_LINE || token == tokens.get(tokens.size() - 1))) {
                if (token.getType() == Token.Type.NEW_LINE) {
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
}
