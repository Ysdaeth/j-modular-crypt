package io.github.ysdaeth.jmodularcrypt.common.converter;

import io.github.ysdaeth.jmodularcrypt.common.ConversionPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Immutable class that is designed for most common field types,
 * rather than complex structures. It is created to work with MCF most
 * common types.
 * it supports conversion of primitives and simple objects to string, and
 * string to primitives/objects.
 *
 * @deprecated use {@link ConversionRegistry}
 */
@Deprecated(forRemoval = true)
public final class BasicTypeConverter implements TypeConverter {
    public BasicTypeConverter(){}

    public String objectToString(Object object){
        Class<?> type = object.getClass();
        if(!TYPE_TO_STRING.containsKey(type)){
            throw new IllegalArgumentException("Unsupported conversion type "+type);
        }
        return TYPE_TO_STRING.get(type).apply(object);
    }

    public <T> T stringToObject(String value, Class<T> type){
        if(!STRING_TO_TYPE.containsKey(type)){
            throw new IllegalArgumentException("Unsupported conversion type: "+type);
        }
        return (T) STRING_TO_TYPE.get(type).apply(value);
    }

    private static Map<Class<?>, Function<Object,String>> TYPE_TO_STRING = new HashMap<>();
    static{
        TYPE_TO_STRING.put(String.class,Object::toString);
        TYPE_TO_STRING.put(Integer.class,Object::toString);
        TYPE_TO_STRING.put(int.class,Object::toString);
        TYPE_TO_STRING.put(Byte[].class,b-> ConversionPolicy.toBase64((Byte[]) b) );
        TYPE_TO_STRING.put(byte[].class,b-> ConversionPolicy.toBase64((byte[]) b) );
        TYPE_TO_STRING.put(Character.class,(ch)-> ""+ch);
        TYPE_TO_STRING.put(char.class,(ch)-> ""+(char)ch);
    }

    private static Map<Class<?>, Function<String,?>> STRING_TO_TYPE = new HashMap<>();
    static{
        STRING_TO_TYPE.put(String.class,Function.identity());
        STRING_TO_TYPE.put(Integer.class,Integer::valueOf);
        STRING_TO_TYPE.put(int.class,Integer::valueOf);
        STRING_TO_TYPE.put(Byte[].class, ConversionPolicy::fromBase64);
        STRING_TO_TYPE.put(byte[].class,ConversionPolicy::fromBase64);
        STRING_TO_TYPE.put(Character.class,(ch)-> ch.charAt(0));
        STRING_TO_TYPE.put(char.class,(ch)-> ch.charAt(0));
    }

}
