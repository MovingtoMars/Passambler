package passambler.tests;

import passambler.util.OutputInterceptor;
import passambler.exception.TestException;
import passambler.lexer.Lexer;
import passambler.parser.Parser;
import passambler.exception.EngineException;
import passambler.value.Value;

public class TestRunner {
    private Test test;

    public TestRunner(Test test) {
        this.test = test;
    }

    public void run() throws EngineException {
        Parser parser = new Parser();

        if (test.getInput() == null) {
            throw new TestException("Missing input section");
        }

        Value result = parser.parse(new Lexer(test.getInput()));

        if (test.getOutput() != null && !test.getOutput().equals(OutputInterceptor.getOutput())) {
            throw new TestException("Unexpected output, expected `%s` but got `%s`", test.getOutput(), OutputInterceptor.getOutput());
        }

        if (test.getResult() != null) {
            if (result == null) {
                throw new TestException("Unexpected result, expected `%s`", test.getResult());
            } else if (!test.getResult().equals(result.toString())) {
                throw new TestException("Unexpected result, expected `%s` but got `%s`", test.getResult(), result.toString());
            }
        }
    }
}
