package io.github.ysdaeth.jmodularcrypt.core.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class BasicConverterTest {

    private static final BasicConverter converter = new BasicConverter();

    @Test
    public void convert_shouldReturnStringFromString(){
        String expected = "expected";
        String actual = converter.convert(expected,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnIntFromString(){
        String num = "123";
        int actual = converter.convert(num, int.class);
        Assertions.assertEquals(123,actual);
    }
    @Test
    public void convert_shouldReturnStringFromInt(){
        int num = 123;
        String expected = "123";
        String actual = converter.convert(num,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnIntegerFromString(){
        String num = "123";
        Integer expected = 123;
        Integer actual = converter.convert(num,Integer.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void convert_shouldReturnStringFromInteger(){
        Integer num = 123;
        String expected = "123";
        String actual = converter.convert(num,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnCharacterFromString(){
        String ch = "A";
        Character expected = 'A';
        Character actual = converter.convert(ch,Character.class);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    public void convert_shouldReturnStringFromCharacter(){
        Character ch = 'A';
        String expected = "A";
        String actual = converter.convert(ch,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnCharFromString(){
        String ch = "A";
        char expected = 'A';
        char actual = converter.convert(ch,char.class);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    public void convert_shouldReturnStringFromChar(){
        char ch = 'A';
        String expected = "A";
        String actual = converter.convert(ch,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnStringFromFloat(){
        Float f = 12.3F;
        String expected = "12.3";
        String actual = converter.convert(f,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnObjectFloatFromString(){
        String fl = "12.3";
        Float expected = 12.3F;
        Float actual = converter.convert(fl,Float.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnPrimitiveFloatFromString(){
        String fl = "12.3";
        float expected = 12.3f;
        float actual = converter.convert(fl,float.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnStringFromPrimitiveFloat(){
        float fl = 12.3f;
        String expected = "12.3";
        String actual = converter.convert(fl,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnBase64_fromPrimitiveBytes(){
        byte[] bytes = new byte[]{1,2,3};
        String expected = "AQID";
        String actual = converter.convert(bytes,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnPrimitiveBytes_fromBase64(){
        String base64 = "AQID";
        byte[] expected = new byte[]{1,2,3};
        byte[] actual = converter.convert(base64,byte[].class);
        boolean matches = Arrays.equals(expected,actual);
        Assertions.assertTrue(matches,"Byte arrays does not match after base64 decoding");
    }

    @Test
    public void convert_shouldReturnBase64_fromReferenceBytes(){
        Byte[] bytes = new Byte[]{1,2,3};
        String expected = "AQID";
        String actual = converter.convert(bytes,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnReferenceBytes_fromBase64(){
        String base64 = "AQID";
        Byte[] expected = new Byte[]{1,2,3};
        Byte[] actual = converter.convert(base64,Byte[].class);
        boolean matches = Arrays.equals(expected,actual);
        Assertions.assertTrue(matches,"Byte arrays does not match after base64 decoding");
    }

    @Test
    public void convert_shouldReturnStringFromPrimitiveBoolean(){
        boolean b = true;
        String expected = "true";
        String actual = converter.convert(b,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnPrimitiveBooleanFromString(){
        String b = "false";
        boolean expected = false;
        boolean actual = converter.convert(b,boolean.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnStringFromReferenceBoolean(){
        Boolean b = true;
        String expected = "true";
        String actual = converter.convert(b,String.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void convert_shouldReturnReferenceBooleanFromString(){
        String b = "false";
        Boolean expected = false;
        Boolean actual = converter.convert(b, Boolean.class);
        Assertions.assertEquals(expected,actual);
    }

}