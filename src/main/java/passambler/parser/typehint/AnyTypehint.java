package passambler.parser.typehint;

import passambler.value.Value;

public class AnyTypehint implements Typehint {
    @Override
    public boolean matches(Value value) {
        return true;
    }
}