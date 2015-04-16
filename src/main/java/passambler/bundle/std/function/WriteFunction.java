package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.value.WriteableValue;

public class WriteFunction extends Value implements Function {
    private WriteableValue handler;

    private boolean newLine;

    public WriteFunction(WriteableValue handler, boolean newLine) {
        this.handler = handler;
        this.newLine = newLine;
    }

    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        WriteableValue handler = this.handler;

        if (context.getArguments().length > 0 && context.getArgument(0) instanceof WriteableValue) {
            handler = (WriteableValue) context.getArgument(0);
        }

        for (Value argument : context.getArguments()) {
            if (argument == context.getArgument(0) && argument instanceof WriteableValue) {
                continue;
            }

            handler.write(argument);

            if (argument != context.getArgument(context.getArguments().length - 1)) {
                handler.write(new StringValue(" "));
            }
        }

        if (newLine) {
            handler.write(new StringValue("\n"));
        }

        return null;
    }
}
