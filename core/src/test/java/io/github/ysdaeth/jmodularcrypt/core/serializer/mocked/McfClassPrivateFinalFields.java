package io.github.ysdaeth.jmodularcrypt.core.serializer.mocked;

import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;

public class McfClassPrivateFinalFields implements McfClass{

    @Module(order = 0)
    private final String name;

    @Module(order = 1)
    private final byte[] bytes;

    @SerializerCreator
    public McfClassPrivateFinalFields(String name, byte[] bytes){
        this.bytes = bytes;
        this.name = name;
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
