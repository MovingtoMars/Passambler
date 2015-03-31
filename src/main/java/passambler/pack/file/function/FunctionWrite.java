package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class FunctionWrite extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            String fileName = ((ValueStr) context.getArgument(0)).getValue();

            for (int i = 1; i < context.getArguments().length; ++i) {
                Files.write(Paths.get(fileName), ((ValueStr) context.getArgument(i)).getValue().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return new ValueBool(true);
    }
}
