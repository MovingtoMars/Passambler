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
                    
                    Val comp1 = val;
                    
                    List<Token> comp2tok = new ArrayList<>();
                    
                    stream.next();
                    
                    /* here we compute the next stmt to check, we won't go futher than && or ||. */
                    while (stream.hasNext()) {
                        if (stream.current().getType() == Token.Type.OR || stream.current().getType() == Token.Type.AND) {
                            break;
                        }
                        
                        comp2tok.add(stream.current());
                        
                        stream.next();
                    }
                    
                    stream.position--;

                    Val comp2 = Evaluator.evaluate(parser, new TokenStream(comp2tok));
                    
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

                            Function function = parser.getScope().getFunction(functionName);
                            
                            int openCount = 0, closeCount = 0;
                            
                            boolean inPar = false;
                            
                            List<Token> tokensInPar = new ArrayList<>();

                            while (stream.hasNext()) {
                                stream.next();
                                
                                if (stream.current().getType() == Token.Type.LPAREN) {
                                    openCount++;
                                    
                                    inPar = true;
                                } else if (stream.current().getType() == Token.Type.RPAREN) {
                                    closeCount++;
                                    
                                    inPar = true;
                                }
                                
                                tokensInPar.add(stream.current());

                                if (inPar && openCount == closeCount) {
                                    tokensInPar.remove(tokensInPar.size()-1);
                                    tokensInPar.remove(0);
                                    
                                    
                                    openCount = 0;
                                    closeCount = 0;
                                    
                                    break;
                                }
                            }
                            
                            if (openCount != closeCount) {
                                throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "unmatching parens");
                            }
                            
                            List<Token> argumentTokens = new ArrayList<>();
                            
                            List<Val> arguments = new ArrayList<>();
                            
                            for (Token t : tokensInPar) {
                                if (t.getType() == Token.Type.LPAREN) {
                                    openCount++;
                                } else if (t.getType() == Token.Type.RPAREN) {
                                    closeCount++;
                                }
                                
                                argumentTokens.add(t);
                                
                                if (openCount == closeCount && (t.getType() == Token.Type.COMMA || tokensInPar.indexOf(t) == tokensInPar.size() - 1)) {
                                    if (t.getType() == Token.Type.COMMA) {
                                        argumentTokens.remove(argumentTokens.size() - 1);
                                    }
                                    
                                    arguments.add(Evaluator.evaluate(parser, new TokenStream(argumentTokens)));
                                    
                                    argumentTokens.clear();
                                    
                                    openCount = 0;
                                    closeCount = 0;
                                }
                            }

                            if (function.getArguments() != -1 && function.getArguments() != arguments.size()) {
                                throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, token.getPosition(), functionName, function.getArguments(), arguments.size());
                            }
                            
                            for (int ii = 0; ii < arguments.size(); ++ii) {
                                if (!function.isArgumentValid(arguments.get(ii), ii)) {
                                    throw new ParserException(ParserException.Type.INVALID_ARGUMENT, token.getPosition(), ii, functionName);
                                }
                            }
                            
                            Val[] vals = new Val[arguments.size()];
                            
                            newVal = function.invoke(parser, arguments.toArray(vals));
                        } else {
                            if (!parser.getScope().hasVariable(token.getStringValue())) {
                                throw new ParserException(ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getStringValue());
                            }

                            newVal = parser.getScope().getVariable(token.getStringValue());
                        }
                    }

                    if (val == null) {
                        val = newVal;
                    } else {
                        if (!val.isOperatorSupported(stream.back().getType())) {
                            throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, stream.back().getPosition(), stream.back().getType());
                        }

                        val = val.onOperator(newVal, stream.back().getType());
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
                    performOperatorCheck(parser, stream);

                    Token.Type operator = stream.current() == stream.first() ? Token.Type.PLUS : stream.back().getType();

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

                    if (!tokensInPar.isEmpty()) {
                        if (val == null) {
                            val = Evaluator.evaluate(parser, new TokenStream(tokensInPar));
                        } else {
                            val = val.onOperator(Evaluator.evaluate(parser, new TokenStream(tokensInPar)), operator);
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