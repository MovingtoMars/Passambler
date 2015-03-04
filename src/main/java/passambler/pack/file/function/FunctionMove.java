package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class FunctionMove extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            Path file = Paths.get(((ValueStr) context.getArgument(0)).getValue());
            Path destination = Paths.get(((ValueStr) context.getArgument(1)).getValue());

            Files.move(file, destination);
        } catch (Exception e) {
            return new ValueBool(false);
        }

        return new ValueBool(true);
    }
}
