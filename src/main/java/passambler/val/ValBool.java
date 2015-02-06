package passambler.val;

import java.util.Objects;
import passambler.scanner.Token;

public class ValBool extends Val {
    public ValBool(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }
    
    @Override
    public Val onOperator(Val value, Token.Type tokenType) {
        if (value instanceof ValBool) {
            switch (tokenType) {
                case AND:
                    return new ValBool(Objects.equals(getValue(), ((ValBool) value).getValue()));
            }
        }
        
        return super.onOperator(value, tokenType);
    }
}
