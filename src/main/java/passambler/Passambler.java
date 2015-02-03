package passambler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.scanner.Scanner;
import passambler.scanner.ScannerException;

public class Passambler {
    public static final String VERSION = "0.1.0-SNAPSHOT";
    
    public static final boolean DEBUG = true;

    public static final Logger LOGGER = Logger.getLogger("Passambler");

    public static void main(String[] args) {
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogHandler());

        try {
            if (args.length == 1 && args[0].equals("-a")) {
                LOGGER.log(Level.INFO, "Interactive mode enabled.");

                Parser parser = new Parser();

                parser.setInInteractiveMode(true);

                java.util.Scanner input = new java.util.Scanner(System.in);

                while (input.hasNextLine()) {
                    parser.parseScanner(new Scanner(input.nextLine()));
                }
            } else if (args.length == 1) {
                String data = null;

                try {
                    data = String.join("\n", Files.readAllLines(Paths.get(args[0]), Charset.forName("UTF-8")));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, String.format("Could not read input file: %s", args[0]));

                    System.exit(-1);
                }

                Parser parser = new Parser();

                parser.parseScanner(new Scanner(data));
            }
        } catch (ScannerException e) {
            LOGGER.log(Level.SEVERE, "Scanner exception", e);
        } catch (ParserException e) {
            LOGGER.log(Level.SEVERE, "Parser exception", e);
        }
    }
}
