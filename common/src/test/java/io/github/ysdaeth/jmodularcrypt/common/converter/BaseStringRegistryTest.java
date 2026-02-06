package io.github.ysdaeth.jmodularcrypt.common.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseStringRegistryTest {
    ConversionRegistry registry = new BaseStringRegistry();

    @Test
    void convert_shouldReturnIntegerFromString(){
        String value = "123";
        Integer result = registry.convert(value, Integer.class);
        assertEquals(123, result);
    }

    @Test
    void convert_shouldReturnStringFromInteger(){
        Integer value = 123;
        String result = registry.convert(value,String.class);
        assertEquals("123",result);
    }

    @Test
    void convert_shouldReturnStringFromInt(){
        int value = 123;
        String result = registry.convert(value,String.class);
        assertEquals("123",result);
    }

    @Test
    void convert_shouldReturnIntFromString(){
        String value = "123";
        int result = registry.convert(value,int.class);
        assertEquals(123,result);
    }

    <T> void assertEquals(T expected, T actual){
        Assertions.assertEquals(expected, actual,
                "Expected is "+ expected+ " but was " + actual);
    }

}