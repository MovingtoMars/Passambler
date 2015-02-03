package passambler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
       
        OptionParser optionParser = new OptionParser();
        optionParser.accepts("v");
        optionParser.accepts("a");
        optionParser.accepts("f").withRequiredArg();
        
        try {
            OptionSet options = optionParser.parse(args);
            
            if (options.has("v")) {
                LOGGER.log(Level.INFO, String.format("Passambler %s", VERSION));
            }
            
            if (options.has("a")) {
                LOGGER.log(Level.INFO, "Interactive mode enabled.");

                Parser parser = new Parser();

                parser.setInInteractiveMode(true);

                java.util.Scanner input = new java.util.Scanner(System.in);

                while (input.hasNextLine()) {
                    parser.parseScanner(new Scanner(input.nextLine()));
                }
            }
            
            if (options.has("f")) {
                String file = String.valueOf(options.valueOf("f"));
                
                String data = String.join("\n", Files.readAllLines(Paths.get(file), Charset.forName("UTF-8")));

                Parser parser = new Parser();

                parser.parseScanner(new Scanner(data));
            }
        } catch (ScannerException e) {
            LOGGER.log(Level.SEVERE, "Scanner exception", e);
        } catch (ParserException e) {
            LOGGER.log(Level.SEVERE, "Parser exception", e);
        } catch (OptionException e) {
            LOGGER.log(Level.SEVERE, "Option exception", e);    
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO exception", e);    
        }
    }
}
