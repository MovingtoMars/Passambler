package passambler.parser.statement;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.parser.Parser;
import passambler.value.Value;

public interface Statement {
    public boolean canPerform(Parser parser, TokenList tokens);

    public Value perform(Parser parser, TokenList tokens) throws EngineException;
}
