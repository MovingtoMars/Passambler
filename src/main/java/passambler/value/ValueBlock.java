package passambler.value;

import java.util.ArrayList;
import java.util.List;
import passambler.procedure.Procedure;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.parser.Scope;
import passambler.lexer.Token;

public class ValueBlock extends Value implements Procedure {
    private Parser parser;

    private List<String> argumentNames;

    private List<Token> tokens = new ArrayList<>();

    public ValueBlock(Scope parentScope, List<String> argumentNames) {
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
    public boolean isArgumentValid(Value value, int argument) {
        return argument < argumentNames.size();
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        for (int i = 0; i < argumentNames.size(); ++i) {
            if (i < arguments.length) {
                this.parser.getScope().setSymbol(argumentNames.get(i), arguments[i]);
            }
        }

        return this.parser.parse(tokens);
    }

    public static ValueBlock transform(Procedure procedure) {
        return new ValueBlock(new Scope(), null) {
            @Override
            public int getArguments() {
                return procedure.getArguments();
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return procedure.isArgumentValid(value, argument);
            }

            @Override
            public Value invoke(Parser parser, Value... arguments) throws ParserException {
                return procedure.invoke(parser, arguments);
            }
        };
    }
}
