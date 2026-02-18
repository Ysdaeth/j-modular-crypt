package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

import io.github.ysdaeth.jmodularcrypt.core.serializer.Serializer;

/**
 * Factory provides configured serializer implementation based on specified
 * type {@link SerializerType}
 * <ul>
 *     <li>{@link SerializerType#MCF_BASE64} serializes structures to Modular Crypt Format - bytes to base64</li>
 *     <li>{@link SerializerType#MCF_HEXADECIMAL} serializes structures to Modular Crypt Format - bytes to hex format</li>
 *     <li>{@link SerializerType#MCF_PARAMETER} serializes structures to parameter Modular Crypt Format - bytes base64</li>
 * </ul>
 */
public final class SerializerFactory {

    private SerializerFactory(){}

    /**
     * Create configured instance of {@link Serializer} based on provided type
     * <ul>
     *     <li>{@link SerializerType#MCF_BASE64} serializes structures to Modular Crypt Format - bytes to base64</li>
     *     <li>{@link SerializerType#MCF_HEXADECIMAL} serializes structures to Modular Crypt Format - bytes to hex format</li>
     *     <li>{@link SerializerType#MCF_PARAMETER} serializes structures to parameter Modular Crypt Format - bytes base64</li>
     * </ul>
     * @param type type of serializer
     * @return serializer
     */
    public static Serializer getInstance(SerializerType type){
        return switch(type){
            case MCF_BASE64 -> new McfSerializer();
            case MCF_HEXADECIMAL -> new McfSerializerHexFormat();
            case MCF_PARAMETER -> new McfParameterSerializer();
        };
    }

}
