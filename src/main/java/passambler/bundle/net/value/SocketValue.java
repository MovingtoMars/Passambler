package passambler.bundle.net.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.CloseableValue;
import passambler.value.ReadableValue;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.value.WriteableValue;

public class SocketValue extends Value implements WriteableValue, ReadableValue, CloseableValue {
    private Socket socket;

    public SocketValue(Socket socket) {
        this.socket = socket;

        this.setProperty("HostAddr", new StringValue(socket.getInetAddress().getHostAddress()));
        this.setProperty("HostName", new StringValue(socket.getInetAddress().getHostName()));
    }

    @Override
    public Socket getValue() {
        return socket;
    }

    @Override
    public void write(Value value) throws EngineException {
        try {
            socket.getOutputStream().write(value.getValue().toString().getBytes());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }

    @Override
    public Value read() throws EngineException {
        try {
            return new StringValue(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
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
}
