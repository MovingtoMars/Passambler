package passambler.module.db.function;

import org.sql2o.Query;
import passambler.module.db.DbModule;
import passambler.module.db.value.QueryValue;
import passambler.exception.EngineException;
import passambler.value.DictValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class QueryFunction extends Value implements Function {
    private DbModule module;

    public QueryFunction(DbModule module) {
        this.module = module;
    }

    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        }

        return value instanceof DictValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Query query = module.getConnection().createQuery(((StringValue) context.getArgument(0)).toString());

        if (context.getArguments().length == 2) {
            ((DictValue) context.getArgument(1)).getValue().entrySet().stream().forEach((parameter) -> {
                query.addParameter(parameter.getKey().toString(), parameter.getValue().toString());
            });
        }

        return new QueryValue(query);
    }
}
