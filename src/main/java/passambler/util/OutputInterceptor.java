package passambler.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class OutputInterceptor {
    private static PrintStream stream;

    private static StringBuilder output = new StringBuilder();

    public static void start() {
        stream = System.out;

        System.setOut(new PrintStream(new ByteArrayOutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                output.append(new String(b, off, len));
            }
        }));
    }

    public static void stop() {
        output = new StringBuilder();

        System.setOut(stream);
    }

    public static String getOutput() {
        // This will trim any \n if there is one
        return output.toString().substring(0, output.toString().length() - (output.toString().charAt(output.toString().length() - 1) == '\n' ? 1 : 0));
    }
}
