package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.assignment.AssignmentParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class AssignmentFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        TokenList list = tokens.copyAtCurrentPosition();

        while (list.hasNext()) {
            if (list.current().getType() == TokenType.LEFT_BRACE || list.current().getType() == TokenType.LEFT_PAREN) {
                return false;
            } else if (list.current().getType().isAssignmentOperator()) {
                return true;
            }

            list.next();
        }

        return false;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        new AssignmentParser(parser, tokens).parse();

        return null;
    }
}
