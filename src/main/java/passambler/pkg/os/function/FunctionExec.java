package passambler.pkg.os.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionExec extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        Process process;

        try {
            process = Runtime.getRuntime().exec(((ValueStr) arguments[0]).getValue());
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

            return new ValueStr(output.toString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}