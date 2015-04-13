package passambler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import passambler.parser.Parser;
import passambler.lexer.Lexer;
import passambler.lexer.Token;
import passambler.exception.EngineException;
import passambler.tests.OutputRecorder;
import passambler.lexer.TokenType;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;
import passambler.value.Value;

public class Main {
    public static final String VERSION = "DEV";

    public static final Logger LOGGER = Logger.getLogger("Passambler");

    private OptionSet options;

    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("a", "Run interactively");
        optionParser.accepts("d", "Enables debug mode");
        optionParser.accepts("f", "Run one or multiple file(s)").withRequiredArg();
        optionParser.accepts("t", "Run a test (file(s) or a whole directory)").withRequiredArg();

        options = optionParser.parse(args);

        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogHandler(options.has("debug")));

        if (options.has("v")) {
            LOGGER.log(Level.INFO, String.format("Passambler %s", VERSION));
        }

        if (options.has("h") || !options.hasOptions()) {
            optionParser.printHelpOn(System.out);
        }

        if (options.has("f")) {
            for (String file : options.valueOf("f").toString().split(",")) {
                runFile(Paths.get(file));
            }
        }

        if (options.has("t")) {
            for (String file : options.valueOf("t").toString().split(",")) {
                runTestFile(Paths.get(file));
            }
        }

        if (options.has("a")) {
            runInteractiveMode();
        }
    }

    public void runFile(Path file) throws IOException {
        try {
            Parser parser = new Parser();

            parser.parse(new Lexer(String.join("\n", Files.readAllLines(file))));
        } catch (EngineException e) {
            LOGGER.log(Level.SEVERE, e.getName(), e);
        }
    }

    public void runTestFile(Path testFile) throws IOException {
        List<Path> files = new ArrayList<>();

        if (Files.isDirectory(testFile)) {
            for (Path file : Files.newDirectoryStream(testFile)) {
                files.add(file);
            }
        } else {
            files.add(testFile);
        }

        LOGGER.log(Level.INFO, String.format("Running %d tests", files.size()));

        for (Path file : files) {
            try {
                TestParser parser = new TestParser(file);

                TestRunner runner = new TestRunner(parser.parse());

                OutputRecorder.record();

                runner.run();

                OutputRecorder.stop();

                LOGGER.log(Level.INFO, String.format("Test '%s' passed", file.getFileName()));
            } catch (EngineException e) {
                OutputRecorder.stop();

                LOGGER.log(Level.WARNING, String.format("Test '%s' failed", file.getFileName()), e);
            }
        }
    }

    public void runInteractiveMode() {
        Parser parser = new Parser();

        Scanner input = new Scanner(System.in);

        List<Token> tokens = new ArrayList<>();

        while (true) {
            System.out.print("-> ");

            Value result = null;

            try {
                Lexer lexer = new Lexer(input.nextLine());

                tokens.addAll(lexer.scan());

                if (tokens.size() > 0) {
                    TokenType type = tokens.get(tokens.size() - 1).getType();

                    if (type != TokenType.SEMI_COL && type != TokenType.LBRACE) {
                        tokens.add(new Token(TokenType.SEMI_COL, null));
                    }
                }

                int braces = 0;

                for (Token token : tokens) {
                    if (token.getType() == TokenType.LBRACE) {
                        braces++;
                    } else if (token.getType() == TokenType.RBRACE) {
                        braces--;
                    }
                }

                if (braces == 0) {
                    result = parser.parse(tokens);

                    tokens.clear();
                }
            } catch (EngineException e) {
                LOGGER.log(Level.WARNING, e.getName(), e);

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
