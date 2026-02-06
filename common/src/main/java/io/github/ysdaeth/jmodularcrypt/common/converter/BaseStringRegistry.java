package io.github.ysdaeth.jmodularcrypt.common.converter;

import java.util.function.Function;


/**
 * Class that is designed for most common field types,
 * rather than complex structures. It is created to convert Strings to basic types
 * it supports conversion of primitives and simple objects to string, and
 * String to primitives and basic objects.
 * <h2>Supported types</h2>
 * <ul>
 *     <li>String</li>
 *     <li>Integer</li>
 *     <li>int</li>
 *     <li>Character</li>
 *     <li>char</li>
 * </ul>
 */
public class BaseStringRegistry extends ConversionRegistry {

    public BaseStringRegistry(){
        register(String.class,String.class, Function.identity(), Object::toString);
        register(Integer.class,String.class, Object::toString, Integer::valueOf);
        register(int.class,String.class, Object::toString, Integer::valueOf);
        register(Character.class,String.class,(c)->""+c, (ch)->ch.charAt(0));
    }
}
