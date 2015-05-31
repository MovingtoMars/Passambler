package passambler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import passambler.module.os.OsModule;
import passambler.parser.Parser;
import passambler.lexer.Lexer;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenType;
import passambler.util.OutputInterceptor;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;
import passambler.util.Constants;
import passambler.util.PathWatcher;
import static passambler.util.Constants.VERSION;
import static passambler.util.Constants.LOGGER;
import passambler.value.StringValue;
import passambler.value.Value;

public class Main {
    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("w", "Watches the file being run").withOptionalArg().defaultsTo("1000");
        optionParser.accepts("r", "Run a code snippet").withRequiredArg();
        optionParser.accepts("t", "Run a test (file(s) or a whole directory)").withRequiredArg();
        optionParser.accepts("e", "Runs the REPL");

        OptionSet options = optionParser.parse(args);

        if (!options.nonOptionArguments().isEmpty()) {
            String fileName = String.valueOf(options.nonOptionArguments().get(0)) + "." + Constants.EXTENSION;

            for (int i = 1; i < options.nonOptionArguments().size(); ++i) {
                OsModule.args.getValue().add(new StringValue(String.valueOf(options.nonOptionArguments().get(i))));
            }

            if (options.has("w")) {
                runWatchedFile(Paths.get(fileName), Integer.valueOf((String) options.valueOf("w")));
            } else {
                runFile(Paths.get(fileName));
            }
        }

        if (options.has("t")) {
            runTestFile(Paths.get(String.valueOf(options.valueOf("t")) + "." + Constants.TEST_EXTENSION));
        }

        if (options.has("r")) {
            runCode(String.valueOf(options.valueOf("r")));
        }

        if (options.has("v")) {
            System.err.println("Passambler " + VERSION);
        }

        if (options.has("h") || (!options.hasOptions() && options.nonOptionArguments().isEmpty())) {
            optionParser.printHelpOn(System.err);
        }

        if (options.has("e")) {
            runREPL();
        }
    }

    public void runWatchedFile(Path file, int watchTime) {
        Timer timer = new Timer();

        TimerTask task = new PathWatcher(file) {
            @Override
            public void onChange() {
                LOGGER.log(Level.INFO, "Watched file `" + file.getFileName() + "` is reloaded");

                runFile(file);
            }
        };

        timer.schedule(task, new Date(), watchTime);
    }

    public Value runFile(Path file) {
        try {
            return runCode(String.join("\n", Files.readAllLines(file)));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error", e);
        }

        return null;
    }

    public Value runCode(String code) {
        try {
            return new Parser().parse(new Lexer(code));
        } catch (EngineException e) {
            LOGGER.log(Level.SEVERE, e.getName(), e);
        }

        return null;
    }

    public void runTestFile(Path testFile) throws IOException {
        List<Path> files = new ArrayList<>();

        if (Files.isDirectory(testFile)) {
            Files.newDirectoryStream(testFile).forEach(f -> files.add(f));
        } else {
            files.add(testFile);
        }

        for (Path file : files) {
            try {
                TestParser parser = new TestParser(file);
                TestRunner runner = new TestRunner(parser.parse());

                OutputInterceptor.start();

                runner.run();

                OutputInterceptor.stop();

                LOGGER.log(Level.INFO, "Test `" + file.getFileName() + "` " + Constants.ANSI_GREEN + "passed" + Constants.ANSI_RESET);
            } catch (Exception e) {
                OutputInterceptor.stop();

                LOGGER.log(Level.INFO, "Test `" + file.getFileName() + "` " + Constants.ANSI_RED + "failed" + Constants.ANSI_RESET, e);
            }
        }
    }

    public void runREPL() {
        Parser parser = new Parser();

        StringBuilder input = new StringBuilder();

        int depth = 0;

        while (true) {
            try {
                if (System.console() != null) {
                    String data = System.console().readLine("> ");

                    if (data == null) {
                        break;
                    }

                    input.append(data);

                    for (Token token : new Lexer(data).tokenize()) {
                        if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                            depth++;
                        } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                            depth--;
                        }
                    }

                    if (depth == 0) {
                        Value result = parser.parse(new Lexer(input.toString()));

                        input = new StringBuilder();

                        if (result != null) {
                            System.err.println(result);
                        }
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Console not available");

                    break;
                }
            } catch (EngineException e) {
                LOGGER.log(Level.WARNING, e.getName(), e);

                depth = 0;

                input = new StringBuilder();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main(args);
    }
}
