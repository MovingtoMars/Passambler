package passambler.bundle.db.function;

import org.sql2o.Sql2oException;
import org.sql2o.data.Table;
import passambler.bundle.db.value.QueryValue;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class FetchFunction extends Value implements Function {
    private boolean one;

    public FetchFunction(boolean one) {
        this.one = one;
    }

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
        ListValue results = new ListValue();

        try {
            Table table = ((QueryValue) context.getArgument(0)).getValue().executeAndFetchTable();

            table.rows().stream().forEach((row) -> {
                DictValue item = new DictValue();

                table.columns().stream().forEach((column) -> {
                    item.setEntry(new StringValue(column.getName()), Value.toValue(row.getObject(column.getName())));
                });

                results.getValue().add(item);
            });
        } catch (Sql2oException e) {
            throw new ErrorException(e);
        }

        return one ? (results.getValue().size() > 0 ? results.getValue().get(0) : Value.VALUE_NIL) : results;
    }
}
