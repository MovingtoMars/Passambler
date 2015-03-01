package passambler.pkg.http.function;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pkg.http.PackageHttp;
import passambler.pkg.http.value.ValueRequest;
import passambler.pkg.http.value.ValueResponse;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionHandle extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueStr;
        }

        return value instanceof Function || value instanceof HttpRequestHandler;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        PackageHttp.REQUEST_HANDLER.register(((ValueStr) arguments[0]).getValue(), arguments[1] instanceof HttpRequestHandler ? (HttpRequestHandler) arguments[1] : createHandlerFromFunction(parser, (Function) arguments[1]));

        return null;
    }

    private HttpRequestHandler createHandlerFromFunction(Parser parser, Function function) {
        return (HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) -> {
            try {
                ValueRequest request = new ValueRequest(httpContext, httpRequest);
                ValueResponse response = new ValueResponse(httpContext, httpResponse);

                function.invoke(parser, new Value[]{request, response});

                httpResponse.setHeaders(response.getHeaders());
                httpResponse.setStatusCode(response.getStatus());
                httpResponse.setEntity(new StringEntity(response.getResponseData(), ContentType.create("text/html", "UTF-8")));
            } catch (ParserException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
