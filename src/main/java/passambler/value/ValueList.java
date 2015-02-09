package passambler.value;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;

public class ValueList extends Value implements IndexedValue {
    protected List<Value> list = new ArrayList<>();

    public ValueList() {
        setProperty("add", new Function() {
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
    }

    @Override
    public Value getIndex(int index) {
        return list.get(index);
    }

    @Override
    public void setIndex(int index, Value value) {
        list.set(index, value);
    }

    @Override
    public int getIndexCount() {
        return list.size();
    }

    public void add(Value value) {
        if (isLocked()) {
            throw new RuntimeException("Value is locked");
        }

        list.add(value);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
