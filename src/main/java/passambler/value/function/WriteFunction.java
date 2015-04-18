package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.WriteableValue;

public class WriteFunction extends Value implements Function {
    private WriteableValue handler;

    private boolean line;

    public WriteFunction(WriteableValue handler, boolean line) {
        this.handler = handler;
        this.line = line;
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
            writeable.write(line, context.getArgument(i));
        }

        return null;
    }
}
