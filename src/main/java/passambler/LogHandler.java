package passambler;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
    private boolean stacktrace;

    public LogHandler(boolean stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Override
    public void publish(LogRecord record) {
        PrintStream output = System.out;

        if (record.getLevel() == Level.SEVERE) {
            output = System.err;
        }

        if (record.getThrown() != null) {
            output.println(String.format("%s: %s", record.getMessage(), record.getThrown().getMessage()));

            if (stacktrace) {
                output.println("Stacktrace:");

                for (StackTraceElement stackTrace : record.getThrown().getStackTrace()) {
                    output.println(" @ " + stackTrace.toString());
                }
            }
        } else {
            output.println(String.format("%s", record.getMessage()));
        }

        if (record.getLevel() == Level.SEVERE) {
            System.exit(-1);
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
