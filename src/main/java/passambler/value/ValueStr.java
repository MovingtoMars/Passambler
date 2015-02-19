package passambler.value;

import passambler.procedure.Procedure;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.lexer.Token;

public class ValueStr extends Value implements IndexedValue {
    public ValueStr(String data) {
        setValue(data);

        setProperty("append", new Procedure() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueStr(getValue() + ((ValueStr) arguments[0]).getValue());
            }
        });

        setProperty("split", new Procedure() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                ValueList list = new ValueList();

                for (String part : getValue().split(((ValueStr) arguments[0]).getValue())) {
                    list.add(new ValueStr(part));
                }

                return list;
            }
        });

        setProperty("replace", new Procedure() {
            @Override
            public int getArguments() {
                return 2;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueStr(getValue().replace(((ValueStr) arguments[0]).getValue(), ((ValueStr) arguments[1]).getValue()));
            }
        });

        setProperty("contains", new Procedure() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueBool(getValue().contains(((ValueStr) arguments[0]).getValue()));
            }
        });

        setProperty("indexOf", new Procedure() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueNum(getValue().indexOf(((ValueStr) arguments[0]).getValue()));
            }
        });

        setProperty("lowerCase", new Procedure() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueStr(getValue().toLowerCase());
            }
        });

        setProperty("upperCase", new Procedure() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return value instanceof ValueStr;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return new ValueStr(getValue().toUpperCase());
            }
        });
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Value onOperator(Value value, Token.Type tokenType) {
        if (value instanceof ValueStr && (tokenType == Token.Type.PLUS || tokenType == Token.Type.ASSIGN_PLUS)) {
            return new ValueStr(getValue() + value.toString());
        }

        return super.onOperator(value, tokenType);
    }

    @Override
    public Value getIndex(Value key) {
        return new ValueStr(String.valueOf(getValue().charAt(((ValueNum) key).getValueAsInteger())));
    }

    @Override
    public void setIndex(Value key, Value value) {
        char[] array = getValue().toCharArray();

        array[((ValueNum) key).getValueAsInteger()] = ((ValueStr) value).getValue().charAt(0);

        setValue(new String(array));
    }

    @Override
    public int getIndexCount() {
        return getValue().length();
    }
}
