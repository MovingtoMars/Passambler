package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.lexer.Token;
import passambler.value.Value;

public class Block {
    private Parser parser;
    
    private List<Token> tokens = new ArrayList<>();
    
    public Block(Scope scope) {
        this.parser = new Parser(new Scope(scope));
    }
    
    public Parser getParser() {
        return parser;
    }
    
    public List<Token> getTokens() {
        return tokens;
    }
    
    public Value invoke() throws ParserException {
        return parser.parse(tokens);
    }
}
