package passambler.extension.std.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueList;

public class ProcedureKeys implements Procedure {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueDict;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = new ValueList();

        ((ValueDict) arguments[0]).getValue().keySet().stream().forEach((key) -> {
            list.add(key);
        });

        return list;
    }
}
