package passambler.lexer;

import java.util.List;
import passambler.parser.ParserException;

public class TokenStream {
    private int position;

    private List<Token> tokens;

    public TokenStream(List<Token> tokens) {
        this(tokens, 0);
    }

    public TokenStream(List<Token> tokens, int position) {
        this.tokens = tokens;
        this.position = position;
    }

    public Token back() {
        return back(1);
    }

    public Token back(int amount) {
        return position - amount < 0 ? null : tokens.get(position - amount);
    }

    public Token current() {
        return tokens.get(position);
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

    public Token first() {
        return tokens.get(0);
    }

    public Token last() {
        return tokens.get(tokens.size() - 1);
    }

    public int getPosition() {
        return position;
    }

    public List<Token> rest() {
        return tokens.subList(position, tokens.size());
    }

    public TokenStream copy() {
        return new TokenStream(tokens);
    }

    public TokenStream copyAtCurrentPosition() {
        return new TokenStream(tokens, position);
    }

    public void match(Token.Type... types) throws ParserException {
        StringBuilder typesExpected = new StringBuilder();

        for (int i = 0; i < types.length; ++i) {
            typesExpected.append(types[i]);

            if (i == types.length - 2) {
                typesExpected.append(" or ");
            } else if (i != types.length - 1) {
                typesExpected.append(", ");
            }

            if (current().getType() == types[i]) {
                return;
            }
        }

        throw new ParserException(ParserException.Type.INVALID_TOKEN, current().getPosition(), typesExpected.toString(), current().getType());
    }
}
