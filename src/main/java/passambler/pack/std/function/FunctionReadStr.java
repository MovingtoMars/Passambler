package passambler.pack.std.function;

import java.util.Scanner;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.StringValue;

public class FunctionReadStr extends Value implements Function {
    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Scanner scanner = new Scanner(System.in);

        return new StringValue(scanner.nextLine());
    }
}
