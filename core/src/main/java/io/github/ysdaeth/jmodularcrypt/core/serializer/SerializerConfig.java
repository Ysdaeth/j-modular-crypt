package io.github.ysdaeth.jmodularcrypt.core.serializer;

import io.github.ysdaeth.jmodularcrypt.core.converter.Converter;
import io.github.ysdaeth.jmodularcrypt.core.parser.Parser;

/**
 * Reusable configuration for {@link ConfigurableSerializer}
 */
public interface SerializerConfig {

    /**
     * Type converter converts string to types and types to strings
     * @return Implementation of type converter
     */
    Converter typeConverter();

    /**
     * Return parser that is meant to work with simple line formats
     * @return Simple line parser implementation
     */
    Parser parser();

}
