package io.github.ysdaeth.jmodularcrypt.common.serializer;

import java.lang.invoke.MethodHandle;

final class ModuleAccessor implements Comparable<ModuleAccessor> {
    private final int order;
    private final Class<?> type;
    private final String name;
    private final MethodHandle getter;
    private final MethodHandle setter;

    public ModuleAccessor(int order, Class<?> type,String name, MethodHandle getter, MethodHandle setter){
        this.order = order;
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public int order(){
        return order;
    }
    public Class<?> type(){return type;}
    public String name(){return name;}
    public MethodHandle getter(){
        return getter;
    }
    public MethodHandle setter(){
        return setter;
    }

    @Override
    public int compareTo(ModuleAccessor o) {
        if(o.order == this.order) return 0;
        return o.order > this.order ? -1:1;
    }
}
