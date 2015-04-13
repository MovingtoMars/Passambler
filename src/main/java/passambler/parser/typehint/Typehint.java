package passambler.parser.typehint;

import passambler.value.Value;

public interface Typehint {
    public boolean matches(Value value);
}
