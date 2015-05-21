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
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import passambler.bundle.os.OsBundle;
import passambler.parser.Parser;
import passambler.lexer.Lexer;
import passambler.exception.EngineException;
import passambler.util.OutputInterceptor;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;
import passambler.util.PathWatcher;
import static passambler.util.Constants.VERSION;
import static passambler.util.Constants.LOGGER;
import passambler.value.StringValue;

public class Main {
    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("w", "Watches the file being run").withOptionalArg().defaultsTo("1000");
        optionParser.accepts("t", "Run a test (file(s) or a whole directory)").withRequiredArg();

        OptionSet options = optionParser.parse(args);

        if (!options.nonOptionArguments().isEmpty()) {
            String fileName = String.valueOf(options.nonOptionArguments().get(0));

            for (int i = 1; i < options.nonOptionArguments().size(); ++i) {
                OsBundle.args.getValue().add(new StringValue(String.valueOf(options.nonOptionArguments().get(i))));
            }

            if (options.has("w")) {
                runWatchedFile(Paths.get(fileName), Integer.valueOf((String) options.valueOf("w")));
            } else {
                runFile(Paths.get(fileName));
            }
        }

        if (options.has("v")) {
            System.out.println("Passambler " + VERSION);
        }

        if (options.has("h") || (!options.hasOptions() && options.nonOptionArguments().isEmpty())) {
            optionParser.printHelpOn(System.out);
        }

        if (options.has("t")) {
            for (String file : options.valueOf("t").toString().split(",")) {
                runTestFile(Paths.get(file));
            }
        }
    }

    public void runWatchedFile(Path file, int watchTime) {
        Timer timer = new Timer();

        TimerTask task = new PathWatcher(file) {
            @Override
            public void onChange() {
                LOGGER.info("Watched file '" + file.getFileName() + "' is reloaded");

                runFile(file);
            }
        };

        timer.schedule(task, new Date(), watchTime);
    }

    public void runFile(Path file) {
        try {
            Parser parser = new Parser();

            parser.parse(new Lexer(String.join("\n", Files.readAllLines(file))));
        } catch (EngineException e) {
            LOGGER.fatal(e.getName(), e);
        } catch (IOException e) {
            LOGGER.error(e);
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

                OutputInterceptor.start();

                runner.run();

                OutputInterceptor.stop();

                LOGGER.info("Test '" + file.getFileName() + "' passed");
            } catch (Exception e) {
                OutputInterceptor.stop();

                LOGGER.error("Test '" + file.getFileName() + "' failed", e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main(args);
    }
}
