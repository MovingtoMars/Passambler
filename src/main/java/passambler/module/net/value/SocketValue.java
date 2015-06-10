package passambler.module.net.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.util.ValueConstants;
import passambler.value.BooleanValue;
import passambler.value.CloseableValue;
import passambler.value.Property;
import passambler.value.Readable;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.value.Writeable;

public class SocketValue extends Value implements Writeable, Readable, CloseableValue {
    private Socket socket;

    private BufferedReader reader;
    private PrintWriter writer;

    private boolean closed = false;

    public SocketValue(Socket socket) {
        this.socket = socket;

        this.setProperty("host_addr", new StringValue(socket.getInetAddress().getHostAddress()));
        this.setProperty("host_name", new StringValue(socket.getInetAddress().getHostName()));
        this.setProperty("closed", new Property() {
            @Override
            public Value getValue() {
                return new BooleanValue(closed);
            }
        });
    }

    @Override
    public Socket getValue() {
        return socket;
    }

    @Override
    public void write(boolean line, Value value) throws EngineException {
        if (reader == null || writer == null) {
            setUp();
        }

        if (line) {
            writer.println(value.toString());
        } else {
            writer.print(value.toString());
        }
    }

    @Override
    public Value read(boolean line) throws EngineException {
        if (reader == null || writer == null) {
            setUp();
        }

        try {
            String dataRead = null;

            if (line) {
                dataRead = reader.readLine();
            } else {
                int read = reader.read();

                if (read >= 0) {
                    dataRead = String.valueOf(Character.toChars(read));
                }
            }

            closed = dataRead == null;

            return closed ? ValueConstants.NIL : new StringValue(dataRead);
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }

    @Override
    public void close() throws EngineException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }

    private void setUp() throws EngineException {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
