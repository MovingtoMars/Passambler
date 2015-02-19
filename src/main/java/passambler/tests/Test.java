package passambler.tests;

public class Test {
    private String description;
    
    private String input;
    
    private String result;
    
    public Test(String description, String input, String result) {
        this.description = description;
        
        this.input = input;
        
        this.result = result;
    }
    
    public String getDescription() {
        return description;
    }

    public String getInput() {
        return input;
    }

    public String getResult() {
        return result;
    }
}
