package passambler.module.net.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.CloseableValue;
import passambler.value.Readable;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.value.Writeable;

public class SocketValue extends Value implements Writeable, Readable, CloseableValue {
    private Socket socket;

    private BufferedReader reader;
    private PrintWriter writer;

    public SocketValue(Socket socket) {
        this.socket = socket;

        this.setProperty("host_addr", new StringValue(socket.getInetAddress().getHostAddress()));
        this.setProperty("host_name", new StringValue(socket.getInetAddress().getHostName()));
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
            return new StringValue(line ? reader.readLine() : String.valueOf(Character.toChars(reader.read())));
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
