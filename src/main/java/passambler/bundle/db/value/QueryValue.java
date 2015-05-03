package passambler.bundle.db.value;

import org.sql2o.Query;
import passambler.value.Value;

public class QueryValue extends Value {
    public QueryValue(Query query) {
        setValue(query);
    }

    @Override
    public Query getValue() {
        return (Query) value;
    }
}
