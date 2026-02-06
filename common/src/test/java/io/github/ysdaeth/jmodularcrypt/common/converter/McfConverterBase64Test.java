package io.github.ysdaeth.jmodularcrypt.common.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;


class McfConverterBase64Test {
    private final ConversionRegistry registry = new McfConverterBase64();

    @Test
    void convert_shouldReturnBase64_withoutPadding(){
        String expected = "YWJj";
        byte[] bytes = "abc".getBytes(StandardCharsets.UTF_8);
        String result = registry.convert(bytes,String.class);
        Assertions.assertEquals(expected, result, "Expected is: "+expected+" but was " + result);
    }

    @Test
    void convert_shouldReturnBase64_withPadding(){
        String expected = "YWJjYQ==";
        byte[] bytes = "abca".getBytes(StandardCharsets.UTF_8);
        String actual = registry.convert(bytes,String.class);
        Assertions.assertEquals(expected, actual, "Expected is: "+expected+" but was " + actual);
    }

    @Test
    void convert_shouldReturnBytesFromBase64(){
        byte[] expected = new byte[]{1,2,3,4,5,6};
        String base64 = "AQIDBAUG";
        byte[] actual = registry.convert(base64,byte[].class);

        Assertions.assertEquals(expected.length,actual.length,"bytes array are different length");
        for(int i=0; i<actual.length; i++){
            Assertions.assertEquals(expected[i],actual[i],
                    String.format("Expected byte is %d but was %d", expected[i],actual[i]));
        }

    }


}