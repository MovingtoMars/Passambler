package passambler.pack.net.http.value;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Locale;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import passambler.value.Value;

public class FileHandlerValue extends Value implements HttpRequestHandler {
    private String docRoot;

    public FileHandlerValue(String docRoot) {
        this.docRoot = docRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);

        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(String.format("Method '%s' not supported", method));
        }

        String target = request.getRequestLine().getUri();

        File file = new File(docRoot, URLDecoder.decode(target, "UTF-8"));

        if (!file.exists()) {
            StringEntity entity = new StringEntity("<html><body><h1>File " + file.getPath() + " not found</h1></body></html>", ContentType.create("text/html", "UTF-8"));

            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setEntity(entity);
        } else if (!file.canRead() || file.isDirectory()) {
            StringEntity entity = new StringEntity("<html><body><h1>Access denied</h1></body></html>", ContentType.create("text/html", "UTF-8"));

            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            response.setEntity(entity);
        } else {
            FileEntity body = new FileEntity(file, ContentType.create(Files.probeContentType(file.toPath())));

            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(body);
        }
    }
}
