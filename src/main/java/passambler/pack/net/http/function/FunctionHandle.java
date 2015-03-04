package passambler.pack.net.http.function;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pack.net.http.value.ValueRequest;
import passambler.pack.net.http.value.ValueResponse;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionHandle extends Function {
    private UriHttpRequestHandlerMapper mapper;
    
    public FunctionHandle(UriHttpRequestHandlerMapper mapper) {
        this.mapper = mapper;
    }
    
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
    public Value invoke(FunctionContext context) throws ParserException {
        mapper.register(((ValueStr) context.getArgument(0)).getValue(), context.getArgument(1) instanceof HttpRequestHandler ? (HttpRequestHandler) context.getArgument(1) : createHandlerFromFunction(context.getParser(), (Function) context.getArgument(1)));

        return null;
    }

    private HttpRequestHandler createHandlerFromFunction(Parser parser, Function function) {
        return (HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) -> {
            try {
                ValueRequest request = new ValueRequest(httpContext, httpRequest);
                ValueResponse response = new ValueResponse(httpContext, httpResponse);

                function.invoke(new FunctionContext(parser, new Value[] { request, response }));

                httpResponse.setHeaders(response.getHeaders());
                httpResponse.setStatusCode(response.getStatus());
                httpResponse.setEntity(new StringEntity(response.getResponseData(), ContentType.create("text/html", "UTF-8")));
            } catch (ParserException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
