package passambler.module.crypto.function;

import org.jasypt.digest.StandardStringDigester;
import passambler.exception.EngineException;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class DigestFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        StandardStringDigester digester = new StandardStringDigester();

        digester.setAlgorithm(((StringValue) context.getArgument(0)).toString());

        return new StringValue(digester.digest(((StringValue) context.getArgument(1)).toString()));
    }
}
