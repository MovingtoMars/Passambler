package passambler.pack.http.function;

import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pack.http.PackageHttp;
import passambler.pack.http.RequestListenerThread;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionServe extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        try {
            HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("PassamblerHTTP/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

            HttpService httpService = new HttpService(httpProcessor, PackageHttp.REQUEST_HANDLER);

            Thread requestListener = new RequestListenerThread(((ValueNum) arguments[0]).getValue().intValue(), httpService);
            requestListener.setDaemon(false);
            requestListener.start();

            return new ValueBool(true);
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
