package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.parser.Parser;
import passambler.value.Value;

public interface Feature {
    public boolean canPerform(Parser parser, TokenStream stream);

    public Value perform(Parser parser, TokenStream stream) throws EngineException;
}
