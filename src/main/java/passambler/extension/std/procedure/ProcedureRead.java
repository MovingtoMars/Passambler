package passambler.extension.std.procedure;

import java.util.Scanner;
import passambler.extension.std.value.ValueInStream;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class ProcedureRead implements Procedure {
    public enum ReadType {
        STRING, INTEGER, DOUBLE;

        Value createValue(Scanner scanner) {
            switch (this) {
                case STRING:
                    return new ValueStr(scanner.nextLine());
                case INTEGER:
                    return new ValueNum(scanner.nextInt());
                case DOUBLE:
                    return new ValueNum(scanner.nextDouble());
            }

            return null;
        }
    };

    private ReadType type;

    public ProcedureRead(ReadType type) {
        this.type = type;
    }

    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return argument == 0 && value instanceof ValueInStream;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueInStream in = new ValueInStream(System.in);

        if (arguments.length > 0) {
            in = (ValueInStream) arguments[0];
        }

        return type.createValue(new Scanner(in.getStream()));
    }
}
