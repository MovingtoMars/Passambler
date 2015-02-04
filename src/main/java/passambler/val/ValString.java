package passambler.val;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.scanner.Token;

public class ValString extends Val implements IndexAccess {
    public ValString(String data) {
        setValue(data);

        setProperty("Append", new Function() {
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
                return new ValString(getValue() + ((ValString) arguments[0]).getValue());
            } 
        });
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
}
