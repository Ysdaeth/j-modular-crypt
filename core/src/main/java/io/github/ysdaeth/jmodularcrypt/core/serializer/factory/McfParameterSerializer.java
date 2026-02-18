package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

import io.github.ysdaeth.jmodularcrypt.core.converter.BasicConverter;
import io.github.ysdaeth.jmodularcrypt.core.converter.Converter;
import io.github.ysdaeth.jmodularcrypt.core.parser.McfParametersParser;
import io.github.ysdaeth.jmodularcrypt.core.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.core.serializer.ConfigurableSerializer;
import io.github.ysdaeth.jmodularcrypt.core.serializer.SerializerConfig;

final class McfParameterSerializer extends ConfigurableSerializer {

    public McfParameterSerializer() {
        super(new ParameterSerializerConfig());
    }

    /**
     * Serializer configuration for Modular Crypt Format parameter section
     */
    private static class ParameterSerializerConfig implements SerializerConfig {
        @Override
        public Converter typeConverter() {
            return new BasicConverter();
        }

        @Override
        public Parser parser() {
            return new McfParametersParser();
        }
    }
}
