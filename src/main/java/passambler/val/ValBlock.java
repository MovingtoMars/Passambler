package passambler.val;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.parser.Scope;
import passambler.scanner.Token;

public class ValBlock extends Val implements Function {
    private Parser parser;

    private List<String> argumentNames;

    private List<Token> tokens = new ArrayList<>();

    public ValBlock(Scope parentScope, List<String> argumentNames) {
        this.argumentNames = argumentNames;

        this.parser = new Parser(new Scope(parentScope));
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Parser getParser() {
        return parser;
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public int getArguments() {
        return argumentNames.size();
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return argument < argumentNames.size();
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        for (int i = 0; i < argumentNames.size(); ++i) {
            if (i < arguments.length) {
                this.parser.getScope().setVariable(argumentNames.get(i), arguments[i]);
            }
        }

        this.parser.parseSemicolons(tokens);

        return null;
    }
    
    @Override
    public String toString() {
        return "block";
    }
}
