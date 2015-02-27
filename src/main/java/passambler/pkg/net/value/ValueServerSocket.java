package passambler.pkg.net.value;

import java.io.IOException;
import java.net.ServerSocket;
import passambler.value.Value;

public class ValueServerSocket extends Value {
    private ServerSocket socket;
    
    public ValueServerSocket(int port) throws IOException {
        this(new ServerSocket(port));
    }
    
    public ValueServerSocket(ServerSocket socket) {
        this.socket = socket;
    }
    
    @Override
    public ServerSocket getValue() {
        return socket;
    }
}
