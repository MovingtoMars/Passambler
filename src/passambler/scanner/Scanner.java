package passambler.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private Map<Character, Token.Type> tokenMap = new HashMap();

    private int line = 1, column = 1;

    private int index;

    private final String input;

    public Scanner(String input) {
        this.input = input;

        tokenMap.put('=', Token.Type.ASSIGN);
        tokenMap.put('>', Token.Type.GT);
        tokenMap.put('<', Token.Type.LT);
        tokenMap.put('[', Token.Type.LBRACKET);
        tokenMap.put(']', Token.Type.RBRACKET);
        tokenMap.put('(', Token.Type.LPAREN);
        tokenMap.put(')', Token.Type.RPAREN);
        tokenMap.put('{', Token.Type.LBRACE);
        tokenMap.put('}', Token.Type.RBRACE);
        tokenMap.put('|', Token.Type.PIPE);
        tokenMap.put(',', Token.Type.COMMA);
        tokenMap.put('.', Token.Type.DOT);
        tokenMap.put('+', Token.Type.PLUS);
        tokenMap.put('-', Token.Type.MINUS);
        tokenMap.put('*', Token.Type.MULTIPLY);
        tokenMap.put('/', Token.Type.DIVIDE);
        tokenMap.put(';', Token.Type.SEMICOL);
        tokenMap.put('^', Token.Type.POWER);
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
        boolean inString = false, inComment = false;

        while (hasNext()) {
            if (current() == '\n') {
                line++;
                column = 0;

                inComment = false;
                
                next();
            } else if (inComment) {
                next();
            } else if (current() == '\'' || current() == '"') {
                inString = !inString;

                if (inString) {
                    stringChar = current();

                    tokens.add(createToken(Token.Type.STRING, ""));
                } else {
                    if (stringChar != current()) {
                        throw new ScannerException(String.format("invalid closing of string literal", current()), line, column);
                    }
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
            } else if (current() == '&' && peek() != null && peek() == '&') {
                tokens.add(createToken(Token.Type.AND));

                next();
                next();
            } else if (current() == '|' && peek() != null && peek() == '|') {
                tokens.add(createToken(Token.Type.OR));

                next();
                next();
            } else if (current() == '>' && peek() != null && peek() == '=') {
                tokens.add(createToken(Token.Type.GTE));

                next();
                next();
            } else if (current() == '<' && peek() != null && peek() == '=') {
                tokens.add(createToken(Token.Type.LTE));

                next();
                next();
            } else if (current() == '=' && peek() != null && peek() == '=') {
                tokens.add(createToken(Token.Type.EQUAL));

                next();
                next();
            } else if (current() == '!' && peek() != null && peek() == '=') {
                tokens.add(createToken(Token.Type.NEQUAL));

                next();
                next();
            } else if (current() == '-' && peek() != null && peek() == '-') {
                inComment = true;

                next();
                next();
            } else if (current() == '-' && peek() != null && peek() == '>') {
                tokens.add(createToken(Token.Type.STREAM));

                next();
                next();
            } else if (isIdentifier(current())) {
                tokens.add(createToken(Token.Type.IDENTIFIER, String.valueOf(current())));

                next();

                while (hasNext() && isIdentifier(current())) {
                    Token token = tokens.get(tokens.size() - 1);

                    token.setValue(token.getStringValue() + current());

                    next();
                }

                Token constant = tokens.get(tokens.size() - 1);

                if (Scanner.isNumber(constant.getStringValue())) {
                    constant.setType(Token.Type.NUMBER);

                    constant.setValue(constant.getStringValue());
                }
            } else if (tokenMap.containsKey(current())) {
                tokens.add(createToken(tokenMap.get(current())));

                next();
            } else if (current() == ' ' || current() == '\t') {
                next();
            } else {
                throw new ScannerException(String.format("unexpected character %c", current()), line, column);
            }
        }

        return tokens;
    }

    public char current() {
        return input.charAt(index);
    }

    public void next() {
        index++;

        column++;
    }

    public boolean hasNext() {
        return index < input.length();
    }

    public Character peek() {
        return peek(1);
    }

    public Character peek(int amount) {
        return index + amount > input.length() ? null : input.charAt(index + amount);
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
