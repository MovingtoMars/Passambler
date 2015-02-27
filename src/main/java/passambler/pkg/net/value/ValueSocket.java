package passambler.pkg.net.value;

import java.io.IOException;
import java.net.Socket;
import passambler.value.Value;
import passambler.value.ValueStr;
import passambler.value.ValueWriteHandler;

public class ValueSocket extends ValueWriteHandler {
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
    public void write(Value value) {
        try {
            socket.getOutputStream().write(value.getValue().toString().getBytes());
        } catch (IOException e) {
        }
    }
}
