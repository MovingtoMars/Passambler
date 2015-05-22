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
            Value strFunction = getProperty("to_str").getValue();

            if (strFunction instanceof Function && ((Function) strFunction).getArguments() == 0) {
                try {
                    return ((Function) strFunction).invoke(null).toString();
                } catch (EngineException e) {

                }
            }
        }

        return name;
    }
}
