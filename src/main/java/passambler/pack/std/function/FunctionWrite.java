package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueStr;
import passambler.value.WriteHandler;

public class FunctionWrite extends Value implements Function {
    private WriteHandler handler;

    private boolean newLine;

    public FunctionWrite(WriteHandler handler, boolean newLine) {
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
        WriteHandler handler = this.handler;

        if (context.getArguments().length > 0 && context.getArgument(0) instanceof WriteHandler) {
            handler = (WriteHandler) context.getArgument(0);
        }

        for (Value argument : context.getArguments()) {
            if (argument == context.getArgument(0) && argument instanceof WriteHandler) {
                continue;
            }

            handler.write(argument);

            if (argument != context.getArgument(context.getArguments().length - 1)) {
                handler.write(new ValueStr(" "));
            }
        }

        if (newLine) {
            handler.write(new ValueStr("\n"));
        }

        return null;
    }
}
