package me.puthvang.azerty.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Type {

    static Set<String> compareWithBoolean = new HashSet<>(Arrays.asList("0", "n", "no", "false", "1", "y", "yes", "true"));

    public static boolean isBoolean(String s){
        return compareWithBoolean.contains(s.toLowerCase(Locale.ROOT));
    }

    public static boolean isTrue(String s){
        return (isBoolean(s) && (s.equalsIgnoreCase("1")
                || s.equalsIgnoreCase("y")
                || s.equalsIgnoreCase("yes")
                || s.equalsIgnoreCase("true")));
    }

    public static boolean isFalse(String s){
        return (isBoolean(s) && (s.equalsIgnoreCase("0")
                || s.equalsIgnoreCase("n")
                || s.equalsIgnoreCase("no")
                || s.equalsIgnoreCase("false")));
    }

}
