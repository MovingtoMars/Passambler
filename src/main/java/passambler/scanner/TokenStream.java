package passambler.scanner;

import java.util.List;

public class TokenStream {
    public int position;

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
        return position + 1 != tokens.size() + 1;
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
}
