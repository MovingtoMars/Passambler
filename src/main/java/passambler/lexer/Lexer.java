package passambler.lexer;

import passambler.exception.LexerException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private Map<String, TokenType> tokenMap = new LinkedHashMap();

    private int line = 1, column = 1;

    private int position;

    private final String input;

    public Lexer(String input) {
        this.input = input;

        tokenMap.put("else if", TokenType.ELSEIF);

        tokenMap.put("return", TokenType.RETURN);
        tokenMap.put("elseif", TokenType.ELSEIF);

        tokenMap.put("class", TokenType.CLASS);
        tokenMap.put("catch", TokenType.CATCH);
        tokenMap.put("while", TokenType.WHILE);

        tokenMap.put("else", TokenType.ELSE);

        tokenMap.put("try", TokenType.TRY);
        tokenMap.put("for", TokenType.FOR);

        tokenMap.put("<=>", TokenType.COMPARE);
        tokenMap.put("**=", TokenType.ASSIGN_POWER);

        tokenMap.put("fn", TokenType.FN);
        tokenMap.put("if", TokenType.IF);

        tokenMap.put("==", TokenType.EQUAL);
        tokenMap.put("!=", TokenType.NEQUAL);
        tokenMap.put(">=", TokenType.GTE);
        tokenMap.put("<=", TokenType.LTE);

        tokenMap.put("&&", TokenType.AND);
        tokenMap.put("||", TokenType.OR);
        tokenMap.put("**", TokenType.POWER);

        tokenMap.put("..", TokenType.RANGE);
        tokenMap.put("+=", TokenType.ASSIGN_PLUS);
        tokenMap.put("-=", TokenType.ASSIGN_MINUS);
        tokenMap.put("*=", TokenType.ASSIGN_MULTIPLY);
        tokenMap.put("/=", TokenType.ASSIGN_DIVIDE);
        tokenMap.put("%=", TokenType.ASSIGN_MODULO);

        tokenMap.put("=", TokenType.ASSIGN);
        tokenMap.put(">", TokenType.GT);
        tokenMap.put("<", TokenType.LT);
        tokenMap.put("[", TokenType.LEFT_BRACKET);
        tokenMap.put("]", TokenType.RIGHT_BRACKET);
        tokenMap.put("(", TokenType.LEFT_PAREN);
        tokenMap.put(")", TokenType.RIGHT_PAREN);
        tokenMap.put("{", TokenType.LEFT_BRACE);
        tokenMap.put("}", TokenType.RIGHT_BRACE);
        tokenMap.put("!", TokenType.UNARY_NOT);
        tokenMap.put(",", TokenType.COMMA);
        tokenMap.put(".", TokenType.PERIOD);
        tokenMap.put("+", TokenType.PLUS);
        tokenMap.put("-", TokenType.MINUS);
        tokenMap.put("*", TokenType.MULTIPLY);
        tokenMap.put("/", TokenType.DIVIDE);
        tokenMap.put("^", TokenType.XOR);
        tokenMap.put(":", TokenType.COL);
        tokenMap.put(";", TokenType.SEMI_COL);
        tokenMap.put("%", TokenType.MODULO);
    }

    public Token createToken(TokenType type, String value) {
        return new Token(type, value, new TokenPosition(line, column));
    }

    public Token createToken(TokenType type) {
        return createToken(type, null);
    }

    public List<Token> scan() throws LexerException {
        List<Token> tokens = new ArrayList<>();

        char stringChar = 0;
        boolean inString = false, inComment = false, multiLineComment = false;

        while (hasNext()) {
            if (current() == '\n') {
                line++;
                column = 0;

                if (inComment && !multiLineComment) {
                    inComment = false;
                }

                next();
            } else if (inComment) {
                if (current() == '*' && peek() != null && peek() == '/') {
                    inComment = false;
                    multiLineComment = false;

                    next();
                    next();
                }

                next();
            } else if ((current() == '\'' || current() == '"') && (!inString || current() == stringChar)) {
                if (current() == stringChar && tokens.size() > 1) {
                    Token value = tokens.get(tokens.size() - 1);

                    if (value.getValue().length() > 0 && value.getValue().charAt(value.getValue().length() - 1) == '\\') {
                        value.setValue(value.getValue().substring(0, value.getValue().length() - 1) + current());

                        next();

                        continue;
                    }
                }

                inString = !inString;

                if (inString) {
                    stringChar = current();

                    tokens.add(createToken(TokenType.STRING, ""));
                }

                next();
            } else if (inString) {
                Token token = tokens.get(tokens.size() - 1);

                token.setValue(tokens.get(tokens.size() - 1).getValue() + current());

                next();
            } else if (current() == ' ' || current() == '\t') {
                next();
            } else if (current() == '/' && peek() != null && (peek() == '/' || peek() == '*')) {
                inComment = true;

                next();

                if (current() == '*') {
                    multiLineComment = true;
                }

                next();
            } else {
                StringBuilder identifierFound = new StringBuilder();

                while (hasNext() && isIdentifier(current())) {
                    identifierFound.append(current());

                    next();
                }

                position -= identifierFound.length();

                Map.Entry<String, TokenType> match = null;

                for (Map.Entry<String, TokenType> entry : tokenMap.entrySet()) {
                    for (int i = 0; i < entry.getKey().length(); ++i) {
                        char current = entry.getKey().charAt(i);

                        if (peek(i) != null && peek(i) == current) {
                            /* If we are on the last index from this match, first check if the
                             matches length is bigger or equal then the currently found identifier.
                             This is so that token "++" doesn't get identified as two "+" tokens. */
                            if (i == entry.getKey().length() - 1 && entry.getKey().length() >= identifierFound.length()) {
                                match = entry;

                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (match != null) {
                        break;
                    }
                }

                if (match != null) {
                    position += match.getKey().length();

                    tokens.add(createToken(match.getValue(), match.getKey()));
                }

                if (match == null && identifierFound.length() > 0) {
                    tokens.add(createToken(Lexer.isNumber(identifierFound.toString()) ? TokenType.NUMBER : TokenType.IDENTIFIER, identifierFound.toString()));

                    position += identifierFound.length();
                } else if (match == null) {
                    throw new LexerException(String.format("Unexpected character '%c'", current()), line, column);
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

    public static boolean isPublic(String string) {
        return Character.isUpperCase(string.charAt(0));
    }

    public static boolean isIdentifier(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static boolean isConstant(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isUpperCase(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }

        return true;
    }

    public static boolean isNumber(String data) {
        for (int c : data.chars().toArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
