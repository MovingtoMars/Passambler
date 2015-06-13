package passambler.module.crypto;

import java.util.Map;
import passambler.module.Module;
import passambler.module.crypto.function.DecryptFunction;
import passambler.module.crypto.function.DigestFunction;
import passambler.module.crypto.function.EncryptFunction;
import passambler.value.Value;

public class CryptoModule implements Module {
    @Override
    public String getId() {
        return "crypto";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Encrypt", new EncryptFunction());
        symbols.put("Decrypt", new DecryptFunction());
        symbols.put("Digest", new DigestFunction());
    }
}
