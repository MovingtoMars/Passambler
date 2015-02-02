package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.IndexAccess;
import passambler.val.Val;
import passambler.val.ValNumber;

public class FunctionArraySet implements Function {
    @Override
    public int getArguments() {
        return 3;
    }
    
    @Override
    public boolean isArgumentValid(Val value, int argument) {
        switch (argument) {
            case 0:
                return value instanceof IndexAccess;
            case 1:
                return value instanceof ValNumber;
            case 2:
                return value instanceof Val;
            default:
                return false;
        }
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        IndexAccess indexAccess = (IndexAccess) arguments[0];
        
        int index = ((ValNumber) arguments[1]).getValueAsInteger();
        
        if (index < 0 || index > indexAccess.getIndexCount() - 1) {
            throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, index, indexAccess.getIndexCount());
        }
        
        indexAccess.setIndex(index, arguments[2]);
        
        return null;
    }
}