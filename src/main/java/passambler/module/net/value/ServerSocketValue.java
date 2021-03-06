package passambler.module.net.value;

import java.io.IOException;
import java.net.ServerSocket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.CloseableValue;
import passambler.value.Readable;
import passambler.value.Value;

public class ServerSocketValue extends Value implements Readable, CloseableValue {
    private ServerSocket socket;

    public ServerSocketValue(int port) throws IOException {
        this(new ServerSocket(port));
    }

    public ServerSocketValue(ServerSocket socket) {
        this.socket = socket;
    }

    @Override
    public ServerSocket getValue() {
        return socket;
    }

    @Override
    public void close() throws EngineException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }

    @Override
    public Value read(boolean line) throws EngineException {
        try {
            return new SocketValue(socket.accept());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
