package passambler.pkg.net.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
    public void write(Value value) {
        try {
            socket.getOutputStream().write(value.getValue().toString().getBytes());
        } catch (IOException e) {
        }
    }

    @Override
    public Value read() {
        try {
            return new ValueStr(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
        } catch (IOException e) {
            return Value.VALUE_NIL;
        }
    }
}
