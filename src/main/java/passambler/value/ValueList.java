package passambler.value;

import java.util.ArrayList;
import java.util.List;
import passambler.procedure.Procedure;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.ProcedureSimple;

public class ValueList extends Value implements IndexedValue {
    protected List<Value> list = new ArrayList<>();

    public ValueList() {
        setProperty("add", new Procedure() {
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
                add(arguments[0]);
                
                return null;
            }
        });
        
        setProperty("size", new Property() {
            @Override
            public Value getValue() {
                return new ValueNum(getIndexCount());
            }
        });
            
        setProperty("empty", new ProcedureSimple() {
            @Override
            public Value getValue() {
                return new ValueBool(getIndexCount() == 0);
            }
        });
            
        setProperty("first", new Property() {
            @Override
            public Value getValue() {
                return getIndex(new ValueNum(0));
            }
        });
        
        setProperty("last", new Property() {
            @Override
            public Value getValue() {
                return getIndex(new ValueNum(getIndexCount() - 1));
            }
        });
    }

    @Override
    public Value getIndex(Value key) {
        return list.get(((ValueNum) key).getValueAsInteger());
    }

    @Override
    public void setIndex(Value key, Value value) {
        list.set(((ValueNum) key).getValueAsInteger(), value);
    }

    @Override
    public int getIndexCount() {
        return list.size();
    }

    public void add(Value value) {
        list.add(value);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
