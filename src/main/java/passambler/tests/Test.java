package passambler.tests;

public class Test {
    private String description;

    private String input;

    private String output;

    private String result;

    public Test(String description, String input, String output, String result) {
        this.description = description;

        this.input = input;

        this.output = output;

        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getResult() {
        return result;
    }
}
