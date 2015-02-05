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
import passambler.val.ValList;
import passambler.val.ValNumber;
import passambler.val.ValString;

public class Evaluator {
    public static Val evaluate(Parser parser, TokenStream stream) throws ParserException {
        Val val = null;

        String currentFunctionName = null;

        while (stream.hasNext()) {
            Token token = stream.current();

            int paren = 0, brackets = 0, braces = 0;
            
            switch (token.getType()) {
                case LBRACE:
                case PIPE:
                    braces = 1;
                    
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
                            } else if (stream.current().getType() != Token.Type.COMMA) {
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
                            braces++;
                        } else if (tokenInBlock.getType() == Token.Type.RBRACE) {
                            braces--;
                        }

                        if (braces == 0 && tokenInBlock.getType() == Token.Type.RBRACE) {
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

                    Val[] compare = new Val[] {
                        val,
                        Evaluator.evaluate(parser, new TokenStream(stream.rest()))
                    };

                    if (compare[1] == null) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "invalid assertion");
                    }

                    switch (token.getType()) {
                        case EQUAL:
                            val = new ValBool(compare[0].getValue().equals(compare[1].getValue()));

                            break;
                        case NEQUAL:
                            val = new ValBool(!compare[0].getValue().equals(compare[1].getValue()));

                            break;
                        case GT:
                            val = new ValBool((((ValNumber) compare[0]).getValue() > ((ValNumber) compare[1]).getValue()));

                            break;
                        case LT:
                            val = new ValBool((((ValNumber) compare[0]).getValue() < ((ValNumber) compare[1]).getValue()));

                            break;
                        case GTE:
                            val = new ValBool((((ValNumber) compare[0]).getValue() >= ((ValNumber) compare[1]).getValue()));

                            break;
                        case LTE:
                            val = new ValBool((((ValNumber) compare[0]).getValue() <= ((ValNumber) compare[1]).getValue()));

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
                            val = val.onOperator(newVal, stream.back().getType());
                            
                            if (val == null) {
                                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, stream.back().getPosition(), stream.back().getType());
                            }
                        }
                    }

                    break;
                case DOT:
                    if (val == null || !stream.hasNext() || (stream.hasNext() && stream.peek().getType() != Token.Type.IDENTIFIER)) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition());
                    }
                
                    stream.next();
                    
                    String propertyName = stream.current().getStringValue();
                    
                    if (!val.hasProperty(propertyName)) {
                        throw new ParserException(ParserException.Type.UNDEFINED_PROPERTY, token.getPosition(), propertyName);
                    }

                    val = val.getProperty(propertyName);
                    
                    break;
                case LBRACKET:
                    List<Token> tokensInBrackets = new ArrayList<>();

                    brackets = 1;

                    stream.next();
                    
                    Val doubleDotLeft = null;
                    Val doubleDotRight = null;
                    
                    ValList list = new ValList();

                    while (stream.hasNext()) {
                        if ((stream.current().getType() == Token.Type.COMMA || stream.current().getType() == Token.Type.RBRACKET) && brackets == 1 && paren == 0) {
                            if (tokensInBrackets.isEmpty()) {
                                // If we go back two times, it should give us null if it's an empty declaration.
                                // If we are in an empty declaration, do nothing and leave the list empty.
                                
                                if (stream.back(2) != null) {
                                    throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "no value specified");
                                }
                            } else {
                                list.add(Evaluator.evaluate(parser, new TokenStream(tokensInBrackets)));
                            }
                            
                            if (stream.current().getType() == Token.Type.RBRACKET) {
                                brackets--;
                            } else {
                                stream.next();
                                
                                tokensInBrackets.clear();
                                
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
                        } else if (stream.current().getType() == Token.Type.DOT_DOUBLE && brackets == 1 && paren == 0) {
                            doubleDotLeft = Evaluator.evaluate(parser, new TokenStream(tokensInBrackets));
                            
                            tokensInBrackets.clear();
                            
                            stream.next();
                            
                            continue;
                        }
                        
                        if (brackets == 0 && paren == 0) {
                            if (doubleDotLeft != null) {
                                doubleDotRight = Evaluator.evaluate(parser, new TokenStream(tokensInBrackets));
                            }
                            
                            break;
                        }

                        tokensInBrackets.add(stream.current());

                        stream.next();
                    }

                    if (val == null && (list.getIndexCount() > 0 || tokensInBrackets.isEmpty())) {
                        val = list;
                    } else if (doubleDotLeft != null && doubleDotRight != null) {
                        if (!(doubleDotLeft instanceof ValNumber) || !(doubleDotRight instanceof ValNumber)) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "range syntax only supports numbers");
                        }
                        
                        int min = ((ValNumber) doubleDotLeft).getValueAsInteger();
                        int max = ((ValNumber) doubleDotRight).getValueAsInteger();
                        
                        if (min > max) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), String.format("%d can't be bigger than %d", min, max));
                        }
                        
                        ValList sequence = new ValList();

                        if (val == null) {
                            for (int current = min; current <= max; ++current) {
                                sequence.add(new ValNumber(current));
                            }
                        } else {
                            if (!(val instanceof IndexAccess)) {
                                throw new ParserException(ParserException.Type.NOT_INDEXED, token.getPosition());
                            }

                            IndexAccess indexAccess = (IndexAccess) val;

                            for (int current = min; current <= max; ++current) {
                                sequence.add(indexAccess.getIndex(current));
                            }
                        }
                        
                        val = sequence;
                    } else {
                        if (!(val instanceof IndexAccess)) {
                            throw new ParserException(ParserException.Type.NOT_INDEXED, token.getPosition());
                        }

                        IndexAccess indexAccess = (IndexAccess) val;
                    
                        Val indexVal = Evaluator.evaluate(parser, new TokenStream(tokensInBrackets));

                        if (!(indexVal instanceof ValNumber)) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "array index should be a number");
                        }

                        int index = ((ValNumber) indexVal).getValueAsInteger();

                        if (index < 0 || index > indexAccess.getIndexCount() - 1) {
                            throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, token.getPosition(), index, indexAccess.getIndexCount());
                        }

                        val = indexAccess.getIndex(index);
                    }

                    break;
                case LPAREN:
                    List<Token> tokensBetweenParen = new ArrayList<>();

                    Token.Type operator = null;
                    
                    paren = 1;

                    if (currentFunctionName != null || val instanceof ValBlock) {
                        if (stream.back(2) != null) {
                            operator = stream.back(2).getType();
                        }
                    } else {
                        performOperatorCheck(parser, stream);
                        
                        if (stream.back() != null) {
                            operator = stream.back().getType();
                        }
                    }
                    
                    stream.next();

                    while (stream.hasNext()) {
                        if (stream.current().getType() == Token.Type.LPAREN) {
                            paren++;
                        } else if (stream.current().getType() == Token.Type.RPAREN) {
                            paren--;
                        }

                        if (paren == 0) {
                            break;
                        }

                        tokensBetweenParen.add(stream.current());
                        
                        stream.next();
                    }
                    
                    if (currentFunctionName != null || val instanceof ValBlock) {
                        List<Token> argumentTokens = new ArrayList<>();
                        List<Val> arguments = new ArrayList<>();

                        for (Token tokenBetweenParen : tokensBetweenParen) {
                            if (tokenBetweenParen.getType() == Token.Type.LPAREN) {
                                paren++;
                            } else if (tokenBetweenParen.getType() == Token.Type.RPAREN) {
                                paren--;
                            }

                            argumentTokens.add(tokenBetweenParen);

                            if (paren == 0 && (tokenBetweenParen.getType() == Token.Type.COMMA || tokensBetweenParen.indexOf(tokenBetweenParen) == tokensBetweenParen.size() - 1)) {
                                if (tokenBetweenParen.getType() == Token.Type.COMMA) {
                                    argumentTokens.remove(argumentTokens.size() - 1);
                                }

                                arguments.add(Evaluator.evaluate(parser, new TokenStream(argumentTokens)));

                                argumentTokens.clear();
                            }
                        }

                        Function currentFunction = currentFunctionName != null ? parser.getScope().getFunction(currentFunctionName) : (Function) val;

                        if (currentFunction == null) {
                            continue;
                        }
                        
                        if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
                            throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, token.getPosition(), currentFunction, currentFunction.getArguments(), arguments.size());
                        }

                        for (int argument = 0; argument < arguments.size(); ++argument) {
                            if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                                throw new ParserException(ParserException.Type.INVALID_ARGUMENT, token.getPosition(), argument, currentFunction);
                            }
                        }

                        Val[] vals = new Val[arguments.size()];

                        Val functionReturn = currentFunction.invoke(parser, arguments.toArray(vals));

                        if (val == null || val instanceof ValBlock) {
                            val = functionReturn;
                        } else {
                            val = val.onOperator(functionReturn, operator);
                            
                            if (val == null) {
                                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, token.getPosition(), operator);
                            }
                        }

                        currentFunctionName = null;
                    } else if (!tokensBetweenParen.isEmpty()) {
                        Val valueInParen = Evaluator.evaluate(parser, new TokenStream(tokensBetweenParen));
                        
                        if (val == null) {
                            val = valueInParen;
                        } else {
                            val = val.onOperator(valueInParen, operator);
                            
                            if (val == null) {
                                throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, stream.back().getPosition(), stream.back().getType());
                            }
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
