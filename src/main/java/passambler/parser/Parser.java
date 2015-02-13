package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.IndexedValue;
import passambler.value.Value;
import passambler.value.ValueBlock;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class Parser {
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

    public Value parse(TokenStream stream) throws ParserException {
        if (stream.size() == 0) {
            return null;
        }
        
        if (stream.first().getType() == Token.Type.IDENTIFIER && stream.peek() != null && stream.peek().getType().isAssignmentOperator()) {
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
                scope.setSymbol(key, (Function) value);
            } else {
                scope.setSymbol(key, value);
            }
        } else if (stream.first().getType() == Token.Type.WHILE) {
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
            if (!stream.hasNext()) {
                return null;
            }

            stream.next();

            return Evaluator.evaluate(this, new TokenStream(stream.rest()));
        } else if (stream.first().getType() == Token.Type.FN) {
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
