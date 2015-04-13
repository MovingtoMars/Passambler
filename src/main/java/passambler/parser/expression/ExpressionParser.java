package passambler.parser.expression;

import passambler.exception.ParserException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.function.FunctionUser;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.ValueBool;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class ExpressionParser {
    private boolean assignment;

    private Parser parser;

    private TokenStream stream;

    public ExpressionParser(Parser parser, TokenStream stream) {
        this(parser, stream, false);
    }

    public ExpressionParser(Parser parser, TokenStream stream, boolean assignment) {
        this.parser = parser;
        this.stream = stream;
        this.assignment = assignment;
    }

    public Value parse() throws EngineException {
        List<Token> tokens = new ArrayList<>();
        List<ValueOperatorPair> values = new ArrayList<>();

        Token lastOperator = null;

        int depth = 0;

        while (stream.hasNext()) {
            Token token = stream.current();

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            tokens.add(token);

            if ((token.getType().isOperator() || stream.peek() == null) && depth == 0) {
                if (token.getType().isOperator()) {
                    tokens.remove(tokens.size() - 1);
                }

                if (tokens.isEmpty()) {
                    stream.match(TokenType.MINUS, TokenType.PLUS);

                    boolean negate = stream.current().getType() == TokenType.MINUS;

                    stream.next();

                    stream.match(TokenType.NUMBER);

                    BigDecimal number = new BigDecimal(stream.current().getValue());

                    if (negate) {
                        number = number.negate();
                    } else {
                        number = number.plus();
                    }

                    tokens.add(new Token(TokenType.NUMBER, number.toString(), stream.current().getPosition()));
                }

                values.add(new ValueOperatorPair(createParser(new TokenStream(tokens)).parseSpecialized(), lastOperator));

                tokens.clear();

                lastOperator = stream.current();
            }

            stream.next();
        }

        Value value = null;

        doPrecedence(values, TokenType.AND, TokenType.OR);
        doPrecedence(values, TokenType.EQUAL, TokenType.NEQUAL, TokenType.GT, TokenType.GTE, TokenType.LT, TokenType.LTE);
        doPrecedence(values, TokenType.RANGE);
        doPrecedence(values, TokenType.POWER);
        doPrecedence(values, TokenType.MULTIPLY, TokenType.DIVIDE);

        for (ValueOperatorPair pair : values) {
            if (value == null) {
                value = pair.getValue();
            } else {
                value = value.onOperator(pair.getValue(), pair.getOperator());

                if (value == null) {
                    throw new ParserException(ParserExceptionType.UNSUPPORTED_OPERATOR, pair.getOperator().getPosition(), pair.getOperator().getType());
                }
            }
        }

        return value;
    }

    private void doPrecedence(List<ValueOperatorPair> values, TokenType... types) throws EngineException {
        for (int i = 0; i < values.size(); ++i) {
            ValueOperatorPair current = values.get(i);

            if (i > 0 && current.getOperator() != null && Arrays.asList(types).contains(current.getOperator().getType())) {
                ValueOperatorPair behind = values.get(i - 1);

                behind.setValue(behind.getValue().onOperator(current.getValue(), current.getOperator()));

                values.remove(current);
            }
        }
    }

    public Value parseSpecialized() throws EngineException {
        Value value = null;

        boolean not = false;

        while (stream.hasNext()) {
            Token token = stream.current();

            switch (token.getType()) {
                case NOT:
                    not = true;

                    break;
                case LEFT_PAREN:
                    value = parseParen(value);

                    break;
                case LEFT_BRACE:
                    value = parseBrace();

                    break;
                case LEFT_BRACKET:
                    value = parseIndexed(value);

                    break;
                case STRING:
                case NUMBER:
                case IDENTIFIER:
                    value = parseSymbol();

                    break;
                case PERIOD:
                    value = parseProperty(value);

                    break;
                case FN:
                    value = parseFunction();

                    break;
                default:
                    throw new ParserException(ParserExceptionType.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
            }

            stream.next();
        }

        if (not) {
            if (!(value instanceof ValueBool)) {
                throw new ParserException(ParserExceptionType.EXPECTED_A_BOOL, stream.first().getPosition());
            }

            value = new ValueBool(!((ValueBool) value).getValue());
        }

        return value;
    }

    private Value parseFunction() throws EngineException {
        stream.next();

        List<ArgumentDefinition> arguments = parser.parseArgumentDefinition(stream);

        stream.next();

        Block callback = parser.parseBlock(stream);

        return new FunctionUser(callback, arguments);
    }

    private Value parseParen(Value currentValue) throws EngineException {
        stream.next();

        List<Token> tokens = parser.parseExpressionTokens(stream, TokenType.RIGHT_PAREN);

        if (currentValue instanceof Function) {
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
                        if (currentValue instanceof FunctionUser) {
                            usedNamedArguments = true;

                            String name = argumentTokenStream.current().getValue();

                            argumentTokenStream.next();
                            argumentTokenStream.next();

                            List<ArgumentDefinition> argumentDefinitions = ((FunctionUser) currentFunction).getArgumentDefinitions();

                            int index = argumentDefinitions.indexOf(argumentDefinitions.stream().filter(a -> a.getName().equals(name)).findFirst().get());

                            if (index == -1) {
                                throw new ParserException(ParserExceptionType.UNDEFINED_ARGUMENT, token.getPosition(), name);
                            }

                            if (index >= arguments.size()) {
                                do {
                                    arguments.add(null);
                                } while (index != arguments.size() - 1);
                            }

                            arguments.set(index, createParser(argumentTokenStream.copyAtCurrentPosition()).parse());
                        } else {
                            throw new ParserException(ParserExceptionType.CANNOT_USE_NAMED_ARGUMENTS, token.getPosition());
                        }
                    } else {
                        if (usedNamedArguments) {
                            throw new ParserException(ParserExceptionType.BAD_SYNTAX, token.getPosition(), "cannot specify a normal argument after a specifying a named argument");
                        }

                        arguments.add(createParser(argumentTokenStream).parse());
                    }

                    argumentTokens.clear();
                }
            }

            if (currentFunction instanceof FunctionUser) {
                List<ArgumentDefinition> definitions = ((FunctionUser) currentFunction).getArgumentDefinitions();

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
                throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), currentFunction.getArguments(), arguments.size());
            }

            if (arguments.stream().anyMatch(v -> v == null)) {
                throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), currentFunction.getArguments(), arguments.size() - arguments.stream().filter(v -> v == null).count());
            }

            for (int argument = 0; argument < arguments.size(); ++argument) {
                if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                    throw new ParserException(ParserExceptionType.INVALID_ARGUMENT, stream.first().getPosition(), argument + 1);
                }
            }

            Value result = currentFunction.invoke(new FunctionContext(parser, arguments.toArray(new Value[arguments.size()]), assignment));

            return result == null ? Value.VALUE_NIL : result;
        } else if (!tokens.isEmpty()) {
            return createParser(new TokenStream(tokens)).parse();
        }

        return null;
    }

    private Value parseIndexed(Value currentValue) throws EngineException {
        List<Token> tokens = new ArrayList<>();

        int brackets = 1, paren = 0, braces = 0;

        stream.next();

        ValueList inlineDeclaration = new ValueList();

        while (stream.hasNext()) {
            if ((stream.current().getType() == TokenType.COMMA || stream.current().getType() == TokenType.RIGHT_BRACKET) && brackets == 1 && paren == 0 && braces == 0) {
                if (tokens.isEmpty()) {
                    if (stream.back(2) != null) {
                        throw new ParserException(ParserExceptionType.BAD_SYNTAX, stream.current().getPosition(), "no value specified");
                    }
                } else {
                    inlineDeclaration.getValue().add(createParser(new TokenStream(tokens)).parse());
                }

                if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                    brackets--;
                } else {
                    stream.next();

                    tokens.clear();

                    continue;
                }
            } else if (stream.current().getType() == TokenType.LEFT_BRACKET) {
                brackets++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                brackets--;
            } else if (stream.current().getType() == TokenType.LEFT_PAREN) {
                paren++;
            } else if (stream.current().getType() == TokenType.RIGHT_PAREN) {
                paren--;
            } else if (stream.current().getType() == TokenType.LEFT_BRACE) {
                braces++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACE) {
                braces--;
            }

            if (brackets == 0 && paren == 0 && braces == 0) {
                break;
            }

            tokens.add(stream.current());

            stream.next();
        }

        if (currentValue == null && (inlineDeclaration.getValue().size() > 0 || tokens.isEmpty())) {
            return inlineDeclaration;
        } else {
            Value indexValue = createParser(new TokenStream(tokens)).parse();

            if (indexValue instanceof ValueNum) {
                if (!(currentValue instanceof ValueList)) {
                    throw new ParserException(ParserExceptionType.NOT_A_LIST);
                }

                ValueList list = (ValueList) currentValue;

                int index = ((ValueNum) indexValue).getValue().intValue();

                if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                    throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, null, index, list.getValue().size());
                }

                if (index < 0) {
                    return list.getValue().get(list.getValue().size() - Math.abs(index));
                } else {
                    return list.getValue().get(index);
                }
            } else {
                if (!(currentValue instanceof ValueDict)) {
                    throw new ParserException(ParserExceptionType.NOT_A_DICT);
                }

                ValueDict dict = (ValueDict) currentValue;

                if (dict.getEntry(indexValue) == null) {
                    return Value.VALUE_NIL;
                }

                return dict.getEntry(indexValue);
            }
        }
    }

    private Value parseProperty(Value currentValue) throws EngineException {
        if (stream.peek() == null) {
            throw new ParserException(ParserExceptionType.BAD_SYNTAX, stream.current().getPosition(), "missing property name");
        }

        stream.next();

        stream.match(TokenType.IDENTIFIER);

        String propertyName = stream.current().getValue();

        if (!currentValue.hasProperty(propertyName)) {
            throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, stream.current().getPosition(), propertyName);
        }

        return currentValue.getProperty(propertyName).getValue();
    }

    private Value parseSymbol() throws EngineException {
        Token token = stream.current();

        if (token.getType() == TokenType.STRING) {
            return new ValueStr(token.getValue());
        } else if (token.getType() == TokenType.NUMBER) {
            StringBuilder number = new StringBuilder();

            number.append(token.getValue());

            if (stream.peek() != null && stream.peek().getType() == TokenType.PERIOD) {
                stream.next();
                stream.next();

                stream.match(TokenType.NUMBER);

                number.append(".");
                number.append(stream.current().getValue());
            }

            return new ValueNum(new BigDecimal(number.toString()));
        } else if (token.getType() == TokenType.IDENTIFIER) {
            if (token.getValue().equals("true")) {
                return Value.VALUE_TRUE;
            } else if (token.getValue().equals("false")) {
                return Value.VALUE_FALSE;
            } else if (token.getValue().equals("nil")) {
                return Value.VALUE_NIL;
            }

            if (!parser.getScope().hasSymbol(token.getValue())) {
                throw new ParserException(stream.peek() != null && stream.peek().getType() == TokenType.LEFT_PAREN ? ParserExceptionType.UNDEFINED_FUNCTION : ParserExceptionType.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
            }

            return parser.getScope().getSymbol(token.getValue());
        }

        return null;
    }

    private Value parseBrace() throws EngineException {
        int braces = 1, paren = 0, brackets = 0;

        stream.next();

        ValueDict value = new ValueDict();

        List<Token> tokens = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.LEFT_BRACKET) {
                brackets++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                brackets--;
            } else if (stream.current().getType() == TokenType.LEFT_PAREN) {
                paren++;
            } else if (stream.current().getType() == TokenType.RIGHT_PAREN) {
                paren--;
            } else if (stream.current().getType() == TokenType.LEFT_BRACE) {
                braces++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACE) {
                braces--;
            }

            if ((stream.current().getType() == TokenType.COMMA || stream.current().getType() == TokenType.RIGHT_BRACE) && braces <= 1 && paren == 0 && brackets == 0) {
                // Ugly hack...
                if (tokens.isEmpty()) {
                    break;
                }

                TokenStream element = new TokenStream(tokens);

                List<Token> valueTokens = new ArrayList<>();

                while (element.hasNext()) {
                    if (element.current().getType() == TokenType.COL) {
                        break;
                    }

                    valueTokens.add(element.current());

                    element.next();
                }

                element.match(TokenType.COL);
                element.next();

                value.setEntry(createParser(new TokenStream(valueTokens)).parse(), createParser(new TokenStream(element.rest())).parse());

                tokens.clear();

                if (stream.current().getType() == TokenType.COMMA) {
                    stream.next();

                    continue;
                } else {
                    break;
                }
            }

            tokens.add(stream.current());

            stream.next();
        }

        return value;
    }

    private ExpressionParser createParser(TokenStream stream) {
        return new ExpressionParser(parser, stream, assignment);
    }
}
