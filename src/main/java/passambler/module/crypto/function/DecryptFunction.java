package passambler.module.crypto.function;

import org.jasypt.encryption.pbe.StandardPBEBigDecimalEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class DecryptFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        }

        return value instanceof StringValue || value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        String key = ((StringValue) context.getArgument(0)).toString();

        try {
            if (context.getArgument(1) instanceof StringValue) {
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(key);

                return new StringValue(encryptor.decrypt(((StringValue) context.getArgument(1)).toString()));
            } else if (context.getArgument(1) instanceof NumberValue) {
                StandardPBEBigDecimalEncryptor encryptor = new StandardPBEBigDecimalEncryptor();
                encryptor.setPassword(key);

                return new NumberValue(encryptor.decrypt(((NumberValue) context.getArgument(1)).getValue()));
            }
        } catch (EncryptionOperationNotPossibleException e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
