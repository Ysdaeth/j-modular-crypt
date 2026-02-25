package io.github.ysdaeth.jmodularcrypt.core.parser;

/**
 * Object representation of key value pair.
 * This is similar to {@link java.util.Map#entry(Object, Object)}, but for text
 * format that contains strings.
 * @param key field name {@link java.lang.reflect.Field}
 * @param value string representation of the fields value
 */
public record Section(String key, String value){

}
