package passambler.pack.os.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.StringValue;

public class FunctionExec extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Process process;

        try {
            process = Runtime.getRuntime().exec(((StringValue) context.getArgument(0)).getValue());
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();

            String line = reader.readLine();

            while (line != null) {
                output.append(line);

                line = reader.readLine();

                if (line != null) {
                    output.append("\n");
                }
            }

            return new StringValue(output.toString());
        } catch (IOException | InterruptedException e) {
            throw new ErrorException(e);
        }
    }
}
