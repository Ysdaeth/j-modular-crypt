package io.github.ysdaeth.jmodularcrypt.jcc.common.parser;

import io.github.ysdaeth.jmodularcrypt.common.parser.McfParametersParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Section;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McfParametersParserTest {
    private final McfParametersParser parser = new McfParametersParser();

    @Test
    void compose_shouldReturnCorrectMcfString() {
        Section[] sections = {
                new Section("a", "1"),
                new Section("bc", "d"),
                new Section("e", "fg")
        };
        String result = parser.compose(sections);
        assertEquals("a=1,bc=d,e=fg", result);
    }


    @Test
    void parse_shouldReturnCorrectSections() {
        String composed = "a=1,bc=d,e=fg";

        Section[] result = parser.parse(composed);

        assertEquals(3, result.length);
        assertEquals(new Section("a", "1"), result[0]);
        assertEquals(new Section("bc", "d"), result[1]);
        assertEquals(new Section("e", "fg"), result[2]);
    }

    @Test
    void parse_shouldReturnCorrectValueWithEqualSign() {
        String composed = "k==ab,k=ab=,k=a=b";

        Section[] result = parser.parse(composed);

        assertEquals(3, result.length);
        assertEquals(new Section("k", "=ab"), result[0]);
        assertEquals(new Section("k", "ab="), result[1]);
        assertEquals(new Section("k", "a=b"), result[2]);
    }

    @Test
    void compose_shouldThrowException_whenKeyIsBlank() {
        Section[] sections = {
                new Section(" ", "value")
        };
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.compose(sections)
        );
    }

    @Test
    void compose_shouldThrowException_whenKeyContainsInvalidCharacter() {
        Section[] sections = {
                new Section("a=b", "1")
        };
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.compose(sections)
        );

    }

    @Test
    void compose_shouldThrowException_whenValueContainsComma() {
        Section[] sections = {
                new Section("a", "1,2")
        };
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.compose(sections)
        );
    }

    @Test
    void composeAndParse_shouldBeInverseOperations() {
        Section[] original = {
                new Section("x", "10"),
                new Section("y", "20")
        };
        String composed = parser.compose(original);
        Section[] parsed = parser.parse(composed);
        assertArrayEquals(original, parsed);
    }

}