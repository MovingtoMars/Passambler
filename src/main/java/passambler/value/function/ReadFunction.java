package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Readable;
import passambler.value.Value;

public class ReadFunction extends Value implements Function {
    private Readable defaultHandler;

    private boolean line;

    public ReadFunction(Readable defaultHandler, boolean line) {
        this.defaultHandler = defaultHandler;
        this.line = line;
    }

    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof Readable;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Readable handler = defaultHandler;

        if (context.getArguments().length > 0 && context.getArgument(0) instanceof Readable) {
            handler = (Readable) context.getArgument(0);
        }

        return handler.read(line);
    }
}
