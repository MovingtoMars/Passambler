package passambler.extension.file.procedure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class ProcedureMove extends Procedure {
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
        try {
            Path file = Paths.get(((ValueStr) arguments[0]).getValue());
            Path destination = Paths.get(((ValueStr) arguments[1]).getValue());
            
            Files.move(file, destination);
        } catch (Exception e) {
            return new ValueBool(false);
        }
        
        return new ValueBool(true);
    }
}