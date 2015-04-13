package passambler.pack.net.value;

import java.io.IOException;
import java.net.ServerSocket;
import passambler.value.Value;

public class ServerSocketValue extends Value {
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
}
