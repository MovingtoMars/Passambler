package passambler.value;

import passambler.exception.EngineException;
import passambler.value.function.Function;

public class ClassValue extends Value {
    private String name;

    public ClassValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (hasProperty("to_str")) {
            Value function = getProperty("to_str").getValue();

            if (function instanceof Function && ((Function) function).getArguments() == 0) {
                try {
                    Value result = ((Function) function).invoke(null);

                    if (result != null) {
                        return result.toString();
                    }
                } catch (EngineException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return name;
    }
}
