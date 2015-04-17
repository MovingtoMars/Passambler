package passambler.parser.expression;

import passambler.exception.ParserException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import passambler.value.Value;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
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

    private TokenList tokens;

    public ExpressionParser(Parser parser, TokenList tokens) {
        this(parser, tokens, false);
    }

    public ExpressionParser(Parser parser, TokenList tokens, boolean assignment) {
        this.parser = parser;
        this.tokens = tokens;
        this.assignment = assignment;

        features.add(new FunctionCallFeature());
        features.add(new ParenFeature());
        features.add(new DictFeature());
        features.add(new IndexAccessFeature());
        features.add(new ListFeature());
        features.add(new SymbolFeature());
        features.add(new PropertyFeature());
        features.add(new AnonymousFunctionFeature());
    }

    public Parser getParser() {
        return parser;
    }

    public TokenList getTokens() {
        return tokens;
    }

    public boolean isAssignment() {
        return assignment;
    }

    public ExpressionParser createParser(TokenList tokens) {
        return new ExpressionParser(parser, tokens, assignment);
    }

    public Value parse() throws EngineException {
        List<Token> expressionTokens = new ArrayList<>();
        List<ValueOperatorPair> values = new ArrayList<>();

        Token lastOperator = null;

        int depth = 0;

        while (tokens.hasNext()) {
            Token token = tokens.current();

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            expressionTokens.add(token);

            if ((token.getType().isOperator() || tokens.peek() == null) && depth == 0) {
                if (token.getType().isOperator()) {
                    expressionTokens.remove(expressionTokens.size() - 1);
                }

                if (expressionTokens.isEmpty()) {
                    tokens.match(TokenType.MINUS, TokenType.PLUS);

                    boolean negate = tokens.current().getType() == TokenType.MINUS;

                    tokens.next();

                    tokens.match(TokenType.NUMBER);

                    BigDecimal number = new BigDecimal(tokens.current().getValue());

                    if (negate) {
                        number = number.negate();
                    } else {
                        number = number.plus();
                    }

                    expressionTokens.add(new Token(TokenType.NUMBER, number.toString(), tokens.current().getPosition()));
                }

                values.add(new ValueOperatorPair(createParser(new TokenList(expressionTokens)).parseFeatures(), lastOperator));

                expressionTokens.clear();

                lastOperator = tokens.current();
            }

            tokens.next();
        }

        Value value = null;

        performPrecedence(values, TokenType.RANGE);
        performPrecedence(values, TokenType.COMPARE);
        performPrecedence(values, TokenType.POWER, TokenType.MODULO);
        performPrecedence(values, TokenType.MULTIPLY, TokenType.DIVIDE);
        performPrecedence(values, TokenType.PLUS, TokenType.MINUS);
        performPrecedence(values, TokenType.GT, TokenType.GTE, TokenType.LT, TokenType.LTE);
        performPrecedence(values, TokenType.EQUAL, TokenType.NEQUAL);
        performPrecedence(values, TokenType.AND, TokenType.OR);

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

    private void performPrecedence(List<ValueOperatorPair> values, TokenType... types) throws EngineException {
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

        while (tokens.hasNext()) {
            if (tokens.current().getType() == TokenType.NOT) {
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
                    throw new ParserException(ParserExceptionType.UNEXPECTED_TOKEN, tokens.current().getPosition(), tokens.current().getType());
                }
            }

            tokens.next();
        }

        if (not) {
            if (!(currentValue instanceof BooleanValue)) {
                throw new ParserException(ParserExceptionType.NOT_A_BOOLEAN, tokens.get(0).getPosition());
            }

            currentValue = new BooleanValue(!((BooleanValue) currentValue).getValue());
        }

        return currentValue;
    }
}
