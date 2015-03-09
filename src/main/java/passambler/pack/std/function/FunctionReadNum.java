package passambler.pack.std.function;

import java.util.Scanner;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionReadNum extends Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return argument == 0 && value instanceof ValueBool;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        boolean floating = context.getArguments().length == 0 ? false : ((ValueBool) context.getArgument(0)).getValue();

        Scanner scanner = new Scanner(System.in);

        try {
            if (floating) {
                return new ValueNum(scanner.nextDouble());
            } else {
                return new ValueNum(scanner.nextInt());
            }
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
