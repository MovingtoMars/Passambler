package passambler.pack.http;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

public class WorkerThread extends Thread {
    private HttpService httpService;
    private HttpServerConnection connection;

    public WorkerThread(HttpService service, HttpServerConnection connection) {
        this.httpService = service;
        this.connection = connection;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext(null);

        try {
            while (!Thread.interrupted() && connection.isOpen()) {
                httpService.handleRequest(connection, context);
            }
        } catch (IOException | HttpException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.shutdown();
            } catch (IOException e) {
            }
        }
    }
}
