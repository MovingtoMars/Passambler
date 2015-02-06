package passambler.val;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;

public class ValDict extends Val {
    Map<Val, Val> dict = new HashMap();
    
    public ValDict() {
        setProperty("set", new Function() {
            @Override
            public int getArguments() {
                return 2;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return true;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                set(arguments[0], arguments[1]);
                
                return null;
            }
        });
        
        setProperty("get", new Function() {
            @Override
            public int getArguments() {
                return 1;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return true;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return get(arguments[0]);
            }
        });
        
        setProperty("keys", new Function() {
            @Override
            public int getArguments() {
                return 0;
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return false;
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                ValList list = new ValList();
                
                dict.keySet().stream().forEach((key) -> {
                    list.add(key);
                });
                
                return list;
            }
        });
    }
    
    public void set(Val key, Val value) {
        dict.put(key, value);
    }
    
    public Val get(Val key) {
        return dict.get(key);
    }
    
    @Override
    public String toString() {
        return dict.toString();
    }
}
