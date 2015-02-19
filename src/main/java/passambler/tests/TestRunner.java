package passambler.tests;

import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public class TestRunner {
    private Test test;

    public TestRunner(Test test) {
        this.test = test;
    }

    public void run() throws LexerException, ParserException, TestException {
        Parser parser = new Parser();

        Value result = parser.parse(new Lexer(test.getInput()));

        if (test.getResult() != null) {
            if (result == null) {
                throw new TestException("unexpected result, expected '%s'", test.getResult());
            } else if (!test.getResult().equals(result.toString())) {
                throw new TestException("unexpected result, expected '%s' but got '%s'", test.getResult(), result.toString());
            }
        }
    }
}
