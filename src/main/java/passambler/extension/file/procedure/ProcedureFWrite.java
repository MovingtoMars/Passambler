package passambler.extension.file.procedure;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueStr;

public class ProcedureFWrite extends Procedure {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        try {
            String fileName = ((ValueStr) arguments[0]).getValue();
        
            for (int i = 1; i < arguments.length; ++i) {
                Files.write(Paths.get(fileName), ((ValueStr) arguments[i]).getValue().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            
        }
        
        return null;
    }
}