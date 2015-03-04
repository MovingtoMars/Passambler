package passambler.pack.std.function;

import java.util.Scanner;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionReadStr extends Function {
    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        Scanner scanner = new Scanner(System.in);

        return new ValueStr(scanner.nextLine());
    }
}
