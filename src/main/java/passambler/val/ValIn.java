package passambler.val;

import java.io.InputStream;
import java.util.Scanner;
import passambler.parser.Stream;

public class ValIn extends Val implements Stream {
    private InputStream inputStream;

    public ValIn(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void onStream(Val value) {
        Scanner scanner = new Scanner(inputStream);
        
        if (value instanceof ValNum) {
            value.setValue(scanner.nextDouble());
        } else {
            value.setValue(scanner.next());
        }
    }
}
