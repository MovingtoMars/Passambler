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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import passambler.parser.Parser;
import passambler.lexer.Lexer;
import passambler.exception.EngineException;
import passambler.tests.OutputRecorder;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;

public class Main {
    public static final String VERSION = "DEV";

    public static final Logger LOGGER = LogManager.getLogger("Passambler");

    private OptionSet options;

    public Main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        optionParser.accepts("v", "Show the version number");
        optionParser.accepts("h", "Show help");
        optionParser.accepts("f", "Run one or multiple file(s)").withRequiredArg();
        optionParser.accepts("w", "Watches the file being run").withOptionalArg().defaultsTo("1000");
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
                if (options.has("w")) {
                    runWatchedFile(Paths.get(file), Integer.valueOf((String) options.valueOf("w")));
                } else {
                    runFile(Paths.get(file));
                }
            }
        }

        if (options.has("t")) {
            for (String file : options.valueOf("t").toString().split(",")) {
                runTestFile(Paths.get(file));
            }
        }
    }

    public void runWatchedFile(Path file, int watchTime) {
        Timer timer = new Timer();

        TimerTask task = new FileWatcher(file) {
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

    public static void main(String[] args) throws IOException {
        Main main = new Main(args);
    }

    abstract class FileWatcher extends TimerTask {
        private long lastChangeTime;

        private Path file;

        public FileWatcher(Path file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                long time = Files.getLastModifiedTime(file).toMillis();

                if (lastChangeTime != time) {
                    lastChangeTime = time;

                    onChange();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to get last file modification date", e);
            }
        }

        public abstract void onChange();
    }
}
