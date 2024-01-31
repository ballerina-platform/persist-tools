package io.ballerina.persist.inflector;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Singularizer {
    private Singularizer() {}

    public static String singularize(String word) {
        for (String rule : Rules.UNCOUNTABLE_RULES) {
            if (Pattern.matches(rule, word)) {
                return word;
            }
        }
        for (String[] irregularRule: Rules.IRREGULAR_RULES) {
            if (irregularRule[0].equals(word)) {
                return word;
            }
            if (irregularRule[1].equals(word)) {
                return irregularRule[0];
            }
        }
        for (String[] singularizationRule: Rules.SINGULARIZATION_RULES) {
            Matcher matcher = Pattern.compile(singularizationRule[0]).matcher(word);
            if (matcher.find()) {
                return matcher.replaceFirst(singularizationRule[1]);
            }
        }
        return word;
    }
}
