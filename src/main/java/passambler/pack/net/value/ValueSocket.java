package passambler.pack.net.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.ReadHandler;
import passambler.value.Value;
import passambler.value.ValueStr;
import passambler.value.WriteHandler;

public class ValueSocket extends Value implements WriteHandler, ReadHandler {
    private Socket socket;

    public ValueSocket(Socket socket) {
        this.socket = socket;

        this.setProperty("HostAddr", new ValueStr(socket.getInetAddress().getHostAddress()));
        this.setProperty("HostName", new ValueStr(socket.getInetAddress().getHostName()));
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
            return new ValueStr(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
