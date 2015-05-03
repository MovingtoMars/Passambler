package passambler.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class OutputRecorder {
    private static PrintStream stdOut;

    private static StringBuilder output = new StringBuilder();

    public static void record() {
        stdOut = System.out;

        System.setOut(new PrintStream(new ByteArrayOutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                output.append(new String(b, off, len));
            }
        }));
    }

    public static void stop() {
        output = new StringBuilder();

        System.setOut(stdOut);
    }

    public static String getOutput() {
        int to = output.toString().length();

        // Trim the last newline if there is one.
        if (output.toString().charAt(output.toString().length() - 1) == '\n') {
            to--;
        }

        return output.toString().substring(0, to);
    }
}
