package passambler.pack.net.http.function;

import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.Main;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.pack.net.http.thread.RequestListenerThread;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionServe extends Function {
    private UriHttpRequestHandlerMapper mapper;

    public FunctionServe(UriHttpRequestHandlerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("Passambler/" + Main.VERSION))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

            HttpService httpService = new HttpService(httpProcessor, mapper);

            Thread requestListener = new RequestListenerThread(((ValueNum) context.getArgument(0)).getValue().intValue(), httpService);
            requestListener.setDaemon(false);
            requestListener.start();

            return new ValueBool(true);
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
