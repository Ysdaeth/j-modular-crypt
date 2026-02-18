package io.github.ysdaeth.jmodularcrypt.core.converter;

/**
 * Converter for most common Modular Crypt Format data types.
 * Encodes bytes to hexadecimal format
 */
public class ConverterHexBytes extends BasicConverter {


    /**
     * Create converter for Modular Crypt Format data types.
     * Bytes are encoded to hexadecimal format
     */
    public ConverterHexBytes(){
        register(byte[].class, String.class, ConverterUtils::bytesToHexFormat, ConverterUtils::hexFormatToBytes);
        register(Byte[].class, String.class, ConverterHexBytes::objectBytesToHex, ConverterHexBytes::hexToObjectBytes);
    }

    /**
     * Convert reference object bytes array to hexadecimal format
     * @param bytes bytes array
     * @return hexadecimal format of specified bytes
     */
    private static String objectBytesToHex(Byte[] bytes){
        byte[] bs = ConverterUtils.toPrimitiveBytes(bytes);
        return ConverterUtils.bytesToHexFormat(bs);
    }

    /**
     * Convert hex format to object bytes array
     * @param hexFormat hexadecimal bytes format
     * @return bytes array
     */
    private static Byte[] hexToObjectBytes(String hexFormat){
        byte[] bytes = ConverterUtils.hexFormatToBytes(hexFormat);
        return ConverterUtils.toObjectBytes(bytes);
    }
}
