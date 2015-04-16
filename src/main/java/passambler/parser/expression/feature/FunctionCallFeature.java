package passambler.parser.expression.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.ArgumentDefinition;
import passambler.parser.expression.ExpressionParser;
import passambler.value.ErrorValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.function.UserFunction;

public class FunctionCallFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_PAREN && currentValue instanceof Function;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getStream().next();

        List<Token> tokens = parser.getParser().parseExpressionTokens(parser.getStream(), TokenType.RIGHT_PAREN);

        List<Token> argumentTokens = new ArrayList<>();
        List<Value> arguments = new ArrayList<>();

        boolean usedNamedArguments = false;

        int depth = 0;

        Function currentFunction = (Function) currentValue;

        for (Token token : tokens) {
            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            argumentTokens.add(token);

            if (depth == 0 && (token.getType() == TokenType.COMMA || tokens.indexOf(token) == tokens.size() - 1)) {
                if (token.getType() == TokenType.COMMA) {
                    argumentTokens.remove(argumentTokens.size() - 1);
                }

                TokenStream argumentTokenStream = new TokenStream(argumentTokens);

                if (argumentTokenStream.current().getType() == TokenType.IDENTIFIER && argumentTokenStream.peek() != null && argumentTokenStream.peek().getType() == TokenType.ASSIGN) {
                    if (currentValue instanceof UserFunction) {
                        usedNamedArguments = true;

                        String name = argumentTokenStream.current().getValue();

                        argumentTokenStream.next();
                        argumentTokenStream.next();

                        List<ArgumentDefinition> argumentDefinitions = ((UserFunction) currentFunction).getArgumentDefinitions();

                        int index = argumentDefinitions.indexOf(argumentDefinitions.stream().filter(a -> a.getName().equals(name)).findFirst().get());

                        if (index == -1) {
                            throw new ParserException(ParserExceptionType.UNDEFINED_ARGUMENT, token.getPosition(), name);
                        }

                        if (index >= arguments.size()) {
                            do {
                                arguments.add(null);
                            } while (index != arguments.size() - 1);
                        }

                        arguments.set(index, parser.createParser(argumentTokenStream.copyAtCurrentPosition()).parse());
                    } else {
                        throw new ParserException(ParserExceptionType.CANNOT_USE_NAMED_ARGUMENTS, token.getPosition());
                    }
                } else {
                    if (usedNamedArguments) {
                        throw new ParserException(ParserExceptionType.BAD_SYNTAX, token.getPosition(), "Cannot specify a normal argument after a specifying a named argument");
                    }

                    arguments.add(parser.createParser(argumentTokenStream).parse());
                }

                argumentTokens.clear();
            }
        }

        if (currentFunction instanceof UserFunction) {
            List<ArgumentDefinition> definitions = ((UserFunction) currentFunction).getArgumentDefinitions();

            for (int i = 0; i < definitions.size(); ++i) {
                if (i >= arguments.size()) {
                    do {
                        arguments.add(null);
                    } while (arguments.size() != definitions.size());
                }

                if (arguments.get(i) == null) {
                    arguments.remove(i);
                    arguments.add(i, definitions.get(i).getDefaultValue());
                }
            }
        }

        if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, parser.getStream().first().getPosition(), currentFunction.getArguments(), arguments.size());
        }

        if (arguments.stream().anyMatch(v -> v == null)) {
            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, parser.getStream().first().getPosition(), currentFunction.getArguments(), arguments.size() - arguments.stream().filter(v -> v == null).count());
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                throw new ParserException(ParserExceptionType.INVALID_ARGUMENT, parser.getStream().first().getPosition(), argument + 1);
            }
        }

        Value result = currentFunction.invoke(new FunctionContext(parser.getParser(), arguments.toArray(new Value[arguments.size()]), parser.isAssignment()));

        return result == null ? Value.VALUE_NIL : result;
    }
}
