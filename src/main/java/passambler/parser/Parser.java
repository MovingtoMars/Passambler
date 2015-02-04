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
import passambler.val.ValNumber;

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
                scope.setFunction(key, (Function) value);
            } else {
                scope.setVariable(key, value);
            }
        } else if (isStream(stream.copy()) || isReverseStream(stream.copy())) {
            List<Token> tokensBeforeStream = new ArrayList<>();
            List<Token> tokensAfterStream = new ArrayList<>();
            
            boolean reverseStream = isReverseStream(stream.copy());

            boolean passed = false;
            
            int braces = 0;

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE) {
                    braces++;
                } else if (stream.current().getType() == Token.Type.RBRACE) {
                    braces--;
                }
                
                if (!passed && braces == 0 && (stream.current().getType() == Token.Type.STREAM || stream.current().getType() == Token.Type.STREAM_REVERSE)) {
                    passed = true;
                } else {
                    (passed ? (reverseStream ? tokensBeforeStream : tokensAfterStream) : (reverseStream ? tokensAfterStream : tokensBeforeStream)).add(stream.current());
                }

                stream.next();
            }

            Val toSend = Evaluator.evaluate(this, new TokenStream(tokensBeforeStream));
            Val toReceive = Evaluator.evaluate(this, new TokenStream(tokensAfterStream));

            if (toSend instanceof IndexAccess && toReceive instanceof ValBlock) {
                IndexAccess indexAccess = (IndexAccess) toSend;

                for (int i = 0; i < indexAccess.getIndexCount(); ++i) {
                    ((ValBlock) toReceive).invoke(((ValBlock) toReceive).getParser(), new Val[] {
                        indexAccess.getIndex(i),
                        new ValNumber(i)
                    });
                }
            } else if (toSend instanceof ValBool && toReceive instanceof ValBlock) {
                while (((ValBool) toSend).getValue() == true) {
                    ((ValBlock) toReceive).invoke(((ValBlock) toReceive).getParser());

                    // We need to refresh the condition for the while loop
                    toSend = Evaluator.evaluate(this, new TokenStream(tokensBeforeStream));
                }
            } else {
                if (!(toReceive instanceof Stream)) {
                    // I'm using stream.back() here as the while loop is always one too far
                    throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.back().getPosition(), "not a stream");
                }

                ((Stream) toReceive).onStream(toSend);
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

        int doCount = 0, endCount = 0;

        for (Token token : tokens) {
            if (token.getType() == Token.Type.LBRACE) {
                doCount++;
            } else if (token.getType() == Token.Type.RBRACE) {
                endCount++;
            }

            if (token.getType() == Token.Type.SEMICOL && doCount == endCount) {
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

    public boolean isStream(TokenStream stream) {
        return stream.rest().stream().anyMatch(t -> t.getType() == Token.Type.STREAM);
    }
    
    public boolean isReverseStream(TokenStream stream) {
        return stream.rest().stream().anyMatch(t -> t.getType() == Token.Type.STREAM_REVERSE);
    }

    public boolean isAssignment(TokenStream stream) {
        return stream.size() >= 3 && stream.first().getType() == Token.Type.IDENTIFIER && (stream.peek().getType() == Token.Type.ASSIGN || stream.peek().getType() == Token.Type.ASSIGN_LOCKED);
    }
}
