package passambler.extension.std.procedure;

import passambler.extension.std.value.ValueOutStream;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;

public class ProcedureWrite implements Procedure {
    protected boolean newLine;

    public ProcedureWrite(boolean newLine) {
        this.newLine = newLine;
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
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueOutStream out = arguments.length > 0 && arguments[0] instanceof ValueOutStream ? (ValueOutStream) arguments[0] : new ValueOutStream(System.out);

        for (Value argument : arguments) {
            if (argument == arguments[0] && argument instanceof ValueOutStream) {
                continue;
            }

            out.getStream().print(argument);
        }

        if (newLine) {
            out.getStream().println();
        }

        return null;
    }
}
