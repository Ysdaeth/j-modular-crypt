package io.github.ysdaeth.jmodularcrypt.core.converter;

import java.util.Base64;
import java.util.HexFormat;

final class ConverterUtils {
    private static final HexFormat hexFormat = HexFormat.of();

    /**
     * Cast primitive bytes array to object byte array
     * @param bytes primitive byte array
     * @return object bytes
     */
    static Byte[] toObjectBytes(byte[] bytes){
        Byte[] result = new Byte[bytes.length];
        for(int i=0; i< bytes.length; i++){
            result[i] = bytes[i];
        }
        return result;
    }

    /**
     * Cast object bytes array to primitive byte array
     * @param bytes object byte array
     * @return primitive byte array
     */
    static byte[] toPrimitiveBytes(Byte[] bytes){
        byte[] result = new byte[bytes.length];
        for(int i=0; i< bytes.length; i++){
            result[i] = bytes[i];
        }
        return result;
    }

    /**
     * Make base64 bytes representation format from bytes array
     * @param bytes bytes array
     * @return base64 format of byte array
     */
    static String bytesToBase64(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Make bytes out of base64 format
     * @param base64 base64 encoded bytes
     * @return bytes from base64 format
     */
    static byte[] base64ToBytes(String base64){
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Convert bytes array to hex format
     * @param bytes bytes array
     * @return Hexadecimal format of byte array
     */
    static String bytesToHexFormat(byte[] bytes){
        return hexFormat.formatHex(bytes);
    }

    /**
     * Convert hexadecimal format to byte array
     * @param hexadecimal hexadecimal format
     * @return byte array from hexadecimal format
     */
    static byte[] hexFormatToBytes(String hexadecimal){
        return hexFormat.parseHex(hexadecimal);
    }
}
