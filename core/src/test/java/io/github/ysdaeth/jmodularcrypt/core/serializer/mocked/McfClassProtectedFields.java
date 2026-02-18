package io.github.ysdaeth.jmodularcrypt.core.serializer.mocked;

import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;

public class McfClassProtectedFields implements McfClass{

    @Module(order = 0)
    String name;

    @Module(order = 1)
    byte[] bytes;

    @SerializerCreator
    public McfClassProtectedFields(String name, byte[] bytes){
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
