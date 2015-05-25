package passambler.lexer;

import passambler.exception.LexerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private Map<String, TokenType> tokenMap = new LinkedHashMap();
    private Map<Character, String> escapeSequences = new HashMap();

    private int line = 1, column = 1;
    private int position;

    private final String input;

    public Lexer(String input) {
        this.input = input;

        tokenMap.put("finally", TokenType.FINALLY);
        tokenMap.put("else if", TokenType.ELSEIF);

        tokenMap.put("return", TokenType.RETURN);
        tokenMap.put("elseif", TokenType.ELSEIF);

        tokenMap.put("class", TokenType.CLASS);
        tokenMap.put("catch", TokenType.CATCH);
        tokenMap.put("while", TokenType.WHILE);
        tokenMap.put("defer", TokenType.DEFER);

        tokenMap.put("stop", TokenType.STOP);
        tokenMap.put("skip", TokenType.SKIP);
        tokenMap.put("func", TokenType.FUNC);
        tokenMap.put("else", TokenType.ELSE);

        tokenMap.put("try", TokenType.TRY);
        tokenMap.put("for", TokenType.FOR);
        tokenMap.put("pub", TokenType.PUB);
        tokenMap.put("<=>", TokenType.COMPARE);
        tokenMap.put("**=", TokenType.ASSIGN_POWER);
        tokenMap.put("...", TokenType.INCLUSIVE_RANGE);

        tokenMap.put("in", TokenType.IN);
        tokenMap.put("if", TokenType.IF);
        tokenMap.put("->", TokenType.ARROW);

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
        tokenMap.put("?", TokenType.TERNARY);

        escapeSequences.put('t', "\t");
        escapeSequences.put('b', "\b");
        escapeSequences.put('n', "\n");
        escapeSequences.put('r', "\r");
        escapeSequences.put('f', "\f");
        escapeSequences.put('"', "\"");
        escapeSequences.put('\'', "'");
        escapeSequences.put('\\', "\\");
    }

    public Token createToken(TokenType type, String value) {
        return new Token(type, value, new TokenPosition(line, column));
    }

    public Token createToken(TokenType type) {
        return createToken(type, null);
    }

    public List<Token> tokenize() throws LexerException {
        List<Token> tokens = new ArrayList<>();

        while (current() != null) {
            if (current() == '"' || current() == '\'') {
                char stringChar = current();
                Token string = createToken(TokenType.STRING, "");

                next();

                while (current() != null) {
                    if (current() == stringChar) {
                        break;
                    }

                    string.setValue(string.getValue() + parseCharacter());
                    next();
                }

                next();

                tokens.add(string);
            } else if (current() == ' ' || current() == '\t' || current() == '\n') {
                next();
            } else if (current() == '/' && peek() != null && peek() == '/') {
                next();
                next();

                while (current() != '\n') {
                    next();
                }
            } else if (current() == '/' && peek() != null && peek() == '*') {
                next();
                next();

                while (current() != null) {
                    next();

                    if (current() == '*' && peek() != null && peek() == '/') {
                        next();
                        next();

                        break;
                    }
                }
            } else if (Character.isDigit(current())) {
                Token number = createToken(TokenType.NUMBER, "");

                while (current() != null && Character.isDigit(current())) {
                    number.setValue(number.getValue() + current());
                    next();

                    if (current() == '_') {
                        next();
                    }
                }

                tokens.add(number);
            } else {
                StringBuilder identifierFound = new StringBuilder();

                while (current() != null && isIdentifier(current())) {
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
                    tokens.add(createToken(TokenType.IDENTIFIER, identifierFound.toString()));

                    position += identifierFound.length();
                } else if (match == null) {
                    throw new LexerException(String.format("Unexpected character '%c'", current()), line, column);
                }
            }
        }

        return tokens;
    }

    private String parseCharacter() throws LexerException {
        if (current() == '\\' && peek() != null) {
            next();

            if (!escapeSequences.containsKey(current())) {
                throw new LexerException(String.format("Escape sequence '%c' not found", current()), line, column);
            }

            return escapeSequences.get(current());
        } else {
            return String.valueOf(current());
        }
    }

    public Character current() {
        return position < 0 || position > input.length() - 1 ? null : input.charAt(position);
    }

    public void next() {
        position++;

        if (current() != null && current() == '\n') {
            line++;
            column = 0;
        }

        column++;
    }

    public Character peek() {
        return peek(1);
    }

    public Character peek(int amount) {
        return position + amount < 0 || position + amount > input.length() - 1 ? null : input.charAt(position + amount);
    }

    public static boolean isIdentifier(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
}
