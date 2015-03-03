package passambler.pack.net.http.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.HttpService;

public class RequestListenerThread extends Thread {
    private HttpConnectionFactory<DefaultBHttpServerConnection> connectionFactory;
    private ServerSocket serverSocket;
    private HttpService httpService;

    public RequestListenerThread(int port, HttpService service) throws IOException {
        this.connectionFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
        this.serverSocket = new ServerSocket(port);
        this.httpService = service;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = serverSocket.accept();

                Thread worker = new WorkerThread(httpService, connectionFactory.createConnection(socket));
                worker.setDaemon(true);
                worker.start();
            } catch (Exception e) {
                break;
            }
        }
    }
}
