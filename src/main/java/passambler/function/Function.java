package passambler.function;

import passambler.function.FunctionRead.ReadType;
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

    public static FunctionRead FUNCTION_READSTR = new FunctionRead(ReadType.STRING);
    public static FunctionRead FUNCTION_READINT = new FunctionRead(ReadType.INTEGER);
    public static FunctionRead FUNCTION_READDOUBLE = new FunctionRead(ReadType.DOUBLE);
    
    public static FunctionMath FUNCTION_SIN = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.sin(value);
        }
    };
    
    public static FunctionMath FUNCTION_COS = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.cos(value);
        }
    };
    
    public static FunctionMath FUNCTION_TAN = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.tan(value);
        }
    };
    
    public static FunctionMath FUNCTION_LOG = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.log(value);
        }
    };
    
    public static FunctionMath FUNCTION_LOG10 = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.log10(value);
        }
    };
    
    public static FunctionMath FUNCTION_SQRT = new FunctionMath() {
        @Override
        public double getValue(double value) {
            return Math.sqrt(value);
        }
    };

    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(Parser parser, Value... arguments) throws ParserException;
}
