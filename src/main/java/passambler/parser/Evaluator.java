package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.val.Val;
import passambler.scanner.Token;
import passambler.scanner.TokenStream;
import passambler.val.IndexAccess;
import passambler.val.ValBlock;
import passambler.val.ValBool;
import passambler.val.ValNumber;
import passambler.val.ValString;

public class Evaluator {
    public static Val evaluate(Parser parser, TokenStream stream) throws ParserException {
        Val val = null;

        String currentFunctionName = null;
        Token.Type currentFunctionOperator = null;

        while (stream.hasNext()) {
            Token token = stream.current();

            switch (token.getType()) {
                case LBRACE:
                case PIPE:
                    int leftBraceCount = 1, rightBraceCount = 0;

                    boolean commaCheck = false;

                    List<String> argumentNames = new ArrayList<>();

                    if (stream.current().getType() == Token.Type.PIPE) {
                        while (stream.hasNext()) {
                            stream.next();

                            if (stream.current().getType() == Token.Type.PIPE) {
                                if (stream.back().getType() == Token.Type.COMMA) {
                                    throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, stream.current().getPosition(), stream.current().getType());
                                }

                                break;
                            } else if (stream.current().getType() == Token.Type.IDENTIFIER) {
                                if (commaCheck && stream.back().getType() != Token.Type.COMMA) {
                                    throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, stream.current().getPosition(), stream.current().getType());
                                }

                                commaCheck = true;

                                argumentNames.add(stream.current().getStringValue());
                            } else if (stream.current().getType() == Token.Type.COMMA) {
                                /* falltrough */
                            } else {
                                throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, stream.current().getPosition(), stream.current().getType());
                            }
                        }

                        stream.next();
                    }

                    val = new ValBlock(parser.getScope(), argumentNames);

                    stream.next();

                    while (stream.hasNext()) {
                        Token tokenInBlock = stream.current();

                        if (tokenInBlock.getType() == Token.Type.LBRACE) {
                            leftBraceCount++;
                        } else if (tokenInBlock.getType() == Token.Type.RBRACE) {
                            rightBraceCount++;
                        }

                        if (tokenInBlock.getType() == Token.Type.RBRACE && leftBraceCount == rightBraceCount) {
                            return val;
                        } else {
                            ((ValBlock) val).addToken(tokenInBlock);

                            stream.next();
                        }
                    }

                    break;
                case PLUS:
                case MINUS:
                case MULTIPLY:
                case DIVIDE:
                case POWER:
                    if (token == stream.last()) {
                        throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
                    }

                    break;
                case EQUAL:
                case NEQUAL:
                case LT:
                case GT:
                case LTE:
                case GTE:
                    if (val == null) {
                        throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
                    }

                    stream.next();

                    Val comp1 = val;
                    Val comp2 = Evaluator.evaluate(parser, new TokenStream(stream.rest()));

                    if (comp2 == null) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "invalid assertion");
                    }

                    switch (token.getType()) {
                        case EQUAL:
                            val = new ValBool(comp1.getValue().equals(comp2.getValue()));

                            break;
                        case NEQUAL:
                            val = new ValBool(!comp1.getValue().equals(comp2.getValue()));

                            break;
                        case GT:
                            val = new ValBool((((ValNumber) comp1).getValue() > ((ValNumber) comp2).getValue()));

                            break;
                        case LT:
                            val = new ValBool((((ValNumber) comp1).getValue() < ((ValNumber) comp2).getValue()));

                            break;
                        case GTE:
                            val = new ValBool((((ValNumber) comp1).getValue() >= ((ValNumber) comp2).getValue()));

                            break;
                        case LTE:
                            val = new ValBool((((ValNumber) comp1).getValue() <= ((ValNumber) comp2).getValue()));

                            break;
                    }

                    break;
                case STRING:
                case NUMBER:
                case IDENTIFIER:
                    performOperatorCheck(parser, stream);

                    Val newVal = null;

                    if (token.getType() == Token.Type.STRING) {
                        newVal = new ValString(token.getStringValue());
                    } else if (token.getType() == Token.Type.NUMBER) {
                        double number = Double.valueOf(token.getStringValue());

                        if (stream.current() != stream.first() && stream.back().getType() == Token.Type.MINUS) {
                            number *= -1;
                        }

                        newVal = new ValNumber(number);

                        if (stream.getPosition() > 0 && stream.back().getType() == Token.Type.DIVIDE && token.getIntValue() == 0) {
                            throw new ParserException(ParserException.Type.ZERO_DIVISION, token.getPosition());
                        }
                    } else if (token.getType() == Token.Type.IDENTIFIER) {
                        if (stream.peek() != null && stream.peek().getType() == Token.Type.LPAREN) {
                            String functionName = token.getStringValue();

                            if (!parser.getScope().hasFunction(functionName)) {
                                throw new ParserException(ParserException.Type.UNDEFINED_FUNCTION, token.getPosition(), functionName);
                            }

                            currentFunctionOperator = stream.current() == stream.first() ? Token.Type.PLUS : stream.back().getType();
                            currentFunctionName = functionName;
                        } else {
                            if (!parser.getScope().hasVariable(token.getStringValue())) {
                                throw new ParserException(ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getStringValue());
                            }

                            newVal = parser.getScope().getVariable(token.getStringValue());
                        }
                    }

                    if (newVal != null) {
                        if (val == null) {
                            val = newVal;
                        } else {
                            if (!val.isOperatorSupported(stream.back().getType())) {
                                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, stream.back().getPosition(), stream.back().getType());
                            }

                            val = val.onOperator(newVal, stream.back().getType());
                        }
                    }

                    break;
                case LBRACKET:
                    if (!(val instanceof IndexAccess)) {
                        throw new ParserException(ParserException.Type.NOT_INDEXED, token.getPosition());
                    }

                    IndexAccess indexAccess = (IndexAccess) val;

                    List<Token> tokensInBrackets = new ArrayList<>();

                    int brOpen = 1, brClose = 0;

                    stream.next();

                    while (stream.hasNext()) {
                        if (stream.current().getType() == Token.Type.LBRACKET) {
                            brOpen++;
                        } else if (stream.current().getType() == Token.Type.RBRACKET) {
                            brClose++;

                            if (brOpen == brClose) {
                                break;
                            }
                        }

                        tokensInBrackets.add(stream.current());

                        stream.next();
                    }

                    if (brOpen != brClose) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "unmatching brackets");
                    }

                    Val indexVal = Evaluator.evaluate(parser, new TokenStream(tokensInBrackets));

                    if (!(indexVal instanceof ValNumber)) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "array index should be a number");
                    }

                    int index = ((ValNumber) indexVal).getValueAsInteger();

                    if (index < 0 || index > indexAccess.getIndexCount() - 1) {
                        throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, token.getPosition(), index, indexAccess.getIndexCount());
                    }

                    val = indexAccess.getIndex(index);

                    break;
                case LPAREN:
                    List<Token> tokensInPar = new ArrayList<>();

                    int parOpen = 1, parClose = 0;

                    while (stream.hasNext()) {
                        stream.next();

                        if (stream.current().getType() == Token.Type.LPAREN) {
                            parOpen++;
                        } else if (stream.current().getType() == Token.Type.RPAREN) {
                            parClose++;
                        }

                        if (parOpen == parClose) {
                            break;
                        }

                        tokensInPar.add(stream.current());
                    }

                    if (parOpen != parClose) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "unmatching parens");
                    }

                    if (currentFunctionName != null) {
                        List<Token> argumentTokens = new ArrayList<>();
                        List<Val> arguments = new ArrayList<>();

                        int parCount = 0;

                        for (Token t : tokensInPar) {
                            if (t.getType() == Token.Type.LPAREN) {
                                parCount++;
                            } else if (t.getType() == Token.Type.RPAREN) {
                                parCount--;
                            }

                            argumentTokens.add(t);

                            if (parCount == 0 && (t.getType() == Token.Type.COMMA || tokensInPar.indexOf(t) == tokensInPar.size() - 1)) {
                                if (t.getType() == Token.Type.COMMA) {
                                    argumentTokens.remove(argumentTokens.size() - 1);
                                }

                                arguments.add(Evaluator.evaluate(parser, new TokenStream(argumentTokens)));

                                argumentTokens.clear();

                                parCount = 0;
                            }
                        }

                        Function currentFunction = parser.getScope().getFunction(currentFunctionName);

                        if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
                            throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, token.getPosition(), currentFunction, currentFunction.getArguments(), arguments.size());
                        }

                        for (int ii = 0; ii < arguments.size(); ++ii) {
                            if (!currentFunction.isArgumentValid(arguments.get(ii), ii)) {
                                throw new ParserException(ParserException.Type.INVALID_ARGUMENT, token.getPosition(), ii, currentFunction);
                            }
                        }

                        Val[] vals = new Val[arguments.size()];

                        Val functionReturn = currentFunction.invoke(parser, arguments.toArray(vals));

                        if (val == null) {
                            val = functionReturn;
                        } else {
                            val = val.onOperator(functionReturn, currentFunctionOperator);
                        }

                        currentFunctionName = null;
                        currentFunctionOperator = null;
                    } else if (!tokensInPar.isEmpty()) {
                        performOperatorCheck(parser, stream);

                        if (val == null) {
                            val = Evaluator.evaluate(parser, new TokenStream(tokensInPar));
                        } else {
                            val = val.onOperator(Evaluator.evaluate(parser, new TokenStream(tokensInPar)), stream.current() == stream.first() ? Token.Type.PLUS : stream.back().getType());
                        }
                    }

                    break;
                default:
                    throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
            }

            stream.next();
        }

        return val;
    }

    public static void performOperatorCheck(Parser parser, TokenStream stream) throws ParserException {
        if (stream.current() != stream.first()) {
            Token.Type type = stream.back().getType();

            if (type != Token.Type.MINUS && type != Token.Type.MULTIPLY && type != Token.Type.DIVIDE && type != Token.Type.POWER && type != Token.Type.PLUS) {
                throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, stream.current().getPosition(), type);
            }
        }
    }
}
