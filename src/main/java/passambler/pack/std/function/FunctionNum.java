package passambler.pack.std.function;

import java.math.BigDecimal;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionNum extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            return new ValueNum(new BigDecimal(context.getArgument(0).toString()));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
