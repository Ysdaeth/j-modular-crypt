package io.github.ysdaeth.jmodularcrypt.common.serializer;

import io.github.ysdaeth.jmodularcrypt.common.converter.ConversionRegistry;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;

/**
 * Reusable configuration for {@link ConfigurableSerializer}
 */
public abstract class SerializerConfig {

    /**
     * Type converter converts string to types and types to strings
     * @return Implementation of type converter
     */
    public abstract ConversionRegistry typeConverter();

    /**
     * Return parser that is meant to work with simple line formats
     * @return Simple line parser implementation
     */
    public abstract Parser parser();

}
