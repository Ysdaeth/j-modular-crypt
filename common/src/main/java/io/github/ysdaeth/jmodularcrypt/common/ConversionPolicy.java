package io.github.ysdaeth.jmodularcrypt.common;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Policy that manages objects/values conversions, by default uses {@link StandardCharsets} UTF-8
 */
public final class ConversionPolicy {

    /**
     * Converts given text to bytes with {@link StandardCharsets#UTF_8}
     * @param sequence sequence that will bytes will be gotten
     * @return bytes from sequence StandardCharset.UTF_8
     */
    public static byte[] textToBytes(CharSequence sequence){
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(sequence));
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    /**
     * Converts bytes to string with {@link StandardCharsets#UTF_8}
     * @param bytes bytes to be converted into string
     * @return string from bytes
     */
    public static String textFromBytes(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Convert bytes to string representation of Base 64 encoding
     * @param bytes bytes to be converted
     * @return base 64 bytes encoding
     */
    public static String toBase64(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Convert bytes to string representation of Base 64 encoding
     * @param bytes bytes to be converted
     * @return base 64 bytes encoding
     */
    public static String toBase64(Byte[] bytes){
        byte[] b = toPrimitive(bytes);
        return toBase64(b);
    }

    /**
     * Convert string of base64 encoded to byte array
     * @param base64 bytes encoded with base 64
     * @return decoded bytes
     */
    public static byte[] fromBase64(String base64){
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Convert object Byte array to primitive byte array
     * @param bytes object wrapper of primitive byte
     * @return primitive bytes array
     */
    private static byte[] toPrimitive(Byte[] bytes){
        byte[] primitives = new byte[bytes.length];
        for(int i=0; i<bytes.length; i++){
            primitives[i] = bytes[i];
        }
        return primitives;
    }

}
