package passambler.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
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

    public Value parse() throws ParserException {
        Value value = null;

        boolean not = false;

        while (stream.hasNext()) {
            Token token = stream.current();

            if (token.getType() == Token.Type.NOT) {
                not = true;
            } else if (token.getType() == Token.Type.LBRACE) {
                value = parseBrace();
            } else if (token.getType() == Token.Type.STRING || token.getType() == Token.Type.NUMBER || token.getType() == Token.Type.IDENTIFIER) {
                value = parseSymbol();
            } else if (token.getType() == Token.Type.PERIOD) {
                value = parseProperty(value);
            } else if (token.getType() == Token.Type.LBRACKET) {
                value = parseIndexed(value);
            } else if (token.getType() == Token.Type.LPAREN) {
                value = parseParen(value);
            } else if (token.getType() == Token.Type.FN) {
                value = parseFunction();
            } else if (token.getType() == Token.Type.TERNARY) {
                value = parseTernary(value);
            } else if (!token.getType().isOperator()) {
                throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
            }

            if (value != null && stream.peek() != null) {
                int paren = 0;

                Token operatorToken = stream.peek();

                if (operatorToken.getType().isOperator()) {
                    stream.next();
                    stream.next();

                    List<Token> tokens = new ArrayList<>();

                    while (stream.hasNext()) {
                        if (stream.current().getType() == Token.Type.LPAREN) {
                            paren++;
                        } else if (stream.current().getType() == Token.Type.RPAREN) {
                            paren--;
                        }

                        if ((stream.current().getType() == Token.Type.AND || stream.current().getType() == Token.Type.OR || stream.current().getType() == Token.Type.TERNARY) && paren == 0) {
                            break;
                        } else {
                            tokens.add(stream.current());

                            stream.next();
                        }
                    }

                    Value operatorChange = value.onOperator(new ExpressionParser(parser, new TokenStream(tokens), assignment).parse(), operatorToken);

                    if (operatorChange == null) {
                        throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, operatorToken.getPosition(), operatorToken.getType());
                    }

                    value = operatorChange;

                    continue;
                }
            }

            stream.next();
        }

        if (not) {
            if (!(value instanceof ValueBool)) {
                throw new ParserException(ParserException.Type.EXPECTED_A_BOOL, stream.first().getPosition());
            }

            value = new ValueBool(!((ValueBool) value).getValue());
        }

        return value;
    }

    private Value parseTernary(Value currentValue) throws ParserException {
        if (!(currentValue instanceof ValueBool)) {
            throw new ParserException(ParserException.Type.EXPECTED_A_BOOL, stream.current().getPosition());
        }

        stream.next();

        List<Token> tokens = new ArrayList<>();

        int brackets = 0, paren = 0, braces = 0;

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.LBRACKET) {
                brackets++;
            } else if (stream.current().getType() == Token.Type.RBRACKET) {
                brackets--;
            } else if (stream.current().getType() == Token.Type.LPAREN) {
                paren++;
            } else if (stream.current().getType() == Token.Type.RPAREN) {
                paren--;
            } else if (stream.current().getType() == Token.Type.LBRACE) {
                braces++;
            } else if (stream.current().getType() == Token.Type.RBRACE) {
                braces--;
            }

            if (brackets == 0 && paren == 0 && braces == 0 && stream.current().getType() == Token.Type.COL) {
                break;
            } else {
                tokens.add(stream.current());

                stream.next();
            }
        }

        stream.match(Token.Type.COL);
        stream.next();

        Value ifTrue = new ExpressionParser(parser, new TokenStream(tokens), assignment).parse();
        Value ifFalse = new ExpressionParser(parser, new TokenStream(stream.rest()), assignment).parse();

        return ((ValueBool) currentValue).getValue() == true ? ifTrue : ifFalse;
    }

    private Value parseFunction() throws ParserException {
        stream.next();

        List<String> argumentNames = parser.argumentNames(stream);

        stream.next();

        Block callback = parser.block(stream);

        return new Function() {
            @Override
            public int getArguments() {
                return argumentNames.size();
            }

            @Override
            public boolean isArgumentValid(Value value, int argument) {
                return argument < argumentNames.size();
            }

            @Override
            public Value invoke(FunctionContext context) throws ParserException {
                for (int i = 0; i < argumentNames.size(); ++i) {
                    callback.getParser().getScope().setSymbol(argumentNames.get(i), context.getArgument(i));
                }

                return callback.invoke();
            }
        };
    }

    private Value parseParen(Value currentValue) throws ParserException {
        List<Token> tokens = new ArrayList<>();

        stream.next();

        int paren = 1;

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.LPAREN) {
                paren++;
            } else if (stream.current().getType() == Token.Type.RPAREN) {
                paren--;

                if (paren == 0) {
                    break;
                }
            }

            tokens.add(stream.current());

            stream.next();
        }

        if (paren != 0) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.first().getPosition(), "unmatching parens");
        }

        if (currentValue instanceof Function) {
            List<Token> argumentTokens = new ArrayList<>();
            List<Value> arguments = new ArrayList<>();

            int brackets = 0;

            for (Token token : tokens) {
                if (token.getType() == Token.Type.LPAREN) {
                    paren++;
                } else if (token.getType() == Token.Type.RPAREN) {
                    paren--;
                } else if (token.getType() == Token.Type.LBRACKET) {
                    brackets++;
                } else if (token.getType() == Token.Type.RBRACKET) {
                    brackets--;
                }

                argumentTokens.add(token);

                if (paren == 0 && brackets == 0 && (token.getType() == Token.Type.COMMA || tokens.indexOf(token) == tokens.size() - 1)) {
                    if (token.getType() == Token.Type.COMMA) {
                        argumentTokens.remove(argumentTokens.size() - 1);
                    }

                    arguments.add(new ExpressionParser(parser, new TokenStream(argumentTokens), assignment).parse());

                    argumentTokens.clear();
                }
            }

            if (paren != 0) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.first().getPosition(), "unmatching parens");
            }

            if (brackets != 0) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.first().getPosition(), "unmatching brackets");
            }

            Function currentFunction = (Function) currentValue;

            if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
                throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), currentFunction.getArguments(), arguments.size());
            }

            for (int argument = 0; argument < arguments.size(); ++argument) {
                if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                    throw new ParserException(ParserException.Type.INVALID_ARGUMENT, stream.first().getPosition(), argument + 1);
                }
            }

            Value[] vals = new Value[arguments.size()];

            return currentFunction.invoke(new FunctionContext(parser, arguments.toArray(vals), assignment));
        } else if (!tokens.isEmpty()) {
            return new ExpressionParser(parser, new TokenStream(tokens), assignment).parse();
        }

        return null;
    }

    private Value parseIndexed(Value currentValue) throws ParserException {
        List<Token> tokens = new ArrayList<>();

        int brackets = 1, paren = 0, braces = 0;

        stream.next();

        ValueList inlineDeclaration = new ValueList();

        while (stream.hasNext()) {
            if ((stream.current().getType() == Token.Type.COMMA || stream.current().getType() == Token.Type.RBRACKET) && brackets == 1 && paren == 0 && braces == 0) {
                if (tokens.isEmpty()) {
                    if (stream.back(2) != null) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "no value specified");
                    }
                } else {
                    inlineDeclaration.getValue().add(new ExpressionParser(parser, new TokenStream(tokens), assignment).parse());
                }

                if (stream.current().getType() == Token.Type.RBRACKET) {
                    brackets--;
                } else {
                    stream.next();

                    tokens.clear();

                    continue;
                }
            } else if (stream.current().getType() == Token.Type.LBRACKET) {
                brackets++;
            } else if (stream.current().getType() == Token.Type.RBRACKET) {
                brackets--;
            } else if (stream.current().getType() == Token.Type.LPAREN) {
                paren++;
            } else if (stream.current().getType() == Token.Type.RPAREN) {
                paren--;
            } else if (stream.current().getType() == Token.Type.LBRACE) {
                braces++;
            } else if (stream.current().getType() == Token.Type.RBRACE) {
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
            Value indexValue = new ExpressionParser(parser, new TokenStream(tokens), assignment).parse();

            if (indexValue instanceof ValueNum) {
                if (!(currentValue instanceof ValueList)) {
                    throw new ParserException(ParserException.Type.NOT_A_LIST);
                }

                ValueList list = (ValueList) currentValue;

                int index = ((ValueNum) indexValue).getValue().intValue();

                if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                    throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, null, index, list.getValue().size());
                }

                if (index < 0) {
                    return list.getValue().get(list.getValue().size() - Math.abs(index));
                } else {
                    return list.getValue().get(index);
                }
            } else {
                if (!(currentValue instanceof ValueDict)) {
                    throw new ParserException(ParserException.Type.NOT_A_DICT);
                }

                ValueDict dict = (ValueDict) currentValue;

                if (dict.getEntry(indexValue) == null) {
                    return Value.VALUE_NIL;
                }

                return dict.getEntry(indexValue);
            }
        }
    }

    private Value parseProperty(Value currentValue) throws ParserException {
        if (stream.peek() == null) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "missing property name");
        }

        stream.next();

        stream.match(Token.Type.IDENTIFIER);

        String propertyName = stream.current().getValue();

        if (!currentValue.hasProperty(propertyName)) {
            throw new ParserException(ParserException.Type.UNDEFINED_PROPERTY, stream.current().getPosition(), propertyName);
        }

        return currentValue.getProperty(propertyName).getValue();
    }

    private Value parseSymbol() throws ParserException {
        Token token = stream.current();

        if (token.getType() == Token.Type.STRING) {
            return new ValueStr(token.getValue());
        } else if (token.getType() == Token.Type.NUMBER) {
            StringBuilder number = new StringBuilder();

            if (stream.current() != stream.first() && stream.back().getType() == Token.Type.MINUS) {
                number.append("-");
            }

            number.append(token.getValue());

            if (stream.peek() != null && stream.peek().getType() == Token.Type.PERIOD && stream.peek(2) != null && stream.peek(2).getType() == Token.Type.NUMBER) {
                stream.next();
                stream.next();

                number.append(".");
                number.append(stream.current().getValue());
            }

            return new ValueNum(new BigDecimal(number.toString()));
        } else if (token.getType() == Token.Type.IDENTIFIER) {
            if (token.getValue().equals("true")) {
                return Value.VALUE_TRUE;
            } else if (token.getValue().equals("false")) {
                return Value.VALUE_FALSE;
            } else if (token.getValue().equals("nil")) {
                return Value.VALUE_NIL;
            }

            if (!parser.getScope().hasSymbol(token.getValue())) {
                throw new ParserException(stream.peek() != null && stream.peek().getType() == Token.Type.LPAREN ? ParserException.Type.UNDEFINED_FUNCTION : ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
            }

            return parser.getScope().getSymbol(token.getValue());
        }

        return null;
    }

    private Value parseBrace() throws ParserException {
        int braces = 1, paren = 0, brackets = 0;

        stream.next();

        ValueDict value = new ValueDict();

        List<Token> tokens = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.LBRACKET) {
                brackets++;
            } else if (stream.current().getType() == Token.Type.RBRACKET) {
                brackets--;
            } else if (stream.current().getType() == Token.Type.LPAREN) {
                paren++;
            } else if (stream.current().getType() == Token.Type.RPAREN) {
                paren--;
            } else if (stream.current().getType() == Token.Type.LBRACE) {
                braces++;
            } else if (stream.current().getType() == Token.Type.RBRACE) {
                braces--;
            }

            if ((stream.current().getType() == Token.Type.COMMA || stream.current().getType() == Token.Type.RBRACE) && braces <= 1 && paren == 0 && brackets == 0) {
                // Ugly hack...
                if (tokens.isEmpty()) {
                    break;
                }

                TokenStream element = new TokenStream(tokens);

                List<Token> valueTokens = new ArrayList<>();

                while (element.hasNext()) {
                    if (element.current().getType() == Token.Type.COL) {
                        break;
                    }

                    valueTokens.add(element.current());

                    element.next();
                }

                element.match(Token.Type.COL);
                element.next();

                value.setEntry(new ExpressionParser(parser, new TokenStream(valueTokens), assignment).parse(), new ExpressionParser(parser, new TokenStream(element.rest()), assignment).parse());

                tokens.clear();

                if (stream.current().getType() == Token.Type.COMMA) {
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
}
