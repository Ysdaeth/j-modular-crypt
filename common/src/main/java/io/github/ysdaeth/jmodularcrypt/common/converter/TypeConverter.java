package io.github.ysdaeth.jmodularcrypt.common.converter;

/**
 * It is recommended that implementation of this interface be able to dynamically
 * select implementation for different data types, IE. use map where key is a class, and value is cast: (T)object
 */
public interface TypeConverter {
    /**
     * This method will return string value of specified object
     * @param object object that will be converted to string value
     * @return string representation of the object
     */
    String objectToString(Object object);

    /**
     * Convert string to object of specified type
     * @param value value that is converted to object
     * @param type class type of returned object
     * @return object of specified type
     * @param <T> type
     */
    <T> T stringToObject(String value, Class<T> type);
}
