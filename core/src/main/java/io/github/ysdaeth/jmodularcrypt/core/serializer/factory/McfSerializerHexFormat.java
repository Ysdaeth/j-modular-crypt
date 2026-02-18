package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

import io.github.ysdaeth.jmodularcrypt.core.converter.Converter;
import io.github.ysdaeth.jmodularcrypt.core.converter.ConverterHexBytes;
import io.github.ysdaeth.jmodularcrypt.core.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.core.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.core.serializer.ConfigurableSerializer;
import io.github.ysdaeth.jmodularcrypt.core.serializer.SerializerConfig;

public class McfSerializerHexFormat extends ConfigurableSerializer {

    public McfSerializerHexFormat() {
        super(new HexConfig());
    }

    private static class HexConfig implements SerializerConfig{

        @Override
        public Converter typeConverter() {
            return new ConverterHexBytes();
        }

        @Override
        public Parser parser() {
            return new McfParser();
        }
    }
}
