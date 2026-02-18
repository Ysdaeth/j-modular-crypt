package io.github.ysdaeth.jmodularcrypt.core.converter;

import java.util.Objects;

/**
 * <h2>Basic Converter</h2>
 * <h3>Simple data types conversion</h3>
 * Class that is designed for most common field types,
 * rather than complex structures. It is created to convert Strings to basic types
 * it supports conversion of primitives and simple objects to string, and
 * String to primitives and basic reference objects.
 * <h3>Supported types</h3>
 * <ul>
 *     <li>String</li>
 *     <li>Integer, int</li>
 *     <li>Character, char</li>
 *     <li>Float, float</li>
 *     <li>Byte[], byte[]</li>
 *     <li>Boolean, boolean</li>
 * </ul>
 */
public class BasicConverter extends Converter {

    public BasicConverter(){
        register(String.class,String.class, Objects::toString, Object::toString);
        register(Integer.class,String.class, Object::toString, Integer::valueOf);
        register(int.class,String.class, Object::toString, Integer::valueOf);
        register(Character.class,String.class, Objects::toString, ch->ch.charAt(0));
        register(char.class,String.class, Objects::toString, ch->ch.charAt(0));
        register(Float.class,String.class, Objects::toString, Float::parseFloat);
        register(float.class,String.class, Objects::toString, Float::parseFloat);
        register(byte[].class,String.class, ConverterUtils::bytesToBase64, ConverterUtils::base64ToBytes);
        register(Byte[].class,String.class, BasicConverter::bytesToBase64, BasicConverter::base64ToBytes);
        register(boolean.class,String.class, Objects::toString,s-> s.equals("true"));
        register(Boolean.class,String.class, Objects::toString,s-> s.equals("true"));
    }

    /**
     * Convert base64 encoded bytes to object bytes array
     * @param base64 base64 encoded bytes
     * @return byte array
     */
    private static Byte[] base64ToBytes(String base64){
        byte[] bytes = ConverterUtils.base64ToBytes(base64);
        return ConverterUtils.toObjectBytes(bytes);
    }

    /**
     * Convert object bytes array to base64 format
     * @param bytes bytes array
     * @return base64 encoded bytes
     */
    private static String bytesToBase64(Byte[] bytes){
        byte[] primitiveBytes = ConverterUtils.toPrimitiveBytes(bytes);
        return ConverterUtils.bytesToBase64(primitiveBytes);
    }
}
