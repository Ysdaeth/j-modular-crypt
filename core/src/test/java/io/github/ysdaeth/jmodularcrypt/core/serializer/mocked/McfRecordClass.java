package io.github.ysdaeth.jmodularcrypt.core.serializer.mocked;

import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;

public record McfRecordClass(
        @Module(order = 0) String name,
        @Module(order = 1) byte[] bytes) implements McfClass {

    @SerializerCreator
    public McfRecordClass{}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
