package passambler.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        if (record.getLevel() == Level.SEVERE) {
            builder.append(Constants.ANSI_RED);
        } else if (record.getLevel() == Level.INFO) {
            builder.append(Constants.ANSI_BLUE);
        } else if (record.getLevel() == Level.WARNING) {
            builder.append(Constants.ANSI_YELLOW);
        }

        builder.append(record.getLevel().toString().toLowerCase());
        builder.append(Constants.ANSI_RESET);

        builder.append(": ").append(record.getMessage());

        if (record.getThrown() != null) {
            builder.append(": ").append(record.getThrown().getMessage());

            if (Constants.DEBUG) {
                builder.append("\n");

                for (StackTraceElement stackTrace : record.getThrown().getStackTrace()) {
                    builder.append(stackTrace.toString()).append("\n");
                }
            }
        }

        System.err.println(builder.toString());

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
