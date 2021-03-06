package passambler.parser.expression;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Argument;
import passambler.util.ValueConstants;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.function.UserFunction;

public class FunctionCallExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.LEFT_PAREN && currentValue instanceof Function;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        List<Token> tokens = parser.getParser().parseExpressionTokens(parser.getTokens(), TokenType.RIGHT_PAREN);

        List<Token> argumentTokenList = new ArrayList<>();
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

            argumentTokenList.add(token);

            // If we reach the next argument, or if we are at the end of all the arguments
            if (depth == 0 && (token.getType() == TokenType.COMMA || token.getType() == TokenType.NEW_LINE || tokens.indexOf(token) == tokens.size() - 1)) {
                if (token.getType() == TokenType.COMMA || token.getType() == TokenType.NEW_LINE) {
                    argumentTokenList.remove(argumentTokenList.size() - 1);
                }

                if (argumentTokenList.isEmpty()) {
                    continue;
                }

                TokenList argumentTokens = new TokenList(argumentTokenList);

                // If we have an identifier and assign token, use named arguments
                if (argumentTokens.current().getType() == TokenType.IDENTIFIER && argumentTokens.peek() != null && argumentTokens.peek().getType() == TokenType.ASSIGN) {
                    if (currentValue instanceof UserFunction) {
                        usedNamedArguments = true;

                        String name = argumentTokens.current().getValue();

                        argumentTokens.next();
                        argumentTokens.next();

                        // Get the exact position of the specific argument in the function definition
                        List<Argument> argumentDefinitions = ((UserFunction) currentFunction).getArgumentDefinitions();

                        int index = argumentDefinitions.indexOf(argumentDefinitions.stream().filter(a -> a.getName().equals(name)).findFirst().get());

                        if (index == -1) {
                            throw new ParserException(ParserExceptionType.UNDEFINED_ARGUMENT, token.getPosition(), name);
                        }

                        // If the position is bigger than our current size
                        // just append nulls to make it bigger.
                        if (index >= arguments.size()) {
                            while (index != arguments.size() - 1) {
                                arguments.add(null);
                            }
                        }

                        arguments.set(index, new ExpressionParser(parser.getParser(), argumentTokens.copyAtCurrentPosition()).parse());
                    } else {
                        throw new ParserException(ParserExceptionType.CANNOT_USE_NAMED_ARGUMENTS, token.getPosition());
                    }
                } else {
                    if (usedNamedArguments) {
                        throw new ParserException(ParserExceptionType.CANNOT_USE_NAMED_ARGUMENTS, token.getPosition());
                    }

                    arguments.add(new ExpressionParser(parser.getParser(), argumentTokens).parse());
                }

                argumentTokenList.clear();
            }
        }

        if (currentFunction instanceof UserFunction) {
            List<Argument> definitions = ((UserFunction) currentFunction).getArgumentDefinitions();

            // Here we set the default values of arguments
            for (int i = 0; i < definitions.size(); ++i) {
                if (i >= arguments.size()) {
                    while (arguments.size() != definitions.size()) {
                        arguments.add(null);
                    }
                }

                if (arguments.get(i) == null) {
                    arguments.remove(i);
                    arguments.add(i, definitions.get(i).getDefaultValue());
                }
            }
        }

        if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, parser.getTokens().get(0).getPosition(), currentFunction.getArguments(), arguments.size());
        }

        if (arguments.stream().anyMatch(v -> v == null)) {
            throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, parser.getTokens().get(0).getPosition(), currentFunction.getArguments(), arguments.size() - arguments.stream().filter(v -> v == null).count());
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                throw new ParserException(ParserExceptionType.INVALID_ARGUMENT, parser.getTokens().get(0).getPosition(), argument + 1);
            }
        }

        Value result = currentFunction.invoke(new FunctionContext(parser.getParser(), arguments.toArray(new Value[arguments.size()])));

        return result == null ? ValueConstants.NIL : result;
    }
}
