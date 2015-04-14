package passambler.parser.expression;

import passambler.exception.ParserException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.Parser;
import passambler.parser.expression.feature.*;
import passambler.value.BooleanValue;

public class ExpressionParser {
    private List<Feature> features = new ArrayList<>();
    
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

        features.add(new FunctionCallFeature());
        features.add(new ParenFeature());
        features.add(new DictOrBlockFeature());
        features.add(new IndexedOrArrayFeature());
        features.add(new SymbolFeature());
        features.add(new PropertyFeature());
        features.add(new AnonymousFunctionFeature());
    }

    public Parser getParser() {
        return parser;
    }

    public TokenStream getStream() {
        return stream;
    }

    public boolean isAssignment() {
        return assignment;
    }

    public ExpressionParser createParser(TokenStream stream) {
        return new ExpressionParser(parser, stream, assignment);
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

                values.add(new ValueOperatorPair(createParser(new TokenStream(tokens)).parseFeatures(), lastOperator));

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

    public Value parseFeatures() throws EngineException {
        Value currentValue = null;
        
        boolean not = false;

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.NOT) {
                not = true;
            } else {
                boolean performed = false;
                
                for (Feature feature : features) {
                    if (feature.canPerform(this, currentValue)) {
                        currentValue = feature.perform(this, currentValue);

                        performed = true;
                        
                        break;
                    }
                }

                if (!performed) {
                    throw new ParserException(ParserExceptionType.UNEXPECTED_TOKEN, stream.current().getPosition(), stream.current().getType());
                }
            }

            stream.next();
        }

        if (not) {
            if (!(currentValue instanceof BooleanValue)) {
                throw new ParserException(ParserExceptionType.EXPECTED_A_BOOL, stream.first().getPosition());
            }

            currentValue = new BooleanValue(!((BooleanValue) currentValue).getValue());
        }

        return currentValue;
    }
}
