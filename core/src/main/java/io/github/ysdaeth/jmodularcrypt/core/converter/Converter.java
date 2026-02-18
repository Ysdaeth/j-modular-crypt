package io.github.ysdaeth.jmodularcrypt.core.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class is designed for data types conversion mechanism and caching
 * backwards and forwards converting data types functions.
 */
public abstract class Converter {

    /**
     * Map that stores functions responsible for backward and forward types conversion
     */
    private final Map<TypePair, Function<?,?>> registry = new HashMap<>();

    /**
     * Register type converter that will convert between values
     * @param from type of object with current type
     * @param to target type of object
     * @param forwardFn function that convert value from type to target type
     * @param backwardFn function that will revert conversion back to original type
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
     * <p>Example</p>
     * {@code int num = convert("123", int.class) }
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
