package passambler.parser.expression;

import passambler.exception.ParserException;
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
import passambler.util.ValueConstants;
import passambler.value.BooleanValue;

public class ExpressionParser {
    private List<Feature> features = new ArrayList<>();

    private Parser parser;
    private TokenList tokens;

    public ExpressionParser(Parser parser, TokenList tokens) {
        this.parser = parser;
        this.tokens = tokens;

        features.add(new AssignmentFeature());
        features.add(new LiteralFeature());
        features.add(new LookupFeature());
        features.add(new FunctionCallFeature());
        features.add(new ParenFeature());
        features.add(new DictFeature());
        features.add(new IndexAccessFeature());
        features.add(new ListFeature());
        features.add(new PropertyFeature());
        features.add(new FunctionFeature());
        features.add(new TernaryFeature());
    }

    public Parser getParser() {
        return parser;
    }

    public TokenList getTokens() {
        return tokens;
    }

    public Value parse() throws EngineException {
        List<Token> expression = new ArrayList<>();

        List<ValueOperatorPair> pairs = new ArrayList<>();

        Token lastOperator = null;

        int depth = 0;

        boolean escapeOperator = false;

        while (tokens.hasNext()) {
            Token token = tokens.current();

            /*
             * The reason I am doing special checks here is to make sure expressions as:
             *      x = 5 + 5
             *      x = func(y) return y + y
             * .. doesn't get parsed as:
             *      x = 5
             *      x = func(y) return y
             * just because there is an operator.
             */
            if (token.getType().isAssignmentOperator() || token.getType() == TokenType.ARROW) {
                escapeOperator = true;
            }

            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.LEFT_PAREN || token.getType() == TokenType.LEFT_BRACKET) {
                depth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE || token.getType() == TokenType.RIGHT_PAREN || token.getType() == TokenType.RIGHT_BRACKET) {
                depth--;
            }

            if (token.getType().isOperator() && escapeOperator) {
                expression.add(token);
            } else {
                expression.add(token);

                if (expression.size() == 1 && (token.getType().isUnaryOperator() || token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS)) {
                    if (token.getType() == TokenType.PLUS) {
                        token.setType(TokenType.UNARY_PLUS);
                    } else if (token.getType() == TokenType.MINUS) {
                        token.setType(TokenType.UNARY_MINUS);
                    }

                    expression.remove(expression.size() - 1);

                    lastOperator = token;
                }

                if (depth <= 0 && (token.getType().isOperator() || tokens.peek() == null)) {
                    if (token.getType().isOperator()) {
                        expression.remove(expression.size() - 1);
                    }

                    if (lastOperator != null && lastOperator.getType() == TokenType.AND) {
                        Value result = parsePairs(pairs);

                        if (result instanceof BooleanValue && !((BooleanValue) result).getValue()) {
                            return ValueConstants.FALSE;
                        }
                    }

                    pairs.add(new ValueOperatorPair(new ExpressionParser(parser, new TokenList(expression)).parseFeatures(), lastOperator));

                    expression.clear();

                    lastOperator = tokens.current();
                }
            }

            tokens.next();
        }

        return parsePairs(pairs);
    }

    private Value parsePairs(List<ValueOperatorPair> pairs) throws EngineException {
        Value value = null;

        performUnary(pairs);
        performPrecedence(pairs, TokenType.POWER, TokenType.MODULO);
        performPrecedence(pairs, TokenType.MULTIPLY, TokenType.DIVIDE);
        performPrecedence(pairs, TokenType.PLUS, TokenType.MINUS);
        performPrecedence(pairs, TokenType.RANGE, TokenType.INCLUSIVE_RANGE, TokenType.COMPARE);
        performPrecedence(pairs, TokenType.GT, TokenType.GTE, TokenType.LT, TokenType.LTE);
        performPrecedence(pairs, TokenType.EQUAL, TokenType.NEQUAL);
        performPrecedence(pairs, TokenType.AND, TokenType.OR, TokenType.XOR);

        for (int i = 0; i < pairs.size(); ++i) {
            ValueOperatorPair pair = pairs.get(i);

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

    private void performUnary(List<ValueOperatorPair> pairs) throws EngineException {
        for (int i = 0; i < pairs.size(); ++i) {
            ValueOperatorPair current = pairs.get(i);

            if (current.getOperator() != null && current.getOperator().getType().isUnaryOperator()) {
                current.setValue(current.getValue().onOperator(current.getValue(), current.getOperator()));
            }
        }
    }

    private void performPrecedence(List<ValueOperatorPair> pairs, TokenType... types) throws EngineException {
        for (int i = 0; i < pairs.size(); ++i) {
            ValueOperatorPair current = pairs.get(i);

            if (i > 0 && current.getOperator() != null && Arrays.asList(types).contains(current.getOperator().getType())) {
                ValueOperatorPair behind = pairs.get(i - 1);

                behind.setValue(behind.getValue().onOperator(current.getValue(), current.getOperator()));

                pairs.remove(current);
            }
        }
    }

    public Value parseFeatures() throws EngineException {
        Value currentValue = null;

        while (tokens.hasNext()) {
            boolean performed = false;

            for (Feature feature : features) {
                if (feature.canPerform(this, currentValue)) {
                    currentValue = feature.perform(this, currentValue);

                    performed = true;

                    break;
                }
            }

            // Ignore new lines
            if (!performed && tokens.current().getType() != TokenType.NEW_LINE) {
                throw new ParserException(ParserExceptionType.UNEXPECTED_TOKEN, tokens.current().getPosition(), tokens.current().getType());
            }

            tokens.next();
        }

        return currentValue;
    }
}
