package io.github.ysdaeth.jmodularcrypt.common.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;


class McfConverterHexTest {
    private ConversionRegistry registry = new McfConverterHex();

    @Test
    void convert_shouldReturnNonBlankString(){
        byte[] bytes = new byte[]{1,2,3,4,5,6};
        String hex = registry.convert(bytes,String.class);
        Assertions.assertNotNull(hex);
        Assertions.assertFalse(hex.isBlank(),"Hex format after conversion is blank");
    }

    @Test
    void convert_shouldReturnPrimitiveBytesFromHex(){
        byte[] bytes = new byte[]{12,43,22,13,98};
        HexFormat hexFormat = HexFormat.of();
        String expected = hexFormat.formatHex(bytes);
        String actual = registry.convert(bytes,String.class);
        Assertions.assertEquals(expected, actual,
                String.format("Expected is '%s' but was '%s' ",expected, actual));
    }

    @Test
    void convert_shouldReturnObjectBytesFromHex(){
        Byte[] bytes = new Byte[]{12,43,22,13,98};
        HexFormat hexFormat = HexFormat.of();
        String expected = hexFormat.formatHex( byteToObjectBytes(bytes) );
        String actual = registry.convert(bytes,String.class);
        Assertions.assertEquals(expected, actual,
                String.format("Expected is '%s' but was '%s' ",expected, actual));
    }

    private static byte[] byteToObjectBytes(Byte[] bytes){
        byte[] result = new byte[bytes.length];
        for(int i=0; i<bytes.length; i++){
            result[i] = bytes[i];
        }
        return result;
    }

}