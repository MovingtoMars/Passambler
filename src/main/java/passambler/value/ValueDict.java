package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.procedure.Procedure;
import passambler.parser.Parser;
import passambler.parser.ParserException;

public class ValueDict extends Value implements IndexedValue {
    protected Map<Value, Value> dict = new HashMap();

    public ValueDict() {
        setProperty("keys", new Procedure() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return false;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                ValueList list = new ValueList();

                dict.keySet().stream().forEach((key) -> {
                    list.add(key);
                });

                return list;
            }
        });

        setProperty("values", new Procedure() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return false;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                ValueList list = new ValueList();

                dict.values().stream().forEach((value) -> {
                    list.add(value);
                });

                return list;
            }
        });

        setProperty("entries", new Procedure() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return false;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                ValueList list = new ValueList();

                dict.entrySet().stream().forEach((set) -> {
                    Value value = new Value();

                    value.setProperty("key", set.getKey());
                    value.setProperty("value", set.getValue());

                    list.add(value);
                });

                return list;
            }
        });
    }

    @Override
    public Value getIndex(Value key) {
        for (Map.Entry<Value, Value> entry : dict.entrySet()) {
            if (entry.getKey().getValue().equals(key.getValue())) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    @Override
    public void setIndex(Value key, Value value) {
        dict.put(key, value);
    }

    @Override
    public int getIndexCount() {
        return dict.size();
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
