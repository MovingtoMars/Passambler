package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.IndexedValue;
import passambler.value.ValueBlock;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class ExpressionParser {
    private Parser parser;

    private TokenStream stream;

    public ExpressionParser(Parser parser, TokenStream stream) {
        this.parser = parser;

        this.stream = stream;
    }

    public Value parse() throws ParserException {
        Value value = null;

        while (stream.hasNext()) {
            Token token = stream.current();

            if (token.getType() == Token.Type.LBRACE) {
                value = parseBrace();
            } else if (token.getType() == Token.Type.STRING || token.getType() == Token.Type.NUMBER || token.getType() == Token.Type.IDENTIFIER) {
                value = parseSymbol();
            } else if (token.getType() == Token.Type.DOT) {
                value = parseProperty(value);
            } else if (token.getType() == Token.Type.LBRACKET) {
                value = parseIndex(value);
            } else if (token.getType() == Token.Type.LPAREN) {
                value = parseParen(value);
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

                        if ((stream.current().getType() == Token.Type.AND || stream.current().getType() == Token.Type.OR) && paren == 0) {
                            break;
                        } else {
                            tokens.add(stream.current());

                            stream.next();
                        }
                    }

                    Value operatorChange = value.onOperator(new ExpressionParser(parser, new TokenStream(tokens)).parse(), operatorToken.getType());

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

        if (currentValue instanceof ValueBlock) {
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

                    arguments.add(new ExpressionParser(parser, new TokenStream(argumentTokens)).parse());

                    argumentTokens.clear();
                }
            }

            Procedure currentProcedure = (Procedure) currentValue;

            if (currentProcedure.getArguments() != -1 && currentProcedure.getArguments() != arguments.size()) {
                throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, stream.first().getPosition(), currentProcedure.getArguments(), arguments.size());
            }

            for (int argument = 0; argument < arguments.size(); ++argument) {
                if (!currentProcedure.isArgumentValid(arguments.get(argument), argument)) {
                    throw new ParserException(ParserException.Type.INVALID_ARGUMENT, stream.first().getPosition(), argument + 1);
                }
            }

            Value[] vals = new Value[arguments.size()];

            return currentProcedure.invoke(parser, arguments.toArray(vals));
        } else if (!tokens.isEmpty()) {
            return new ExpressionParser(parser, new TokenStream(tokens)).parse();
        }

        return null;
    }

    private Value parseIndex(Value currentValue) throws ParserException {
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
                    inlineDeclaration.add(new ExpressionParser(parser, new TokenStream(tokens)).parse());
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
                doubleDotLeft = new ExpressionParser(parser, new TokenStream(tokens)).parse();

                tokens.clear();

                stream.next();

                continue;
            }

            if (brackets == 0 && paren == 0) {
                if (doubleDotLeft != null) {
                    doubleDotRight = new ExpressionParser(parser, new TokenStream(tokens)).parse();
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
                    list.add(indexedValue.getIndex(new ValueNum(current)));
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

            Value indexValue = new ExpressionParser(parser, new TokenStream(tokens)).parse();

            if (!(indexValue instanceof ValueNum)) {
                throw new ParserException(ParserException.Type.BAD_SYNTAX, stream.current().getPosition(), "array index should be a number");
            }

            if (indexValue instanceof ValueNum) {
                int index = ((ValueNum) indexValue).getValueAsInteger();

                if (index < -indexedValue.getIndexCount() || index > indexedValue.getIndexCount() - 1) {
                    throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, stream.current().getPosition(), index, indexedValue.getIndexCount());
                }

                if (index < 0) {
                    return indexedValue.getIndex(new ValueNum(indexedValue.getIndexCount() - Math.abs(index)));
                } else {
                    return indexedValue.getIndex(new ValueNum(index));
                }
            } else {
                return indexedValue.getIndex(indexValue);
            }
        }
    }

    private Value parseProperty(Value currentValue) throws ParserException {
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

            if (stream.back() != null && stream.back().getType() == Token.Type.DIVIDE && token.getValueAsInteger() == 0) {
                throw new ParserException(ParserException.Type.ZERO_DIVISION, token.getPosition());
            }

            if (stream.current() != stream.first() && stream.back().getType() == Token.Type.MINUS) {
                number.append("-");
            }

            number.append(token.getValue());

            if (stream.peek() != null && stream.peek().getType() == Token.Type.DOT && stream.peek(2) != null && stream.peek(2).getType() == Token.Type.NUMBER) {
                stream.next();
                stream.next();

                number.append(".");
                number.append(stream.current().getValue());
            }

            return new ValueNum(Double.valueOf(number.toString()));
        } else if (token.getType() == Token.Type.IDENTIFIER) {
            if (!parser.getScope().hasSymbol(token.getValue())) {
                throw new ParserException(stream.peek() != null && stream.peek().getType() == Token.Type.LPAREN ? ParserException.Type.UNDEFINED_PROCEDURE : ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
            }

            return parser.getScope().getSymbol(token.getValue());
        }

        return null;
    }

    private Value parseBrace() throws ParserException {
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

                    element.match(Token.Type.IDENTIFIER);

                    String key = element.current().getValue();

                    element.next();

                    element.match(Token.Type.COL);

                    if (!element.hasNext()) {
                        throw new ParserException(ParserException.Type.BAD_SYNTAX, element.current().getPosition(), "value of property missing");
                    }

                    element.next();

                    value.set(new ValueStr(key), new ExpressionParser(parser, new TokenStream(element.rest())).parse());

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
