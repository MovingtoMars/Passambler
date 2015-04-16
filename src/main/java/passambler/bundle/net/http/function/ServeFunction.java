package passambler.bundle.net.http.function;

import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.Main;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.bundle.net.http.thread.RequestListenerThread;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.NumberValue;

public class ServeFunction extends Value implements Function {
    private UriHttpRequestHandlerMapper mapper;

    public ServeFunction(UriHttpRequestHandlerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("Passambler/" + Main.VERSION))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

            HttpService httpService = new HttpService(httpProcessor, mapper);

            Thread requestListener = new RequestListenerThread(((NumberValue) context.getArgument(0)).getValue().intValue(), httpService);
            requestListener.setDaemon(false);
            requestListener.start();
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
