package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

import io.github.ysdaeth.jmodularcrypt.core.converter.BasicConverter;
import io.github.ysdaeth.jmodularcrypt.core.converter.Converter;
import io.github.ysdaeth.jmodularcrypt.core.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.core.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.core.serializer.ConfigurableSerializer;
import io.github.ysdaeth.jmodularcrypt.core.serializer.SerializerConfig;

final class McfSerializer extends ConfigurableSerializer {

    public McfSerializer() {
        super(new McfConfig());
    }

    /**
     * Serializer configuration class for Modular Crypt output format
     */
    private static class McfConfig implements SerializerConfig{

        @Override
        public Converter typeConverter() {
            return new BasicConverter();
        }

        @Override
        public Parser parser() {
            return new McfParser();
        }
    }
}
