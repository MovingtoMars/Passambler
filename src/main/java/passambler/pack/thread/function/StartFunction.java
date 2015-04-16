package passambler.pack.thread.function;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.ErrorValue;
import passambler.value.Value;

public class StartFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof Function;
        }

        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        new Runnable() {
            @Override
            public void run() {
                try {
                    Function function = (Function) context.getArgument(0);

                    if (function.getArguments() != context.getArguments().length - 1) {
                        throw new ErrorException(new ErrorValue("Thread callback expected %d arguments, %d given", function.getArguments(), context.getArguments().length - 1));
                    }

                    List<Value> values = new ArrayList<>();

                    for (int i = 1; i < context.getArguments().length; ++i) {
                        values.add(context.getArgument(i));
                    }

                    function.invoke(new FunctionContext(context.getParser(), values.toArray(new Value[values.size()])));
                } catch (EngineException e) {
                    throw new RuntimeException(e);
                }
            }
        }.run();

        return null;
    }
}
