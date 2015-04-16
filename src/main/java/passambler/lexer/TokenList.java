package passambler.lexer;

import java.util.List;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;

public class TokenList {
    private int position;

    private List<Token> tokens;

    public TokenList(List<Token> tokens) {
        this(tokens, 0);
    }

    public TokenList(List<Token> tokens, int position) {
        this.tokens = tokens;
        this.position = position;
    }

    public Token current() {
        return position > tokens.size() - 1 ? null : tokens.get(position);
    }

    public boolean hasNext() {
        return position + 1 <= tokens.size();
    }

    public void next() {
        position++;
    }

    public int size() {
        return tokens.size();
    }

    public Token peek() {
        return peek(1);
    }

    public Token peek(int amount) {
        return position + amount < tokens.size() ? tokens.get(position + amount) : null;
    }

    public Token get(int index) {
        return tokens.get(index);
    }

    public int getPosition() {
        return position;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<Token> getTokensFromPosition() {
        return tokens.subList(position, tokens.size());
    }

    public TokenList copy() {
        return new TokenList(tokens);
    }

    public TokenList copyAtCurrentPosition() {
        return new TokenList(tokens, position);
    }

    public void match(TokenType... types) throws ParserException {
        StringBuilder typesExpected = new StringBuilder();

        for (int i = 0; i < types.length; ++i) {
            typesExpected.append(types[i]);

            if (i == types.length - 2) {
                typesExpected.append(" or ");
            } else if (i != types.length - 1) {
                typesExpected.append(", ");
            }

            if (current() != null && current().getType() == types[i]) {
                return;
            }
        }

        throw new ParserException(ParserExceptionType.INVALID_TOKEN, current() == null ? get(0).getPosition() : current().getPosition(), typesExpected.toString(), current() == null ? "nothing" : current().getType());
    }
}
