package io.github.ysdaeth.jmodularcrypt.common.serializer;

import io.github.ysdaeth.jmodularcrypt.common.converter.TypeConverter;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;

/**
 * Reusable configuration for {@link ConfigurableSerializer}
 */
public interface SerializerConfiguration {
    /**
     * Type converter converts string to types and types to strings
     * @return Implementation of type converter
     */
    TypeConverter typeConverter();

    /**
     * Return parser that is meant to work with simple line formats
     * @return Simple line parser implementation
     */
    Parser parser();
}
