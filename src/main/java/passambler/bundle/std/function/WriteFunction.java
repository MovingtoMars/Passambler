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
        int x = 0;

        WriteableValue writeable = this.handler;

        if (context.getArguments().length > 0 && context.getArgument(0) instanceof WriteableValue) {
            writeable = (WriteableValue) context.getArgument(0);
            x++;
        }

        for (int i = x; i < context.getArguments().length; ++i) {
            writeable.write(context.getArgument(i));
        }

        if (newLine) {
            writeable.write(new StringValue("\n"));
        }

        return null;
    }
}
