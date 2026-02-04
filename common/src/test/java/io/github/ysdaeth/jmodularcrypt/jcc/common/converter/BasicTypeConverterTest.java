package io.github.ysdaeth.jmodularcrypt.jcc.common.converter;

import io.github.ysdaeth.jmodularcrypt.common.converter.BasicTypeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Test for default test converter with types dedicated to that converter.
 * This is not meant to be reusable test, it tests functionality of that single specific converter only
 * {@link BasicTypeConverter}
 */
class BasicTypeConverterTest {
    private final BasicTypeConverter converter = new BasicTypeConverter();

    @Test
    void string_1_shouldBeParsedTo_int_1(){
        String value = "1";
        var result = converter.stringToObject(value, int.class);
        Assertions.assertEquals(1, result, "Expected int 1 but was "+result);
    }

    @Test
    void string_a_shouldBeParsedTo_char_a(){
        String value = "a";
        var result = converter.stringToObject(value, char.class);
        Assertions.assertEquals('a', result, "Expected char 'a' but was "+result);
    }

    @Test
    void bytesShouldBeParsedToStringBase64withoutPadding(){
        String expected = "YWJj";
        byte[] bytes = "abc".getBytes(StandardCharsets.UTF_8);
        String result = converter.objectToString(bytes);
        Assertions.assertEquals(expected, result, "Expected string: "+expected+" but was " + result);
    }

    @Test
    void stringBase64withoutPaddingShouldBeConvertedToBytes(){
        String expectedBase64 = "YWJj";
        byte[] expectedBytes = Base64.getDecoder().decode(expectedBase64);
        var actual = converter.stringToObject(expectedBase64,byte[].class);
        Assertions.assertEquals(expectedBytes.length,actual.length,"bytes array are different length");
        for(int i=0; i<actual.length; i++){
            Assertions.assertEquals(expectedBytes[i],actual[i],
                    String.format("expected = %d but was %d", expectedBytes[i],actual[i]));
        }
    }

    @Test
    void stringBase64withPaddingShouldBeConvertedToBytes(){
        String expectedBase64 = "YWJjYQ==";
        byte[] expectedBytes = Base64.getDecoder().decode(expectedBase64);
        var actual = converter.stringToObject(expectedBase64,byte[].class);
        Assertions.assertEquals(expectedBytes.length,actual.length,"bytes array are different length");
        for(int i=0; i<actual.length; i++){
            Assertions.assertEquals(expectedBytes[i],actual[i],
                    String.format("expected = %d but was %d", expectedBytes[i],actual[i]));
        }
    }
    @Test
    void bytesShouldBeParsedToStringBase64withPadding(){
        String expected = "YWJjYQ==";
        byte[] bytes = "abca".getBytes(StandardCharsets.UTF_8);
        String actual = converter.objectToString(bytes);
        Assertions.assertEquals(expected, actual, "Expected string: "+expected+" but was " + actual);
    }

    @Test
    void bytesBase64ToStringShouldBeEqual(){
        byte[] bytes = new byte[]{1,2,3,4};
        String expected = Base64.getEncoder().encodeToString(bytes);
        String actual = converter.objectToString(bytes);
        Assertions.assertEquals(expected,actual,
                String.format("Base 64 does not match. Expected: '%s' but was '%s' ",expected,actual)
        );
    }

}