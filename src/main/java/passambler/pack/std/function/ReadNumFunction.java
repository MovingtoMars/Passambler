package passambler.pack.std.function;

import java.util.Scanner;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.BooleanValue;
import passambler.value.NumberValue;

public class ReadNumFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return argument == 0 && value instanceof BooleanValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        boolean floating = context.getArguments().length == 0 ? false : ((BooleanValue) context.getArgument(0)).getValue();

        Scanner scanner = new Scanner(System.in);

        try {
            return new NumberValue(floating ? scanner.nextDouble() : scanner.nextInt());
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
