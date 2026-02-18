package io.github.ysdaeth.jmodularcrypt.core.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ConverterBytesToHexTest {

    private static final ConverterHexBytes converter = new ConverterHexBytes();

    @Test
    public void convert_shouldReturnHexFromReferenceBytes(){
        Byte[] bytes = new Byte[]{1,2,3};
        String expected = "010203";
        String actual =converter.convert(bytes,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnPrimitiveBytesFromHexFormat(){
        String hex = "010203";
        byte[] expected = new byte[]{1,2,3};
        byte[] actual = converter.convert(hex,byte[].class);
        boolean matches = Arrays.equals(expected,actual);
        Assertions.assertTrue(matches,"byte array does not match after hex decoding");
    }

    @Test
    public void convert_shouldReturnHexFromPrimitiveBytes(){
        byte[] bytes = new byte[]{1,2,3};
        String expected = "010203";
        String actual =converter.convert(bytes,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnObjectBytesFromHexFormat(){
        String hex = "010203";
        Byte[] expected = new Byte[]{1,2,3};
        Byte[] actual = converter.convert(hex, Byte[].class);
        boolean matches = Arrays.equals(expected,actual);
        Assertions.assertTrue(matches,"byte array does not match after hex decoding");
    }
}