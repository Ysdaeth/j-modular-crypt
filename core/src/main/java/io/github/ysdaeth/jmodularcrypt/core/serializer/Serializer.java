package io.github.ysdaeth.jmodularcrypt.core.serializer;

/**
 * Interface for serializer that convert objects to string value and
 * from string value to objects
 */
public interface Serializer {

    /**
     * Serialize object to string.
     * @param mcfObject object that fields will be converted to string
     * @return serialized object
     */
    String serialize(Object mcfObject);

    /**
     * Deserialize string to object of specified class
     * @param serialized result that was returned with {@link Serializer#serialize(Object)} method
     * @param mcfClass class of object
     * @return Object of specified type
     * @param <T> Type of object to return
     */
    <T> T deserialize(String serialized, Class<T> mcfClass);
}
