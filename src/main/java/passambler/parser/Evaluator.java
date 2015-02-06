package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.val.Val;
import passambler.scanner.Token;
import passambler.scanner.TokenStream;
import passambler.val.IndexAccess;
import passambler.val.ValBlock;
import passambler.val.ValList;
import passambler.val.ValNum;
import passambler.val.ValStr;

public class Evaluator {
    public static Val evaluate(Parser parser, TokenStream stream) throws ParserException {
        Val val = null;
        
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
                case STRING:
                case NUMBER:
                case IDENTIFIER:
                    if (token.getType() == Token.Type.STRING) {
                        val = new ValStr(token.getStringValue());
                    } else if (token.getType() == Token.Type.NUMBER) {
                        double number = Double.valueOf(token.getStringValue());

                        if (stream.current() != stream.first() && stream.back().getType() == Token.Type.MINUS) {
                            number *= -1;
                        }

                        val = new ValNum(number);

                        if (stream.getPosition() > 0 && stream.back().getType() == Token.Type.DIVIDE && token.getIntValue() == 0) {
                            throw new ParserException(ParserException.Type.ZERO_DIVISION, token.getPosition());
                        }
                    } else if (token.getType() == Token.Type.IDENTIFIER) {
                        if (!parser.getScope().hasSymbol(token.getStringValue())) {
                            throw new ParserException(stream.peek().getType() == Token.Type.LPAREN ? ParserException.Type.UNDEFINED_FUNCTION : ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getStringValue());
                        }

                        val = parser.getScope().getSymbol(token.getStringValue());
                    }
                
                    break;
                case DOT:
                    if (val == null || !stream.hasNext() || (stream.hasNext() && stream.peek().getType() != Token.Type.IDENTIFIER)) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "property name not found");
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
                                list.add(evaluate(parser, new TokenStream(tokensInBrackets)));
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
                            doubleDotLeft = evaluate(parser, new TokenStream(tokensInBrackets));
                            
                            tokensInBrackets.clear();
                            
                            stream.next();
                            
                            continue;
                        }
                        
                        if (brackets == 0 && paren == 0) {
                            if (doubleDotLeft != null) {
                                doubleDotRight = evaluate(parser, new TokenStream(tokensInBrackets));
                            }
                            
                            break;
                        }

                        tokensInBrackets.add(stream.current());

                        stream.next();
                    }
                    
                    if (doubleDotLeft != null && doubleDotRight != null) {
                        if (!(doubleDotLeft instanceof ValNum) || !(doubleDotRight instanceof ValNum)) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "range syntax only supports numbers");
                        }
                        
                        int min = ((ValNum) doubleDotLeft).getValueAsInteger();
                        int max = ((ValNum) doubleDotRight).getValueAsInteger();
                        
                        if (min > max) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), String.format("%d can't be bigger than %d", min, max));
                        }
                        
                        ValList sequence = new ValList();

                        if (val == null) {
                            for (int current = min; current <= max; ++current) {
                                sequence.add(new ValNum(current));
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
                    } else if (val == null && (list.getIndexCount() > 0 || tokensInBrackets.isEmpty())) {
                        val = list;
                    } else {
                        if (!(val instanceof IndexAccess)) {
                            throw new ParserException(ParserException.Type.NOT_INDEXED, token.getPosition());
                        }

                        IndexAccess indexAccess = (IndexAccess) val;
                    
                        Val indexVal = evaluate(parser, new TokenStream(tokensInBrackets));

                        if (!(indexVal instanceof ValNum)) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, token.getPosition(), "array index should be a number");
                        }

                        int index = ((ValNum) indexVal).getValueAsInteger();

                        if (index < 0 || index > indexAccess.getIndexCount() - 1) {
                            throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, token.getPosition(), index, indexAccess.getIndexCount());
                        }

                        val = indexAccess.getIndex(index);
                    }

                    break;
                case LPAREN:
                    List<Token> tokensBetweenParen = new ArrayList<>();

                    paren = 1;
                    
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
                    
                    if (val instanceof ValBlock) {
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

                                arguments.add(evaluate(parser, new TokenStream(argumentTokens)));

                                argumentTokens.clear();
                            }
                        }

                        Function currentFunction = (Function) val;

                        if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
                            throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, token.getPosition(), currentFunction, currentFunction.getArguments(), arguments.size());
                        }

                        for (int argument = 0; argument < arguments.size(); ++argument) {
                            if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                                throw new ParserException(ParserException.Type.INVALID_ARGUMENT, token.getPosition(), argument, currentFunction);
                            }
                        }

                        Val[] vals = new Val[arguments.size()];

                        val = currentFunction.invoke(parser, arguments.toArray(vals));
                    } else if (!tokensBetweenParen.isEmpty()) {
                        val = evaluate(parser, new TokenStream(tokensBetweenParen));
                    }

                    break;
                default:
                    if (!token.getType().isOperator()) {
                        throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
                    }
            }
            
            // Here check if the next is an operator, and then keep looping until next matching operator is found.
            if (val != null && stream.peek() != null) {
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
                        
                        if ((stream.current().getType() == Token.Type.AND || stream.current().getType() == Token.Type.OR) && paren == 0) {
                            break;
                        } else {
                            tokens.add(stream.current());
                            
                            stream.next();
                        }
                    }
                    
                    Val valOnOperator = val.onOperator(evaluate(parser, new TokenStream(tokens)), operatorToken.getType());
                    
                    if (valOnOperator == null) {
                        throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, operatorToken.getPosition(), operatorToken.getType());
                    }
                    
                    val = valOnOperator;
                    
                    // We use continue here so the last stream.next() doesn't get called.
                    // It already gets called in the while loop.
                    continue;
                }
            }
            
            stream.next();
        }

        return val;
    }
}