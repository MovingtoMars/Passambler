package passambler.val;

import java.io.InputStream;
import java.util.Scanner;
import passambler.parser.Stream;

public class ValInputStream extends Val implements Stream {
    private InputStream inputStream;

    public ValInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void onStream(Val value) {
        Scanner scanner = new Scanner(inputStream);
        
        if (value instanceof ValNumber) {
            value.setValue(scanner.nextDouble());
        } else {
            value.setValue(scanner.next());
        }
    }
}
