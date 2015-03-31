package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionIsDir extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) throws ParserException {
        try {
            return new ValueBool(Files.isDirectory(file));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
