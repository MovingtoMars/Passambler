package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionModified extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) throws ParserException {
        try {
            return new ValueNum(Files.getLastModifiedTime(file).toMillis());
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
