package passambler.procedure;

import passambler.procedure.ProcedureRead.ReadType;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public interface Procedure {
    public static ProcedureWrite PROCEDURE_WRITE = new ProcedureWrite(false);
    public static ProcedureWrite PROCEDURE_WRITELN = new ProcedureWrite(true);
        
    public static ProcedureExit PROCEDURE_EXIT = new ProcedureExit();
    public static ProcedureRandom PROCEDURE_RANDOM = new ProcedureRandom();

    public static ProcedureRead PROCEDURE_READSTR = new ProcedureRead(ReadType.STRING);
    public static ProcedureRead PROCEDURE_READINT = new ProcedureRead(ReadType.INTEGER);
    public static ProcedureRead PROCEDURE_READDOUBLE = new ProcedureRead(ReadType.DOUBLE);
    
    public static ProcedureMath PROCEDURE_SIN = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.sin(value);
        }
    };
    
    public static ProcedureMath PROCEDURE_COS = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.cos(value);
        }
    };
    
    public static ProcedureMath PROCEDURE_TAN = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.tan(value);
        }
    };
    
    public static ProcedureMath PROCEDURE_LOG = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.log(value);
        }
    };
    
    public static ProcedureMath PROCEDURE_LOG10 = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.log10(value);
        }
    };
    
    public static ProcedureMath PROCEDURE_SQRT = new ProcedureMath() {
        @Override
        public double getValue(double value) {
            return Math.sqrt(value);
        }
    };

    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(Parser parser, Value... arguments) throws ParserException;
}
