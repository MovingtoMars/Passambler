package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.parser.assignment.AssignmentParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class AssignmentFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return AssignmentParser.isAssignment(tokens.copyAtCurrentPosition());
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        new AssignmentParser(parser, tokens).parse();

        return null;
    }
}
