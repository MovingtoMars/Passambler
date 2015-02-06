package passambler.val;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.scanner.Token;

public class ValStr extends Val implements IndexAccess {
    public ValStr(String data) {
        setValue(data);

        setProperty("append", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValStr(getValue() + ((ValStr) arguments[0]).getValue());
            }
        });
        
        setProperty("split", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                ValList list = new ValList();
                
                for (String part : getValue().split(((ValStr) arguments[0]).getValue())) {
                    list.add(new ValStr(part));
                }
                
                return list;
            }
        });
        
        setProperty("replace", new Function() {
            @Override
            public int getArguments() {
                return 2;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValStr(getValue().replace(((ValStr) arguments[0]).getValue(), ((ValStr) arguments[1]).getValue()));
            }
        });
        
        setProperty("contains", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValBool(getValue().contains(((ValStr) arguments[0]).getValue()));
            }
        });
        
        setProperty("indexOf", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValNum(getValue().indexOf(((ValStr) arguments[0]).getValue()));
            }
        });
        
        setProperty("lowerCase", new Function() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValStr(getValue().toLowerCase());
            }
        });
        
        setProperty("upperCase", new Function() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return value instanceof ValStr;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return new ValStr(getValue().toUpperCase());
            }
        });
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Val onOperator(Val value, Token.Type tokenType) {
        if (value instanceof ValStr && tokenType == Token.Type.PLUS) {
            return new ValStr(getValue() + value.toString());
        }

        return super.onOperator(value, tokenType);
    }
    
    @Override
    public Val getIndex(int index) {
        return new ValStr(String.valueOf(getValue().charAt(index)));
    }

    @Override
    public void setIndex(int index, Val value) {
        char[] array = getValue().toCharArray();

        array[index] = ((ValStr) value).getValue().charAt(0);

        setValue(new String(array));
    }

    @Override
    public int getIndexCount() {
        return getValue().length();
    }
}
