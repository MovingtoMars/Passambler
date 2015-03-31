package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionCreate extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) throws ParserException {
        try {
            Files.createFile(file);
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return new ValueBool(true);
    }
}
