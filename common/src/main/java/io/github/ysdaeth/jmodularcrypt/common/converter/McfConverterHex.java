package io.github.ysdaeth.jmodularcrypt.common.converter;

import java.util.HexFormat;

public class McfConverterHex extends BaseStringRegistry{

    private static final HexFormat HEX_FORMAT = HexFormat.of();
    public McfConverterHex(){
        register(byte[].class, String.class, McfConverterHex::byteToHex,McfConverterHex::hexToBytes);
        register(Byte[].class, String.class, McfConverterHex::byteToHex,McfConverterHex::hexToObjectBytes);
    }

    private static String byteToHex(byte[] bytes){
        return HEX_FORMAT.formatHex(bytes);
    }

    private static String byteToHex(Byte[] bytes){
        byte[] bs = new byte[bytes.length];

        for(int i=0; i<bytes.length; i++)
            bs[i] = bytes[i];

        return byteToHex(bs);
    }

    private static byte[] hexToBytes(String hex){
        return HEX_FORMAT.parseHex(hex);
    }

    private static Byte[] hexToObjectBytes(String hex){
        byte[] bytes = HEX_FORMAT.parseHex(hex);
        Byte[] result = new Byte[bytes.length];

        for(int i=0; i<bytes.length; i++)
            result[i] = bytes[i];

        return result;
    }
}
