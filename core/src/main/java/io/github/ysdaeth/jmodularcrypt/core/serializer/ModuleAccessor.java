package io.github.ysdaeth.jmodularcrypt.core.serializer;

import java.lang.invoke.MethodHandle;

/**
 * Class is accessor for class field. It is responsible for storing metadata
 * of specified {@link java.lang.reflect.Field} such as:
 * <ul>
 *     <li>Field class type</li>
 *     <li>Field name</li>
 *     <li>field getter</li>
 *     <li>field setter</li>
 *     <li>Field order</li>
 * </ul>
 * Because java reflections does not guarantee fields order in any particular order,
 * it contains field order metadata
 */
final class ModuleAccessor implements Comparable<ModuleAccessor> {
    private final int order;
    private final Class<?> type;
    private final String name;
    private final MethodHandle getter;
    private final MethodHandle setter;

    /**
     * Create module accessor
     * @param order order of the field (i.e: in source code, or any other order)
     * @param type Type of the field like Integer, String, boolean, etc.
     * @param name Field name
     * @param getter Field value setter
     * @param setter Field value getter
     */
    public ModuleAccessor(int order, Class<?> type, String name, MethodHandle getter, MethodHandle setter){
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

    /**
     * Comparator that compares other module accessors by its order.
     * If order of this order is lesser -1 is returned, equal 0, higher 1.
     * @param o the object to be compared.
     * @return -1, 0, 1 when this order is lesser, equal, higher.
     */
    @Override
    public int compareTo(ModuleAccessor o) {
        if(o.order == this.order) return 0;
        return o.order > this.order ? -1:1;
    }
}
