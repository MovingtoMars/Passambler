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
    private boolean interactiveMode = false;

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

    public void setInInteractiveMode(boolean mode) {
        interactiveMode = mode;
    }

    public boolean isInInteractiveMode() {
        return interactiveMode;
    }

    public Value parse(TokenStream stream) throws ParserException {
        if (isAssignment(stream.copy())) {           
            String key = stream.current().getValue();

            stream.next();
            
            boolean locked = false;
            
            if (stream.current().getType() == Token.Type.EXCL) {
                locked = true;
                
                stream.next();
            }
            
            stream.next();

            Value value = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (locked) {
                value.lock();
            }

            if (value instanceof ValueBlock) {
                scope.setSymbol(key, (Function) value);
            } else {
                scope.setSymbol(key, value);
            }
        } else if (isWhile(stream.copy())) {
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
                callback.invoke(this, new Value[] {});
                
                value = Evaluator.evaluate(this, new TokenStream(tokens));
            }
        } else if (isFor(stream.copy())) {
            stream.next();

            List<Token> tokens = new ArrayList<>();

            List<String> arguments = new ArrayList<>();

            if (stream.peek().getType() == Token.Type.IN) {
                arguments.add(stream.current().getValue());
                
                stream.next();
                stream.next();
            } else if (stream.peek(3).getType() == Token.Type.IN) {
                arguments.add(stream.current().getValue());
                
                stream.next();
                stream.next();
                
                arguments.add(stream.current().getValue());
                
                stream.next();
                stream.next();
            }
            
            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE) {
                    break;
                }

                tokens.add(stream.current());

                stream.next();
            }

            if (stream.current().getType() != Token.Type.LBRACE) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.back().getPosition(), "missing brace");
            }

            Value callbackValue = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (!(callbackValue instanceof ValueBlock)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "callback should be a block");
            }

            ValueBlock callback = (ValueBlock) callbackValue;

            callback.getArgumentNames().addAll(arguments);

            Value val = Evaluator.evaluate(this, new TokenStream(tokens));

            if (!(val instanceof IndexedValue)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }

            IndexedValue indexedValue = (IndexedValue) val;

            if (callback.getArgumentNames().size() > 2) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "invalid argument count expected");
            }

            for (int i = 0; i < indexedValue.getIndexCount(); ++i) {
                callback.invoke(this, new Value[]{
                    indexedValue.getIndex(i),
                    new ValueNum(i)
                });
            }
        } else if (isReturn(stream.copy())) {
            if (!stream.hasNext()) {
                return null;
            }

            stream.next();

            return Evaluator.evaluate(this, new TokenStream(stream.rest()));
        } else {
            Value value = Evaluator.evaluate(this, stream);

            if (isInInteractiveMode() && value != null) {
                System.out.println(value);
            }
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

        return Value.nil;
    }

    public boolean isReturn(TokenStream stream) {
        return stream.size() >= 1 && stream.first().getType() == Token.Type.RETURN;
    }

    public boolean isFor(TokenStream stream) {
        return stream.size() >= 5 && stream.first().getType() == Token.Type.FOR;
    }

    public boolean isWhile(TokenStream stream) {
        return stream.size() >= 5 && stream.first().getType() == Token.Type.WHILE;
    }

    public boolean isAssignment(TokenStream stream) {
        return stream.size() >= 3 && stream.current().getType() == Token.Type.IDENTIFIER && stream.peek(stream.peek().getType() == Token.Type.EXCL ? 2 : 1).getType() == Token.Type.ASSIGN;
    }
}
