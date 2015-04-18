package passambler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public static final Logger LOGGER = LogManager.getLogger("Passambler");

    private OptionSet options;

    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("a", "Run interactively");
        optionParser.accepts("f", "Run one or multiple file(s)").withRequiredArg();
        optionParser.accepts("t", "Run a test (file(s) or a whole directory)").withRequiredArg();

        options = optionParser.parse(args);

        if (options.has("v")) {
            System.out.println("Passambler " + VERSION);
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
            LOGGER.fatal(e.getName(), e);
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

        LOGGER.info("Running " + files.size() + " tests");

        for (Path file : files) {
            try {
                TestParser parser = new TestParser(file);

                TestRunner runner = new TestRunner(parser.parse());

                OutputRecorder.record();

                runner.run();

                OutputRecorder.stop();

                LOGGER.info("Test '" + file.getFileName() + "' passed");
            } catch (Exception e) {
                OutputRecorder.stop();

                LOGGER.error("Test '" + file.getFileName() + "' failed", e);
            }
        }
    }

    public void runInteractiveMode() {
        Parser parser = new Parser();

        List<Token> tokens = new ArrayList<>();

        String input = "";

        while ((input = System.console().readLine("-> ")) != null) {
            try {
                tokens.addAll(new Lexer(input).scan());

                long depth = tokens.stream().filter(t -> t.getType() == TokenType.LEFT_BRACE).count() - tokens.stream().filter(t -> t.getType() == TokenType.RIGHT_BRACE).count();

                if (depth == 0) {
                    Value result = parser.parse(tokens);

                    if (result != null) {
                        System.out.println(result);
                    }

                    tokens.clear();
                }
            } catch (EngineException e) {
                LOGGER.error(e.getName(), e);

                tokens.clear();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main(args);
    }
}
