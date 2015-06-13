package passambler.module.crypto.password;

import java.util.Map;
import passambler.module.Module;
import passambler.module.crypto.password.function.EncryptFunction;
import passambler.module.crypto.password.function.CheckFunction;
import passambler.value.Value;

public class PasswordModule implements Module {
    @Override
    public String getId() {
        return "password";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Encrypt", new EncryptFunction());
        symbols.put("Check", new CheckFunction());
    }
}
