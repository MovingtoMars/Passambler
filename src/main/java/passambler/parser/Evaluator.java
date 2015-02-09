package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.IndexedValue;
import passambler.value.ValueBlock;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class Evaluator {
    public static Value evaluate(Parser parser, TokenStream stream) throws ParserException {
        Value value = null;

        while (stream.hasNext()) {
            Token token = stream.current();

            switch (token.getType()) {
                case LBRACE:
                    value = parseBrace(parser, stream);

                    break;
                case STRING:
                case NUMBER:
                case IDENTIFIER:
                    value = parseSymbol(parser, stream);

                    break;
                case DOT:
                    value = parseProperty(parser, stream, value);

                    break;
                case LBRACKET:
                    value = parseIndex(parser, stream, value);
                    
                    break;
                case LPAREN:
                    value = parseParen(parser, stream, value);

                    break;
                default:
                    if (!token.getType().isOperator()) {
                        throw new ParserException(ParserException.Type.UNEXPECTED_TOKEN, token.getPosition(), token.getType());
                    }
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

                        if ((stream.current().getType() == Token.Type.AND || stream.current().getType() == Token.Type.OR) && paren == 0) {
                            break;
                        } else {
                            tokens.add(stream.current());

                            stream.next();
                        }
                    }

                    Value operatorChange = value.onOperator(evaluate(parser, new TokenStream(tokens)), operatorToken.getType());

                    if (operatorChange == null) {
                        throw new ParserException(ParserException.Type.UNSUPPORTED_OPERATOR, operatorToken.getPosition(), operatorToken.getType());
                    }

                    value = operatorChange;

                    continue;
                }
            }

            stream.next();
        }

        return value;
    }

    public static Value parseParen(Parser parser, TokenStream stream, Value currentValue) throws ParserException {
        List<Token> tokens = new ArrayList<>();

        int paren = 1;

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

            tokens.add(stream.current());

            stream.next();
        }

        if (currentValue instanceof ValueBlock) {
            List<Token> argumentTokens = new ArrayList<>();
            List<Value> arguments = new ArrayList<>();

            for (Token token : tokens) {
                if (token.getType() == Token.Type.LPAREN) {
                    paren++;
                } else if (token.getType() == Token.Type.RPAREN) {
                    paren--;
                }

                argumentTokens.add(token);

                if (paren == 0 && (token.getType() == Token.Type.COMMA || tokens.indexOf(token) == tokens.size() - 1)) {
                    if (token.getType() == Token.Type.COMMA) {
                        argumentTokens.remove(argumentTokens.size() - 1);
                    }

                    arguments.add(evaluate(parser, new TokenStream(argumentTokens)));

                    argumentTokens.clear();
                }
            }

            Function currentFunction = (Function) currentValue;

            if (currentFunction.getArguments() != -1 && currentFunction.getArguments() != arguments.size()) {
                throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, stream.current().getPosition(), currentFunction.getArguments(), arguments.size());
            }

            for (int argument = 0; argument < arguments.size(); ++argument) {
                if (!currentFunction.isArgumentValid(arguments.get(argument), argument)) {
                    throw new ParserException(ParserException.Type.INVALID_ARGUMENT, stream.current().getPosition(), argument);
                }
            }

            Value[] vals = new Value[arguments.size()];

            return currentFunction.invoke(parser, arguments.toArray(vals));
        } else if (!tokens.isEmpty()) {
            return evaluate(parser, new TokenStream(tokens));
        }

        return null;
    }

    public static Value parseIndex(Parser parser, TokenStream stream, Value currentValue) throws ParserException {
        List<Token> tokens = new ArrayList<>();

        int brackets = 1, paren = 0;

        stream.next();

        Value doubleDotLeft = null;
        Value doubleDotRight = null;

        ValueList inlineDeclaration = new ValueList();

        while (stream.hasNext()) {
            if ((stream.current().getType() == Token.Type.COMMA || stream.current().getType() == Token.Type.RBRACKET) && brackets == 1 && paren == 0) {
                if (tokens.isEmpty()) {
                    if (stream.back(2) != null) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "no value specified");
                    }
                } else {
                    inlineDeclaration.add(evaluate(parser, new TokenStream(tokens)));
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
            } else if (stream.current().getType() == Token.Type.DOT_DOUBLE && brackets == 1 && paren == 0) {
                doubleDotLeft = evaluate(parser, new TokenStream(tokens));

                tokens.clear();

                stream.next();

                continue;
            }

            if (brackets == 0 && paren == 0) {
                if (doubleDotLeft != null) {
                    doubleDotRight = evaluate(parser, new TokenStream(tokens));
                }

                break;
            }

            tokens.add(stream.current());

            stream.next();
        }

        if (doubleDotLeft != null && doubleDotRight != null) {
            if (!(doubleDotLeft instanceof ValueNum) || !(doubleDotRight instanceof ValueNum)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "range syntax only supports numbers");
            }

            int min = ((ValueNum) doubleDotLeft).getValueAsInteger();
            int max = ((ValueNum) doubleDotRight).getValueAsInteger();

            if (min > max) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), String.format("%d can't be bigger than %d", min, max));
            }

            ValueList list = new ValueList();

            if (currentValue == null) {
                for (int current = min; current <= max; ++current) {
                    list.add(new ValueNum(current));
                }
            } else {
                if (!(currentValue instanceof IndexedValue)) {
                    throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
                }

                IndexedValue indexedValue = (IndexedValue) currentValue;

                for (int current = min; current <= max; ++current) {
                    list.add(indexedValue.getIndex(current));
                }
            }

            return list;
        } else if (currentValue == null && (inlineDeclaration.getIndexCount() > 0 || tokens.isEmpty())) {
            return inlineDeclaration;
        } else {
            if (!(currentValue instanceof IndexedValue)) {
                throw new ParserException(ParserException.Type.NOT_INDEXED, stream.current().getPosition());
            }

            IndexedValue indexedValue = (IndexedValue) currentValue;

            Value indexValue = evaluate(parser, new TokenStream(tokens));

            if (!(indexValue instanceof ValueNum)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "array index should be a number");
            }

            int index = ((ValueNum) indexValue).getValueAsInteger();

            if (index < -indexedValue.getIndexCount() || index > indexedValue.getIndexCount() - 1) {
                throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, stream.current().getPosition(), index, indexedValue.getIndexCount());
            }

            if (index < 0) {
                return indexedValue.getIndex(indexedValue.getIndexCount() - Math.abs(index));
            } else {
                return indexedValue.getIndex(index);
            }
        }
    }

    public static Value parseProperty(Parser parser, TokenStream stream, Value currentValue) throws ParserException {
        stream.next();

        String propertyName = stream.current().getValue();

        if (!currentValue.hasProperty(propertyName)) {
            throw new ParserException(ParserException.Type.UNDEFINED_PROPERTY, stream.current().getPosition(), propertyName);
        }

        return currentValue.getProperty(propertyName);
    }

    public static Value parseSymbol(Parser parser, TokenStream stream) throws ParserException {
        Token token = stream.current();

        if (token.getType() == Token.Type.STRING) {
            return new ValueStr(token.getValue());
        } else if (token.getType() == Token.Type.NUMBER) {
            double number = Double.valueOf(token.getValue());

            if (stream.current() != stream.first() && stream.back().getType() == Token.Type.MINUS) {
                number *= -1;
            }

            if (stream.getPosition() > 0 && stream.back().getType() == Token.Type.DIVIDE && token.getValueAsInteger() == 0) {
                throw new ParserException(ParserException.Type.ZERO_DIVISION, token.getPosition());
            }

            return new ValueNum(number);
        } else if (token.getType() == Token.Type.IDENTIFIER) {
            if (!parser.getScope().hasSymbol(token.getValue())) {
                throw new ParserException(stream.peek() != null && stream.peek().getType() == Token.Type.LPAREN ? ParserException.Type.UNDEFINED_FUNCTION : ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
            }

            return parser.getScope().getSymbol(token.getValue());
        }

        return null;
    }

    public static Value parseBrace(Parser parser, TokenStream stream) throws ParserException {
        int braces = 1;

        if (stream.peek() != null && ((stream.peek().getType() == Token.Type.IDENTIFIER && stream.peek(2) != null && stream.peek(2).getType() == Token.Type.COL) || stream.peek().getType() == Token.Type.RBRACE)) {
            stream.next();

            ValueDict value = new ValueDict();

            List<Token> tokens = new ArrayList<>();

            while (stream.hasNext()) {
                Token token = stream.current();

                if (token.getType() == Token.Type.LBRACE) {
                    braces++;
                } else if (token.getType() == Token.Type.RBRACE) {
                    braces--;
                }

                tokens.add(token);

                if ((token.getType() == Token.Type.COMMA || stream.peek(2) == null) && braces == 1) {
                    if (token.getType() == Token.Type.COMMA) {
                        tokens.remove(tokens.size() - 1);
                    }

                    TokenStream element = new TokenStream(tokens);

                    if (element.current().getType() != Token.Type.IDENTIFIER) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "identifier expected");
                    }

                    String key = element.current().getValue();

                    element.next();
                    element.next();

                    value.set(new ValueStr(key), evaluate(parser, new TokenStream(element.rest())));

                    tokens.clear();
                }

                stream.next();
            }

            return value;
        } else {
            ValueBlock value = new ValueBlock(parser.getScope(), new ArrayList());

            stream.next();

            while (stream.hasNext()) {
                if (stream.current().getType() == Token.Type.LBRACE) {
                    braces++;
                } else if (stream.current().getType() == Token.Type.RBRACE) {
                    braces--;
                }

                if (braces == 0 && stream.current().getType() == Token.Type.RBRACE) {
                    break;
                } else {
                    value.addToken(stream.current());

                    stream.next();
                }
            }

            return value;
        }
    }
}
