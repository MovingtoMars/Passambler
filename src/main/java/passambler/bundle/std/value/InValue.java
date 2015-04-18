package passambler.bundle.std.value;

import java.io.IOException;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.ErrorValue;
import passambler.value.ReadableValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class InValue extends Value implements ReadableValue {
    @Override
    public Value read(boolean line) throws EngineException {
        try {
            String data = null;

            if (System.console() != null) {
                if (line) {
                    data = System.console().readLine();
                } else {
                    data = String.valueOf(Character.toChars(System.console().reader().read()));
                }
            }

            if (data == null) {
                throw new ErrorException(new ErrorValue("No console found"));
            }

            return new StringValue(data);
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
