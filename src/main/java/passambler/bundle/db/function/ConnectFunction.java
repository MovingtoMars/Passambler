package passambler.bundle.db.function;

import org.sql2o.Sql2o;
import passambler.bundle.db.DbBundle;
import passambler.exception.EngineException;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class ConnectFunction extends Value implements Function {
    private DbBundle bundle;

    public ConnectFunction(DbBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        bundle.setConnection(new Sql2o(((StringValue) context.getArgument(0)).toString(),
            ((StringValue) context.getArgument(1)).toString(),
            ((StringValue) context.getArgument(2)).toString()));

        return null;
    }
}
