package ddk.regex.driver;

import ddk.regex.Pattern;

public class PatternMatcher {

    public static void main(String[] args) {
        Pattern pattern = new Pattern(args[1]);
        System.out.println(pattern.matches(args[0]));
    }
}
