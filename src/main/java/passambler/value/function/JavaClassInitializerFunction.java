package passambler.value.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.parser.Block;
import passambler.parser.Scope;
import passambler.util.ValueUtils;
import passambler.value.Value;

public class JavaClassInitializerFunction extends UserFunction {
    private Class cls;

    public JavaClassInitializerFunction(Class cls) {
        super(new Block(new Scope()), new ArrayList());
        this.cls = cls;
    }

    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Value value = new Value();

        Object obj;
        try {
            obj = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ErrorException(e);
        }

        int i = 0;
        String lastName = "";
        for (Method method : cls.getMethods()) {
            if (lastName == method.getName()) {
                i++;
            } else {
                i = 0;
            }
            value.setProperty(method.getName() + i, new UserFunction(new Block(new Scope()), new ArrayList()) {
                @Override
                public int getArguments() {
                    return -1;
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    return true;
                }

                @Override
                public Value invoke(FunctionContext context) throws EngineException {
                    Object objs[] = new Object[context.getArguments().length];

                    for (int i = 0; i < context.getArguments().length; ++i) {
                        objs[i] = ValueUtils.toObject(context.getArgument(i));
                    }

                    try {
                        return ValueUtils.toValue(method.invoke(obj, objs));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new ErrorException(e);
                    }
                }
            });
            lastName = method.getName();
        }

        return value;
    }
}
