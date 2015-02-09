package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;

public class ValueDict extends Value {
    protected Map<Value, Value> dict = new HashMap();
    
    public ValueDict() {
        setProperty("set", new Function() {
            @Override
            public int getArguments() {
                return 2;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return true;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                set(arguments[0], arguments[1]);
                
                return null;
            }
        });
        
        setProperty("get", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return true;
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return get(arguments[0]);
            }
        });
        
        setProperty("keys", new Function() {
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
        
        setProperty("values", new Function() {
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
        
        setProperty("entries", new Function() {
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
    
    public void set(Value key, Value value) {
        dict.put(key, value);
    }
    
    public Value get(Value key) {
        return dict.get(key);
    }
    
    @Override
    public String toString() {
        return dict.toString();
    }
}
