package passambler.module.db;

import java.util.Map;
import org.sql2o.Sql2o;
import passambler.module.Module;
import passambler.module.db.function.*;
import passambler.value.Value;

public class DbModule implements Module {
    private Sql2o connection;

    @Override
    public String getId() {
        return "db";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Connect", new ConnectFunction(this));
        symbols.put("Query", new QueryFunction(this));
        symbols.put("Exec", new ExecFunction());
        symbols.put("Fetch", new FetchFunction(false));
        symbols.put("FetchOne", new FetchFunction(true));
    }

    public void setConnection(Sql2o connection) {
        this.connection = connection;
    }

    public Sql2o getConnection() {
        return connection;
    }
}
