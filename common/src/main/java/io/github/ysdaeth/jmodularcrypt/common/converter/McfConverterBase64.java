package io.github.ysdaeth.jmodularcrypt.common.converter;

import io.github.ysdaeth.jmodularcrypt.common.ConversionPolicy;

public class McfConverterBase64 extends BaseStringRegistry{

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
