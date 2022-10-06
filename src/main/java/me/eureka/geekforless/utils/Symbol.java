package me.eureka.geekforless.utils;

import java.util.List;
import java.util.Objects;

public class Symbol {
    private final List<Character> math = List.of('+', '-', '*', '/');

    public Pair<Character, Type> getPair(String expression, int index) {
        char character = expression.charAt(index);
        if (isNegative(expression, index)) return new Pair<>(character, Type.NUMBER);

        if (math.contains(character)) return new Pair<>(character, Type.MATH);
        if (Objects.equals(character, '(')) return new Pair<>(character, Type.OPEN_BRACKET);
        if (Objects.equals(character, ')')) return new Pair<>(character, Type.CLOSED_BRACKET);

        try {
            String value = String.valueOf(character);
            Integer.parseInt(value);
            return new Pair<>(character, Type.NUMBER);
        } catch (NumberFormatException exception) {
            if (Objects.equals(character, '.')) {
                return new Pair<>(character, Type.NUMBER);
            }
        }

        return new Pair<>(character, Type.LETTER);
    }

    private boolean isNegative(String expression, int index) {
        char character = expression.charAt(index);
        char previous, next;
        try {previous = expression.charAt(index - 1);} catch (IndexOutOfBoundsException exception) {return true;}
        try {next = expression.charAt(index + 1);} catch (IndexOutOfBoundsException exception) {return false;}

        // Example: 4 +-7
        if (Objects.equals(character, '-')) {
            if (math.contains(previous) || Objects.equals(previous, '(')) {
                try {
                    String value = String.valueOf(next);
                    Integer.parseInt(value);

                    return true;
                } catch (NumberFormatException exception) {
                    exception.fillInStackTrace();
                }
            }
        }

        return false;
    }
}
