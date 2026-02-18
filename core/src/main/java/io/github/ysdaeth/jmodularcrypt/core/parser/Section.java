package io.github.ysdaeth.jmodularcrypt.core.parser;

/**
 * Object representation of key value pair.
 * This is similar to {@link java.util.Map} entry, but for text
 * format that contains strings.
 * @param key field name {@link java.lang.reflect.Field}
 * @param value string value representation
 */
public record Section(String key, String value){

}
