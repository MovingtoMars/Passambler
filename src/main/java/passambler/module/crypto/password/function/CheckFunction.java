package passambler.module.crypto.password.function;

import org.jasypt.util.password.StrongPasswordEncryptor;
import passambler.exception.EngineException;
import passambler.value.BooleanValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class CheckFunction extends Value implements Function {
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
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

        return new BooleanValue(encryptor.checkPassword(((StringValue) context.getArgument(0)).toString(), ((StringValue) context.getArgument(1)).toString()));
    }
}
