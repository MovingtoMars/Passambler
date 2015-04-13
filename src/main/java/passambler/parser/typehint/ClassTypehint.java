package passambler.parser.typehint;

import passambler.value.Value;
import passambler.value.ClassValue;

public class ClassTypehint implements Typehint {
    private String name;

    public ClassTypehint(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(Value value) {
        if (!(value instanceof ClassValue)) {
            return false;
        }
        
        return ((ClassValue) value).getName().equals(name);
    }
}
