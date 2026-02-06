package io.github.ysdaeth.jmodularcrypt.config;


import io.github.ysdaeth.jmodularcrypt.common.converter.ConversionRegistry;
import io.github.ysdaeth.jmodularcrypt.common.converter.McfConverterBase64;
import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.common.serializer.SerializerConfig;

/**
 * Serializer configuration that provides basic type converter which
 * in which bytes are encoded to base64 format. Configuration is for
 * {@link io.github.ysdaeth.jmodularcrypt.common.serializer.ConfigurableSerializer}
 */
public final class McfModelBase64 extends SerializerConfig {

    /**
     * Converter that converts bytes to base 64 format
     * @return Serializer configuration
     */
    @Override
    public ConversionRegistry typeConverter() {
        return new McfConverterBase64();
    }

    /**
     * Returns parser that parses standard Modular Crypt Format sections
     * separated with '$' signs
     * @return parser for standard sections
     */
    @Override
    public Parser parser() {
        return new McfParser();
    }
}
