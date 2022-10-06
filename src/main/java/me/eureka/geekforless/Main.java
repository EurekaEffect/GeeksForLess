package me.eureka.geekforless;

import me.eureka.geekforless.utils.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eureka
 * @startDate 06.10.2022 16:01
 * @endDate 06.10.2022 23:14
 */

public class Main {
    private static final List<Pair<Character, Type>> list = new ArrayList<>();
    private static final String NO_BRACKETS = "NO_BRACKETS";

    private static String EXPRESSION;
    private static String ORIGINAL;

    private static final Symbol symbol = new Symbol();

    public static void main(String[] args) throws IOException {
        File file = new File("data.txt");
        if (!file.exists()) Files.createFile(file.toPath());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        Scanner scanner = new Scanner(System.in);
        EXPRESSION = scanner.nextLine();
        ORIGINAL = EXPRESSION;

        EXPRESSION = EXPRESSION.replace(" ", "");

        char[] array = EXPRESSION.toCharArray();
        for (int i = 0; i < array.length; i++) {
            Pair<Character, Type> pair = symbol.getPair(EXPRESSION, i);

            list.add(pair);
        }

        findException();

        // Solve
        while (canSolve()) {
            char[] chars = solvePart(EXPRESSION).toCharArray();
            String brackets = solvePart(EXPRESSION);

            List<Pair<Character, Type>> list = new ArrayList<>();

            for (int i = 0; i < chars.length; i++) {
                Pair<Character, Type> pair = symbol.getPair(brackets, i);

                list.add(pair);
            }

            StringBuilder first = new StringBuilder();
            StringBuilder math = new StringBuilder();
            StringBuilder second = new StringBuilder();

            boolean canSolve = true;
            for (Pair<Character, Type> pair : list) {
                switch (pair.type()) {
                    case NUMBER -> {
                        if (math.isEmpty()) {
                            first.append(pair.symbol());
                        } else {
                            second.append(pair.symbol());
                        }
                    }
                    case MATH -> {
                        if (math.isEmpty()) {
                            math.append(pair.symbol());
                        } else {
                            canSolve = false;
                            swap(first, math, second);
                        }
                    }
                }
            }

            if (canSolve) swap(first, math, second);
        }

        System.out.println(EXPRESSION);
        writer.write(ORIGINAL + " = " + EXPRESSION);
        writer.close();
    }

    private static void swap(StringBuilder f, StringBuilder m, StringBuilder s) {
        String first = f.toString();
        String math = m.toString();
        String second = s.toString();

        String solve = String.valueOf(solve(f, m, s)).replace(".0", "");
        EXPRESSION = EXPRESSION.replace(first + math + second, solve);
    }

    private static boolean canSolve() {
        boolean hasMath = false;

        char[] array = EXPRESSION.toCharArray();
        for (int i = 0; i < array.length; i++) {
            Pair<Character, Type> pair = symbol.getPair(EXPRESSION, i);

            if (pair.type().equals(Type.MATH)) hasMath = true;
        }

        return hasMath && !Objects.equals(solvePart(EXPRESSION), NO_BRACKETS);
    }

    /*
    Priority:
    1. Brackets
    -2. Multiply and Divide-
    3. Plus and Minus
     */
    private static String solvePart(String expression) {
        int start = expression.indexOf('(') + 1;
        int end = expression.indexOf(')');

        try {
            return expression.substring(start, end);
        } catch (StringIndexOutOfBoundsException exception) {
            return expression;
        }
    }

    private static double solve(StringBuilder f, StringBuilder m, StringBuilder s) {
        double first = Double.parseDouble(f.toString());
        double second = 0;
        try {
            second = Double.parseDouble(s.toString());
        } catch (NumberFormatException exception) {
            EXPRESSION = EXPRESSION.replace("(" + f + ")", f.toString());
        }

        try {
            return switch (m.charAt(0)) {
                case '+' -> first + second;
                case '-' -> first - second;
                case '*' -> first * second;
                case '/' -> first / second;
                default -> 0;
            };
        } catch (StringIndexOutOfBoundsException exception) {
            return first;
        }
    }

    private static void findException() {
        AtomicInteger index = new AtomicInteger();

        list.forEach(pair -> {
            index.getAndIncrement();

            if (pair.type().equals(Type.LETTER)) {
                throw new MathSymbolException("Invalid expression at index " + index);
            }
        });
    }
}