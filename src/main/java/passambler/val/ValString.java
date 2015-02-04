package passambler.val;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.scanner.Token;

public class ValString extends Val implements IndexAccess {
    public ValString(String data) {
        setValue(data);

        setProperty("append", new Function() {
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
        
        setProperty("split", new Function() {
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
                String[] parts = getValue().split(((ValString) arguments[0]).getValue());
                
                ValArray array = new ValArray(parts.length);
                
                for (int i = 0; i < parts.length; ++i) {
                    array.setIndex(i, new ValString(parts[i]));
                }
                
                return array;
            }
        });
        
        setProperty("replace", new Function() {
            @Override
            public int getArguments() {
                return 2;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValString;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValString(getValue().replace(((ValString) arguments[0]).getValue(), ((ValString) arguments[1]).getValue()));
            }
        });
        
        setProperty("contains", new Function() {
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
                return new ValBool(getValue().contains(((ValString) arguments[0]).getValue()));
            }
        });
        
        setProperty("lowerCase", new Function() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValString;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValString(getValue().toLowerCase());
            }
        });
        
        setProperty("upperCase", new Function() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValString;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValString(getValue().toUpperCase());
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
