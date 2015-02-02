package passambler;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        PrintStream output = System.out;
        
        if (record.getLevel() == Level.SEVERE) {
            output = System.err;
        }
        
        if (record.getThrown() != null) {
            output.println(String.format("%s: %s", record.getMessage(), record.getThrown().getMessage()));
        } else {
            output.println(String.format("%s", record.getMessage()));
        }
    }

    @Override
    public void flush() {
        
    }

    @Override
    public void close() throws SecurityException {
        
    }
}