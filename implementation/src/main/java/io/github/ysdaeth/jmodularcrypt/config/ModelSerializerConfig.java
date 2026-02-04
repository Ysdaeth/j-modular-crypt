package io.github.ysdaeth.jmodularcrypt.config;


import io.github.ysdaeth.jmodularcrypt.common.converter.BasicTypeConverter;
import io.github.ysdaeth.jmodularcrypt.common.converter.TypeConverter;
import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.common.serializer.SerializerConfiguration;

public final class ModelSerializerConfig implements SerializerConfiguration {
    @Override
    public TypeConverter typeConverter() {
        return new BasicTypeConverter();
    }
    @Override
    public Parser parser() {
        return new McfParser();
    }
}
