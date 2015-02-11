package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public interface Function {
    public static FunctionWrite FUNCTION_WRITE = new FunctionWrite(false);
    public static FunctionWrite FUNCTION_WRITELN = new FunctionWrite(true);
    
    public static FunctionMinMax FUNCTION_MIN = new FunctionMinMax(true);
    public static FunctionMinMax FUNCTION_MAX = new FunctionMinMax(false);
        
    public static FunctionExit FUNCTION_EXIT = new FunctionExit();
    public static FunctionRandom FUNCTION_RANDOM = new FunctionRandom();
    public static FunctionSqrt FUNCTION_SQRT = new FunctionSqrt();
    
    public static FunctionBasicMath FUNCTION_ABS = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.abs(input);
        }
    };
    
    public static FunctionBasicMath FUNCTION_SIN = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.sin(input);
        }
    };
    
    public static FunctionBasicMath FUNCTION_COS = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.cos(input);
        }
    };
    
    public static FunctionBasicMath FUNCTION_TAN = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.tan(input);
        }
    };
    
    public static FunctionBasicMath FUNCTION_FLOOR = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.floor(input);
        }
    };
    
    public static FunctionBasicMath FUNCTION_CEIL = new FunctionBasicMath() {
        @Override
        public double getValue(double input) {
            return Math.ceil(input);
        }
    };

    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(Parser parser, Value... arguments) throws ParserException;
}
