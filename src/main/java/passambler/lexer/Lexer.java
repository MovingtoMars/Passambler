package passambler.lexer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private Map<String, Token.Type> tokenMap = new LinkedHashMap();

    private int line = 1, column = 1;

    private int position;

    private final String input;

    public Lexer(String input) {
        this.input = input;

        tokenMap.put("package", Token.Type.PACKAGE);

        tokenMap.put("return", Token.Type.RETURN);
        tokenMap.put("import", Token.Type.IMPORT);
        tokenMap.put("elseif", Token.Type.ELSEIF);

        tokenMap.put("while", Token.Type.WHILE);

        tokenMap.put("else", Token.Type.ELSE);

        tokenMap.put("for", Token.Type.FOR);

        tokenMap.put("<=>", Token.Type.COMPARE);
        tokenMap.put("cmp", Token.Type.COMPARE);

        tokenMap.put("and", Token.Type.AND);

        tokenMap.put("fn", Token.Type.FN);
        tokenMap.put("if", Token.Type.IF);

        tokenMap.put("==", Token.Type.EQUAL);
        tokenMap.put("eq", Token.Type.EQUAL);

        tokenMap.put("!=", Token.Type.NEQUAL);
        tokenMap.put("ne", Token.Type.NEQUAL);

        tokenMap.put(">=", Token.Type.GTE);
        tokenMap.put("ge", Token.Type.GTE);

        tokenMap.put("<=", Token.Type.LTE);
        tokenMap.put("le", Token.Type.LTE);

        tokenMap.put("gt", Token.Type.GT);
        tokenMap.put("lt", Token.Type.LT);

        tokenMap.put("&&", Token.Type.AND);

        tokenMap.put("||", Token.Type.OR);
        tokenMap.put("or", Token.Type.OR);

        tokenMap.put("..", Token.Type.RANGE);
        tokenMap.put("+=", Token.Type.ASSIGN_PLUS);
        tokenMap.put("-=", Token.Type.ASSIGN_MINUS);
        tokenMap.put("*=", Token.Type.ASSIGN_MULTIPLY);
        tokenMap.put("/=", Token.Type.ASSIGN_DIVIDE);
        tokenMap.put("^=", Token.Type.ASSIGN_POWER);
        tokenMap.put("%=", Token.Type.ASSIGN_MODULO);

        tokenMap.put("=", Token.Type.ASSIGN);
        tokenMap.put(">", Token.Type.GT);
        tokenMap.put("<", Token.Type.LT);
        tokenMap.put("[", Token.Type.LBRACKET);
        tokenMap.put("]", Token.Type.RBRACKET);
        tokenMap.put("(", Token.Type.LPAREN);
        tokenMap.put(")", Token.Type.RPAREN);
        tokenMap.put("{", Token.Type.LBRACE);
        tokenMap.put("}", Token.Type.RBRACE);
        tokenMap.put(",", Token.Type.COMMA);
        tokenMap.put(".", Token.Type.PERIOD);
        tokenMap.put("+", Token.Type.PLUS);
        tokenMap.put("-", Token.Type.MINUS);
        tokenMap.put("*", Token.Type.MULTIPLY);
        tokenMap.put("/", Token.Type.DIVIDE);
        tokenMap.put(":", Token.Type.COL);
        tokenMap.put(";", Token.Type.SEMI_COL);
        tokenMap.put("^", Token.Type.POWER);
        tokenMap.put("%", Token.Type.MODULO);
    }

    public Token createToken(Token.Type type, String value) {
        return new Token(type, value, new SourcePosition(line, column));
    }

    public Token createToken(Token.Type type) {
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

                    tokens.add(createToken(Token.Type.STRING, ""));
                }

                next();
            } else if (inString) {
                Token token = tokens.get(tokens.size() - 1);

                if (stringChar == '"' && current() == '\\' && peek() != null && (peek() == 'n' || peek() == 'r' || peek() == 't')) {
                    switch (peek()) {
                        case 'n':
                        case 'r':
                            token.setValue(tokens.get(tokens.size() - 1).getValue() + System.getProperty("line.separator"));
                            break;
                        case 't':
                            token.setValue(tokens.get(tokens.size() - 1).getValue() + "\t");
                            break;
                    }

                    next();
                } else {
                    token.setValue(tokens.get(tokens.size() - 1).getValue() + current());
                }

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
                boolean matched = false;
                
                StringBuilder identifierFound = new StringBuilder();
                
                while (hasNext() && isIdentifier(current())) {
                    identifierFound.append(current());

                    next();
                }
                
                position -= identifierFound.length();
                
                for (Map.Entry<String, Token.Type> match : tokenMap.entrySet()) {
                    for (int i = 0; i < match.getKey().length(); ++i) {
                        char current = match.getKey().charAt(i);

                        if (peek(i) != null && peek(i) == current) {
                            // If we are on the last index from this match, first check if the
                            // math's length is bigger or equal then the currently found identifier.
                            // This is so that identifier "net" doesn't get identified as token "ne" + identifier "t".
                            if (i == match.getKey().length() - 1 && match.getKey().length() >= identifierFound.length()) {
                                position += match.getKey().length();

                                tokens.add(createToken(match.getValue(), match.getKey()));

                                matched = true;

                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

                if (!matched && identifierFound.length() > 0) {
                    tokens.add(createToken(Lexer.isNumber(identifierFound.toString()) ? Token.Type.NUMBER : Token.Type.IDENTIFIER, identifierFound.toString()));

                    position += identifierFound.length();
                } else if (!matched) {
                    throw new LexerException(String.format("unexpected character %c", current()), line, column);
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
        for (int character : data.chars().toArray()) {
            if (!Character.isDigit(character)) {
                return false;
            }
        }

        return true;
    }
}
