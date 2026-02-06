package io.github.ysdaeth.jmodularcrypt.config;

import io.github.ysdaeth.jmodularcrypt.common.converter.ConversionRegistry;
import io.github.ysdaeth.jmodularcrypt.common.converter.McfConverterHex;
import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.common.serializer.SerializerConfig;

/**
 * Serializer configuration that provides basic type converter which
 * in which bytes are encoded to Hex format. Configuration is for
 * {@link io.github.ysdaeth.jmodularcrypt.common.serializer.ConfigurableSerializer}
 */
public class McfModelHex extends SerializerConfig {

    /**
     * Converter that converts bytes to hex format
     * @return Serializer configuration
     */
    @Override
    public ConversionRegistry typeConverter() {
        return new McfConverterHex();
    }

    /**
     * Returns parser that parses parameter Modular Crypt Format sections
     * separated with ',' parameter is section between '$' signs
     * @return parser for standard sections
     */
    @Override
    public Parser parser() {
        return new McfParser();
    }
}
