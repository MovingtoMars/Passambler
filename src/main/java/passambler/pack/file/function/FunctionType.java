package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionType extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) throws ParserException {
        try {
            return new ValueStr(Files.probeContentType(file));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
