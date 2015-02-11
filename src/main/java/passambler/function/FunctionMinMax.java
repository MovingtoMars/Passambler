package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class FunctionMinMax implements Function {
    private boolean min;
    
    public FunctionMinMax(boolean min) {
        this.min = min;
    }
    
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (value instanceof ValueList) {
            ValueList list = (ValueList) value;
            
            for (int i = 0; i < list.getIndexCount(); ++i) {
                if (!(list.getIndex(i) instanceof ValueNum)) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = (ValueList) arguments[0];
        
        double newValue = 0;
        
        for (int i = 0; i < list.getIndexCount(); ++i) {
            double value = ((ValueNum) list.getIndex(i)).getValue();
            
            if (min) {
                if (i == 0 || value < newValue) {
                    newValue = value;
                }
            } else {
                if (i == 0 || value > newValue) {
                    newValue = value;
                }
            }
        }
        
        return new ValueNum(newValue);
    }
}
