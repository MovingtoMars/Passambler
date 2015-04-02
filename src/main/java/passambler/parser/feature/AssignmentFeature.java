package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.parser.assignment.AssignmentParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class AssignmentFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return AssignmentParser.isAssignment(stream.copyAtCurrentPosition());
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        new AssignmentParser(parser, stream).parse();

        return null;
    }
}
