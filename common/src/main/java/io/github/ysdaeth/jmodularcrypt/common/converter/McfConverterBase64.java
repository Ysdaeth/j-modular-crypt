package io.github.ysdaeth.jmodularcrypt.common.converter;

import io.github.ysdaeth.jmodularcrypt.common.ConversionPolicy;

/**
 * Converter for most common Modular Crypt Format data types.
 * Encodes bytes to base64 format
 */
public class McfConverterBase64 extends BaseStringConverter {

    /**
     * Create converter for Modular Crypt Format data types.
     * Bytes are encoded to base64 format
     */
    public McfConverterBase64(){
        register(byte[].class,String.class, ConversionPolicy::toBase64, ConversionPolicy::fromBase64);
        register(Byte[].class, String.class, ConversionPolicy::toBase64, McfConverterBase64::fromBase64);
    }

    private static Byte[] fromBase64(String base64){
        byte[] bytes = ConversionPolicy.fromBase64(base64);
        Byte[] result = new Byte[bytes.length];
        for(int i=0; i< bytes.length; i++){
            result[i] = bytes[i];
        }
        return result;
    }
}
