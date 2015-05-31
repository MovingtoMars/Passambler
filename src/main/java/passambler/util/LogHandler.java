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

        builder.append(record.getLevel().toString().toLowerCase()).append(Constants.ANSI_RESET);

        builder.append(": ");

        writeMessage(record.getMessage(), builder);

        if (record.getThrown() != null) {
            builder.append(": ");

            writeMessage(record.getThrown().getMessage(), builder);

            if (Constants.DEBUG) {
                for (StackTraceElement stackTrace : record.getThrown().getStackTrace()) {
                    builder.append("\n").append(stackTrace.toString());
                }
            }
        }

        System.err.println(builder.toString());

        if (record.getLevel() == Level.SEVERE) {
            System.exit(-1);
        }
    }

    private void writeMessage(String message, StringBuilder builder) {
        boolean backtick = false;

        for (char c : message.toCharArray()) {
            if (c == '`') {
                backtick = !backtick;

                if (backtick) {
                    builder.append(Constants.ANSI_PURPLE);
                }

                builder.append(c);

                if (!backtick) {
                    builder.append(Constants.ANSI_RESET);
                }
            } else {
                builder.append(c);
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
