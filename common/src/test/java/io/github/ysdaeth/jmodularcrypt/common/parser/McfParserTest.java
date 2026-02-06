package io.github.ysdaeth.jmodularcrypt.common.parser;

import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class McfParserTest {

    private final McfParser mcfParser = new McfParser();

    @Test
    void parsedValueShouldEqualString(){
        String expected = "$identifier$version$1$null$==";
        List<String> values = List.of("identifier","version","1","null","==");

        Section[] sections = values.stream()
                .map(v-> new Section("k",v))
                .toArray(Section[]::new);

        String actual = mcfParser.compose(sections);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void parserShouldThrowExceptionOnIllegalCharacter(){
        Section[] sections =new Section[]{
                new Section("k1","$"),
                new Section("k1","_$"),
                new Section("k1","_$_"),
                new Section("k1","$_"),
                new Section("k1",""),
                new Section("k1","\n"),
                new Section("k1",null)
        };
        Arrays.stream(sections).forEach(
                e -> Assertions.assertThrows(
                        RuntimeException.class,
                        ()->{ mcfParser.compose(new Section[]{e});}
                ));
    }

}