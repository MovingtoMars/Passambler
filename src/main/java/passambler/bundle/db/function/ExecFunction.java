package passambler.bundle.db.function;

import org.sql2o.Sql2oException;
import passambler.bundle.db.value.QueryValue;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class ExecFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof QueryValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            ((QueryValue) context.getArgument(0)).getValue().executeUpdate();
        } catch (Sql2oException e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
