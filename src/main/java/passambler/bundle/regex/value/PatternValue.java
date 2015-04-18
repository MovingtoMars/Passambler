package passambler.bundle.regex.value;

import java.util.regex.Pattern;
import passambler.value.Value;

public class PatternValue extends Value {
    public PatternValue(Pattern pattern) {
        setValue(pattern);
    }

    @Override
    public Pattern getValue() {
        return (Pattern) value;
    }
}
