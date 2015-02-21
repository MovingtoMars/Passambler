package passambler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import passambler.extension.Extension;
import passambler.extension.math.ExtensionMath;
import passambler.extension.std.ExtensionStd;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.lexer.Token;
import passambler.tests.TestException;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;
import passambler.value.Value;

public class Main {
    public static final String VERSION = "0.1.0-SNAPSHOT";

    public static final Logger LOGGER = Logger.getLogger("Passambler");
    
    public static List<Extension> EXTENSIONS = new ArrayList<>();
    
    private OptionSet options;

    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("a", "Run interactively");
        optionParser.accepts("f", "Run one or multiple file(s)").withRequiredArg();
        optionParser.accepts("t", "Run a test (file(s) or a whole directory)").withRequiredArg();

        optionParser.accepts("show-stacktrace", "Shows the stacktrace of the parser");
        optionParser.accepts("show-tokens", "Shows the tokens of a file");

        options = optionParser.parse(args);

        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogHandler(options.has("show-stacktrace")));
        
        EXTENSIONS.add(new ExtensionStd());
        EXTENSIONS.add(new ExtensionMath());

        if (options.has("v")) {
            LOGGER.log(Level.INFO, String.format("Passambler %s", VERSION));
        }

        if (options.has("h") || !options.hasOptions()) {
            optionParser.printHelpOn(System.out);
        }

        if (options.has("f")) {
            for (String file : options.valueOf("f").toString().split(",")) {
                runFile(new File(file));
            }
        }

        if (options.has("t")) {
            for (String file : options.valueOf("t").toString().split(",")) {
                runTestFile(new File(file));
            }
        }

        if (options.has("a")) {
            runInteractiveMode();
        }
    }

    public void runFile(File file) throws IOException {
        try {
            Lexer lexer = new Lexer(String.join("\n", Files.readAllLines(file.toPath())));

            if (options.has("show-tokens")) {
                for (Token token : lexer.scan()) {
                    LOGGER.log(Level.INFO, token.toString());
                }
            } else {
                Parser parser = new Parser();
                
                parser.parse(lexer);
            }
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, "Lexer exception", e);
        } catch (ParserException e) {
            LOGGER.log(Level.SEVERE, "Parser exception", e);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Runtime exception", e);
        }
    }

    public void runTestFile(File testFile) throws IOException {
        List<File> files = new ArrayList<>();

        if (testFile.isDirectory()) {
            for (File file : testFile.listFiles()) {
                files.add(file);
            }
        } else {
            files.add(testFile);
        }

        for (File file : files) {
            boolean passed = true;

            try {
                TestParser parser = new TestParser(file);

                TestRunner runner = new TestRunner(parser.parse());

                runner.run();
            } catch (LexerException | ParserException | TestException e) {
                LOGGER.log(Level.WARNING, String.format("Test %s failed", file.getName()), e);

                passed = false;
            }

            if (passed) {
                LOGGER.log(Level.INFO, String.format("Test %s passed", file.getName()));
            }
        }
    }

    public void runInteractiveMode() {
        Parser parser = new Parser();

        Scanner input = new Scanner(System.in);

        List<Token> tokens = new ArrayList<>();

        while (true) {
            System.out.print((tokens.isEmpty() ? "~>" : "->") + " ");

            Value result = null;

            try {
                Lexer lexer = new Lexer(input.nextLine());

                tokens.addAll(lexer.scan());
                tokens.add(new Token(Token.Type.SEMI_COL, null));

                if (options.has("show-tokens")) {
                    for (Token token : tokens) {
                        LOGGER.log(Level.INFO, token.toString());
                    }
                    
                    tokens.clear();
                } else {
                    int braces = 0;

                    for (Token token : tokens) {
                        if (token.getType() == Token.Type.LBRACE) {
                            braces++;
                        } else if (token.getType() == Token.Type.RBRACE) {
                            braces--;
                        }
                    }

                    if (braces == 0) {
                        result = parser.parse(tokens);

                        tokens.clear();
                    }
                }
            } catch (LexerException e) {
                LOGGER.log(Level.WARNING, "Lexer exception", e);

                tokens.clear();
            } catch (ParserException e) {
                LOGGER.log(Level.WARNING, "Parser exception", e);

                tokens.clear();
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Runtime exception", e);

                tokens.clear();
            }

            if (result != null) {
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main(args);
    }
}
