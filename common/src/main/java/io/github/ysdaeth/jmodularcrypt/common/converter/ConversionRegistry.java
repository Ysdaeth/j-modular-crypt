package io.github.ysdaeth.jmodularcrypt.common.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class that is designed for most common field types,
 * rather than complex structures. It is created to work with MCF most
 * common types.
 * it supports conversion of primitives and simple objects to string, and
 * string to primitives/objects.
 * <h2>Supported types</h2>
 * <ul>
 *     <li>String</li>
 *     <li>Integer</li>
 *     <li>int</li>
 *     <li>Character</li>
 *     <li>char</li>
 * </ul>
 */
public abstract class ConversionRegistry {

    private Map<TypePair, Function<?,?>> registry = new HashMap<>();

    /**
     * Register type converter that will convert between values
     * @param from type of object
     * @param to target type of object
     * @param forwardFn function that convert value from type to target type
     * @param backwardFn function that will revert conversion
     * @param <T> Source type
     * @param <R> Target type
     */
    protected <T,R> void register(
            Class<T> from, Class<R> to,
            Function<T,R> forwardFn, Function<R,T> backwardFn){

        registry.put(new TypePair(from,to),forwardFn);
        registry.put(new TypePair(to,from), backwardFn);
    }

    /**
     * Convert specified value to target value using registered converter
     * @param value value to be converted
     * @param target target type that value is converted
     * @return converted value to specified type
     * @param <T> Current type
     * @param <R> target Type
     */
    @SuppressWarnings("unchecked")
    public <T,R> R convert(T value, Class<R> target){
        var forwardFn = registry.get(new TypePair(value.getClass(),target));
        if(forwardFn == null){
            String variant = value.getClass() + " <--> " + target;
            throw new RuntimeException("There is no registered type converter for "+ variant);
        }
        Function<T,R> fn = (Function<T,R>) forwardFn;

        return fn.apply(value);
    }

}
