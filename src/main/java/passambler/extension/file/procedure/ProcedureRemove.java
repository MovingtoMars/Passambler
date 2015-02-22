package passambler.extension.file.procedure;

import java.nio.file.Files;
import java.nio.file.Paths;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class ProcedureRemove extends Procedure {
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
        try {
            Files.delete(Paths.get(((ValueStr) arguments[0]).getValue()));
        } catch (Exception e) {
            return new ValueBool(false);
        }
        
        return new ValueBool(true);
    }
}