package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.scanner.Scanner;
import passambler.scanner.ScannerException;
import passambler.scanner.Token;
import passambler.scanner.TokenStream;
import passambler.val.IndexAccess;
import passambler.val.Val;
import passambler.val.ValBlock;
import passambler.val.ValBool;
import passambler.val.ValNum;

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

    public void parse(TokenStream stream) throws ParserException {
        if (isAssignment(stream.copy())) {
            String key = stream.current().getStringValue();

            stream.next();
            
            boolean locked = stream.current().getType() == Token.Type.ASSIGN_LOCKED;
            
            stream.next();

            Val value = Evaluator.evaluate(this, new TokenStream(stream.rest()));

            if (locked) {
                value.lock();
            }
            
            if (value instanceof ValBlock) {
                scope.setSymbol(key, (Function) value);
            } else {
                scope.setSymbol(key, value);
            }
        } else if (isFor(stream.copy())) {
            stream.next();
            
            List<Token> iteratorTokens = new ArrayList<>();
            
            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE || stream.current().getType() == Token.Type.PIPE) {
                    break;
                }
                
                iteratorTokens.add(stream.current());
                
                stream.next();
            }
            
            Val iteratorVal = Evaluator.evaluate(this, new TokenStream(iteratorTokens));
            
            if (!(iteratorVal instanceof IndexAccess)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }
            
            IndexAccess iterator = (IndexAccess) iteratorVal;
            
            Val callback = Evaluator.evaluate(this, new TokenStream(stream.rest()));
            
            if (!(callback instanceof ValBlock)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "callback should be a block");
            }
            
            ValBlock callbackBlock = (ValBlock) callback;
            
            if (callbackBlock.getArgumentNames().size() > 2) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "invalid argument count expected");
            }
            
            for (int i = 0; i < iterator.getIndexCount(); ++i) {
                callbackBlock.invoke(this, new Val[] {
                    iterator.getIndex(i),
                    new ValNum(i)
                });
            }
        } else {
            Val val = Evaluator.evaluate(this, stream);

            if (isInInteractiveMode() && val != null) {
                System.out.println("=> " + val);
            }
        }
    }

    public void parseScanner(Scanner scanner) throws ScannerException, ParserException {
        parseSemicolons(scanner.scan());
    }

    public void parseSemicolons(List<Token> tokens) throws ParserException {
        List<Token> subTokens = new ArrayList<>();

        int braces = 0;

        for (Token token : tokens) {
            if (token.getType() == Token.Type.LBRACE) {
                braces++;
            } else if (token.getType() == Token.Type.RBRACE) {
                braces--;
            }

            if (braces == 0 && token.getType() == Token.Type.SEMICOL) {
                parse(new TokenStream(subTokens));

                subTokens.clear();
            } else {
                subTokens.add(token);
            }
        }

        if (!subTokens.isEmpty()) {
            throw new ParserException(ParserException.Type.UNEXPECTED_EOF);
        }
    }

    public boolean isFor(TokenStream stream) {
        return stream.size() >= 3 && stream.first().getType() == Token.Type.FOR;
    }
    
    public boolean isAssignment(TokenStream stream) {
        return stream.size() >= 3 && stream.first().getType() == Token.Type.IDENTIFIER && (stream.peek().getType() == Token.Type.ASSIGN || stream.peek().getType() == Token.Type.ASSIGN_LOCKED);
    }
}
