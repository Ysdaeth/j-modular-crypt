package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

/**
 * Serializer types, that serializes structure to specified format
 * <ul>
 *     <li>{@link SerializerType#MCF_BASE64} serializes structures to Modular Crypt Format - bytes to base64</li>
 *     <li>{@link SerializerType#MCF_HEXADECIMAL} serializes structures to Modular Crypt Format - bytes to hex format</li>
 *     <li>{@link SerializerType#MCF_PARAMETER} serializes structures to parameter Modular Crypt Format - bytes base64</li>
 * </ul>
 */
public enum SerializerType {
    MCF_BASE64, MCF_HEXADECIMAL, MCF_PARAMETER
}
