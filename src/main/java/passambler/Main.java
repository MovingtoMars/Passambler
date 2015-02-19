package passambler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
import passambler.tests.Test;
import passambler.tests.TestParser;
import passambler.tests.TestRunner;
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
            optionParser.accepts("f", "Parse and execute a file").withRequiredArg();
            optionParser.accepts("t", "Run a test (file or a whole directory)").withRequiredArg();
            
            optionParser.accepts("show-stacktrace", "Shows the stacktrace of the parser");
            optionParser.accepts("show-tokens", "Shows the tokens of a file");

            OptionSet options = null;

            options = optionParser.parse(args);

            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(new LogHandler(options.has("show-stacktrace")));

            if (options.has("v")) {
                LOGGER.log(Level.INFO, String.format("Passambler %s", VERSION));
            }

            if (options.has("h") || !options.hasOptions()) {
                optionParser.printHelpOn(System.out);
            }

            if (options.has("f")) {
                try {
                    Lexer lexer = new Lexer(String.join("\n", Files.readAllLines(Paths.get(String.valueOf(options.valueOf("f"))), Charset.forName("UTF-8"))));

                    if (options.has("show-tokens")) {
                        for (Token token : lexer.scan()) {
                            LOGGER.log(Level.INFO, token.toString());
                        }
                    } else {
                        Parser parser = new Parser();

                        parser.getScope().addStd();

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

            if (options.has("a")) {
                Parser parser = new Parser();

                parser.getScope().addStd();

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

            if (options.has("t")) {
                File file = new File(String.valueOf(options.valueOf("t")));

                if (file.isDirectory()) {
                    for (File subFile : file.listFiles()) {
                        parseTestFile(subFile);
                    }
                } else {
                    parseTestFile(file);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "I/O exception", e);
        } catch (OptionException e) {
            LOGGER.log(Level.SEVERE, "Option exception", e);
        }
    }

    public static void parseTestFile(File file) throws IOException {
        TestParser parser = new TestParser(file);

        Test test = parser.parse();

        TestRunner runner = new TestRunner(test);

        boolean passed = true;

        try {
            runner.run();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Test " + file.getName() + " failed", e);

            passed = false;
        }

        if (passed) {
            LOGGER.log(Level.INFO, "Test " + file.getName() + " passed");
        }
    }
}
