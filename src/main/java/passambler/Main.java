package passambler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.value.Value;

public class Main {
    public static final String VERSION = "0.1.0-SNAPSHOT";

    public static final Logger LOGGER = Logger.getLogger("Passambler");

    public static void main(String[] args) {
        try {
            OptionParser optionParser = new OptionParser();

            optionParser.accepts("v", "Version number");
            optionParser.accepts("h", "Help");
            optionParser.accepts("a", "Run interactively");
            optionParser.accepts("s", "Shows the stacktrace of the parser");
            optionParser.accepts("f", "Parse and execute a file").withRequiredArg();
            optionParser.accepts("t", "Show tokens");

            OptionSet options = null;

            options = optionParser.parse(args);

            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(new LogHandler(options.has("s")));

            if (options.has("v")) {
                LOGGER.log(Level.INFO, String.format("Passambler %s", VERSION));
            }

            if (options.has("h") || !options.hasOptions()) {
                optionParser.printHelpOn(System.out);
            }

            if (options.has("f")) {
                try {
                    Lexer lexer = new Lexer(String.join("\n", Files.readAllLines(Paths.get(String.valueOf(options.valueOf("f"))), Charset.forName("UTF-8"))));

                    if (options.has("t")) {
                        for (Token token : lexer.scan()) {
                            LOGGER.log(Level.INFO, token.toString());
                        }
                    } else {
                        Parser parser = new Parser();

                        parser.getScope().addStd();

                        parser.parseLexer(lexer);
                    }
                } catch (LexerException e) {
                    LOGGER.log(Level.SEVERE, "Lexer exception", e);
                } catch (ParserException e) {
                    LOGGER.log(Level.SEVERE, "Parser exception", e);
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Runtime exception", e);
                }
            }

            if (options.has("a")) {
                Parser parser = new Parser();

                parser.getScope().addStd();

                Scanner input = new Scanner(System.in);

                while (true) {
                    System.out.print("~> ");

                    Value result = null;

                    try {
                        result = parser.parseLexer(new Lexer(input.nextLine()));
                    } catch (LexerException e) {
                        LOGGER.log(Level.WARNING, "Lexer exception", e);
                    } catch (ParserException e) {
                        LOGGER.log(Level.WARNING, "Parser exception", e);
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.WARNING, "Runtime exception", e);
                    }

                    if (result != null && result != Value.VALUE_NIL) {
                        System.out.println(result);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "I/O exception", e);
        } catch (OptionException e) {
            LOGGER.log(Level.SEVERE, "Option exception", e);
        }
    }
}
