package passambler.val;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.parser.Scope;
import passambler.scanner.Token;

public class ValString extends Val implements IndexAccess {
    class FunctionAppend extends ValBlock {
        private String currentValue;
        
        public FunctionAppend(String currentValue) {
            super(new Scope(), null);
            
            this.currentValue = currentValue;
        }
        
        @Override
        public int getArguments() {
            return 1;
        }

        @Override
        public boolean isArgumentValid(Val value, int argument) {
            return value instanceof ValString;
        }
        
        @Override
        public Val invoke(Parser parser, Val... arguments) throws ParserException {
            return new ValString(currentValue + ((ValString) arguments[0]).getValue());
        }
    }
    
    public ValString(String data) {
        setValue(data);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Val onOperator(Val value, Token.Type tokenType) {
        if (value instanceof ValString && tokenType == Token.Type.PLUS) {
            return new ValString(getValue() + (value != Val.nil ? value.toString() : ""));
        }

        return null;
    }

    @Override
    public Val getIndex(int index) {
        return new ValString(String.valueOf(getValue().charAt(index)));
    }

    @Override
    public void setIndex(int index, Val value) {
        char[] array = getValue().toCharArray();

        array[index] = ((ValString) value).getValue().charAt(0);

        setValue(new String(array));
    }

    @Override
    public int getIndexCount() {
        return getValue().length();
    }
    
    @Override
    public Val getProperty(String key) {
        if (key.equals("Length")) {
            return new ValNumber(getIndexCount());
        } else if (key.equals("Append")) {
            return new FunctionAppend(getValue());
        } else {
            return super.getProperty(key);
        }
    }
}
