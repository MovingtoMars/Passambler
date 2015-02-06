package passambler.scanner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private Map<String, Token.Type> tokenMap = new LinkedHashMap();

    private int line = 1, column = 1;

    private int position;

    private final String input;

    public Scanner(String input) {
        this.input = input;

        tokenMap.put("|=", Token.Type.ASSIGN_LOCKED);
        tokenMap.put("==", Token.Type.EQUAL);
        tokenMap.put("!=", Token.Type.NEQUAL);
        tokenMap.put(">=", Token.Type.GTE);
        tokenMap.put("<=", Token.Type.LTE);
        tokenMap.put("->", Token.Type.STREAM);
        tokenMap.put("<-", Token.Type.STREAM_REVERSE);
        tokenMap.put("&&", Token.Type.AND);
        tokenMap.put("||", Token.Type.OR);
        tokenMap.put("..", Token.Type.DOT_DOUBLE);
        
        tokenMap.put("=", Token.Type.ASSIGN);
        tokenMap.put(">", Token.Type.GT);
        tokenMap.put("<", Token.Type.LT);
        tokenMap.put("[", Token.Type.LBRACKET);
        tokenMap.put("]", Token.Type.RBRACKET);
        tokenMap.put("(", Token.Type.LPAREN);
        tokenMap.put(")", Token.Type.RPAREN);
        tokenMap.put("{", Token.Type.LBRACE);
        tokenMap.put("}", Token.Type.RBRACE);
        tokenMap.put("|", Token.Type.PIPE);
        tokenMap.put(",", Token.Type.COMMA);
        tokenMap.put(".", Token.Type.DOT);
        tokenMap.put("+", Token.Type.PLUS);
        tokenMap.put("-", Token.Type.MINUS);
        tokenMap.put("*", Token.Type.MULTIPLY);
        tokenMap.put("/", Token.Type.DIVIDE);
        tokenMap.put(";", Token.Type.SEMICOL);
        tokenMap.put("^", Token.Type.POWER);
    }

    public Token createToken(Token.Type type, Object value) {
        return new Token(type, value, new SourcePosition(line, column));
    }

    public Token createToken(Token.Type type) {
        return createToken(type, null);
    }

    public List<Token> scan() throws ScannerException {
        List<Token> tokens = new ArrayList<>();

        char stringChar = 0;
        boolean inString = false, inComment = false, multiLineComment = false;

        while (hasNext()) {
            if (current() == '\n') {
                line++;
                column = 0;

                if (!multiLineComment) {
                    inComment = false;
                }

                next();
            } else if (inComment) {
                if (current() == '-' && peek() != null && peek() == '-' && peek(2) != null && peek(2) == '-') {
                    inComment = false;
                    multiLineComment = false;

                    next();
                    next();
                }

                next();
            } else if ((current() == '\'' || current() == '"') && (!inString || current() == stringChar)) {
                inString = !inString;

                if (inString) {
                    stringChar = current();

                    tokens.add(createToken(Token.Type.STRING, ""));
                }

                next();
            } else if (inString) {
                Token token = tokens.get(tokens.size() - 1);

                if (stringChar == '"' && current() == '\\' && peek() != null && (peek() == 'n' || peek() == 'r' || peek() == 't')) {
                    switch (peek()) {
                        case 'n':
                        case 'r':
                            token.setValue(tokens.get(tokens.size() - 1).getStringValue() + System.getProperty("line.separator"));
                            break;
                        case 't':
                            token.setValue(tokens.get(tokens.size() - 1).getStringValue() + "\t");
                            break;
                    }

                    next();
                } else {
                    token.setValue(tokens.get(tokens.size() - 1).getStringValue() + current());
                }

                next();              
            } else if (current() == ' ' || current() == '\t') {
                next();
            } else if (current() == '-' && peek() != null && peek() == '-') {
                inComment = true;
                multiLineComment = peek(2) != null && peek(2) == '-';

                if (multiLineComment) {
                    next();
                }

                next();
                next();
            } else {
                boolean matched = false;
                
                for (Map.Entry<String, Token.Type> match : tokenMap.entrySet()) {    
                    for (int i = 0; i < match.getKey().length(); ++i) {
                        char current = match.getKey().charAt(i);
                        
                        if (peek(i) != null && peek(i) == current) {
                            if (i == match.getKey().length() - 1) {
                                position += match.getKey().length();

                                tokens.add(createToken(match.getValue(), match.getKey()));

                                matched = true;
                                
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    
                    if (matched) {
                        break;
                    }
                }
            
                if (!matched && isIdentifier(current())) {
                    tokens.add(createToken(Token.Type.IDENTIFIER, String.valueOf(current())));

                    next();

                    while (hasNext() && isIdentifier(current())) {
                        Token token = tokens.get(tokens.size() - 1);

                        token.setValue(token.getStringValue() + current());

                        next();
                    }

                    Token identifier = tokens.get(tokens.size() - 1);

                    if (Scanner.isNumber(identifier.getStringValue())) {
                        identifier.setType(Token.Type.NUMBER);
                        identifier.setValue(identifier.getStringValue());
                    }
                } else if (!matched) {
                    throw new ScannerException(String.format("unexpected character %c", current()), line, column);
                }
            }
        }

        return tokens;
    }

    public char current() {
        return input.charAt(position);
    }
    
    public void next() {
        position++;

        column++;
    }

    public boolean hasNext() {
        return position < input.length();
    }

    public Character peek() {
        return peek(1);
    }

    public Character peek(int amount) {
        return position + amount > input.length() - 1 ? null : input.charAt(position + amount);
    }

    public boolean isIdentifier(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static boolean isNumber(String data) {
        try {
            Integer.parseInt(data);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
