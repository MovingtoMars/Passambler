package passambler.bundle.net.http.function;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.Parser;
import passambler.bundle.net.http.value.RequestValue;
import passambler.bundle.net.http.value.ResponseValue;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.StringValue;

public class HandleFunction extends Value implements Function {
    private UriHttpRequestHandlerMapper mapper;

    public HandleFunction(UriHttpRequestHandlerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        }

        return value instanceof Function || value instanceof HttpRequestHandler;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        mapper.register(((StringValue) context.getArgument(0)).getValue(), context.getArgument(1) instanceof HttpRequestHandler ? (HttpRequestHandler) context.getArgument(1) : createHandlerFromFunction(context.getParser(), (Function) context.getArgument(1)));

        return null;
    }

    private HttpRequestHandler createHandlerFromFunction(Parser parser, Function function) {
        return (HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) -> {
            try {
                RequestValue request = new RequestValue(httpContext, httpRequest);
                ResponseValue response = new ResponseValue(httpContext, httpResponse);

                function.invoke(new FunctionContext(parser, new Value[] { request, response }));

                httpResponse.setHeaders(response.getHeaders());
                httpResponse.setStatusCode(response.getStatus());
                httpResponse.setEntity(new StringEntity(response.getResponseData(), ContentType.create("text/html", "UTF-8")));
            } catch (EngineException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
