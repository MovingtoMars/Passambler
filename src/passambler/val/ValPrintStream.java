package passambler.val;

import java.io.PrintStream;
import passambler.parser.Stream;

public class ValPrintStream extends Val implements Stream {
    private PrintStream printStream;

    public ValPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void onStream(Val value) {
        this.printStream.print(value);
    }
}
