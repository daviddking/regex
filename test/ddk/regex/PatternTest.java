package ddk.regex;

import org.junit.Test;

import static org.junit.Assert.*;

public class PatternTest {

    @Test
    public void testAllLiteralMatch() {
        assertMatches("abc", "abc");
    }

    @Test
    public void testOptionalMetaCharacter() {
        assertMatches("abc", "?bc");
        assertMatches("abc", "a?c");
        assertMatches("abc", "ab?");
        assertMatches("abc", "???");
        assertDoesNotMatch("abc", "?");
        assertDoesNotMatch("abc", "a?");
    }

    @Test
    public void testWildcardMetaCharacter() {
        assertMatches("abc", "*");
        assertMatches("abc", "ab*");
        assertMatches("abc", "ab*");
        assertMatches("abc", "*c");
        assertMatches("abc", "*bc");
        assertMatches("abc", "*b*");
        assertDoesNotMatch("abc", "d*");
        assertDoesNotMatch("abc", "*d");
    }

    @Test
    public void testMixedWildcardAndOptional() {
        assertMatches("abc", "*?");
        assertMatches("abc", "*??");
        assertMatches("abc", "?*");
        assertMatches("abc", "??*");
        assertMatches("abc", "*?c");
        assertMatches("abc", "*???");
        assertDoesNotMatch("abc", "*????");
        assertDoesNotMatch("abc", "abc*?");
    }

    private void assertMatches(String input, String patternString) {
        Pattern pattern = new Pattern(patternString);
        assertTrue(pattern.matches(input));
    }

    private void assertDoesNotMatch(String input, String patternString) {
        Pattern pattern = new Pattern(patternString);
        assertFalse(pattern.matches(input));
    }


}