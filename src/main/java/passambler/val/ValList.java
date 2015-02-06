package passambler.val;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;

public class ValList extends Val implements IndexAccess {
    private List<Val> list = new ArrayList<>();

    public ValList() {
        setProperty("add", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return true;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                add(arguments[0]);
                
                return null;
            }
        });
    }

    @Override
    public Val getIndex(int index) {
        return list.get(index);
    }

    @Override
    public void setIndex(int index, Val value) {
        list.set(index, value);
    }

    @Override
    public int getIndexCount() {
        return list.size();
    }

    public void add(Val value) {
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
