package io.github.ysdaeth.jmodularcrypt.common.serializer;

/**
 * Interface for serializer that convert objects to string value and
 * from string value to objects
 */
public interface Serializer {
    String serialize(Object mcfObject);
    <T> T deserialize(String serialized, Class<T> mcfClass);
}
