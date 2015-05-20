package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.Writeable;

public class WriteFunction extends Value implements Function {
    private Writeable defaultHandler;

    private boolean line;

    public WriteFunction(Writeable defaultHandler, boolean line) {
        this.defaultHandler = defaultHandler;
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

        Writeable handler = defaultHandler;

        if (context.getArguments().length > 0 && context.getArgument(0) instanceof Writeable) {
            handler = (Writeable) context.getArgument(0);

            x++;
        }

        for (int i = x; i < context.getArguments().length; ++i) {
            handler.write(line, context.getArgument(i));
        }

        return null;
    }
}
