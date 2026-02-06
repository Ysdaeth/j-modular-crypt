package io.github.ysdaeth.jmodularcrypt.common.serializer;

import io.github.ysdaeth.jmodularcrypt.common.parser.Section;
import io.github.ysdaeth.jmodularcrypt.common.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;

import java.util.List;
import java.util.function.Function;

/**
 * Class is responsible for storing module accessors with fields getters and setters
 * for a class that contains annotations such as {@link Module}
 * and storing serialization and deserialization function implementation provided by serializer based on the class logic.
 */
final class ClassSerializer {

    private final List<ModuleAccessor> modules;
    private final Function<Object,Section[]> serializer;
    private final Function<Section[],Object> deserializer;

    public ClassSerializer(List<ModuleAccessor> modules,
                           Function<Object, Section[]> serializer,
                           Function<Section[], Object> deserializer) {
        this.modules = modules;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Convert sections that represents substrings of serialized string to object.
     * {@link Parser}
     * @param parsed sections
     * @return
     */
    public Object deserialize(Section[] parsed) {
        if(modules.size() != parsed.length){
            throw new IllegalArgumentException("Sections length does not match modules length");
        }
        return deserializer.apply(parsed);
    }

    public Section[] serialize(Object object) {
        Section[] result = serializer.apply(object);
        if(result.length != modules.size()){
            throw new IllegalArgumentException(
                    "Deserialized fields length does not match with modules length");
        }
        return result;
    }

}
