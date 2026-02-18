package io.github.ysdaeth.jmodularcrypt.core.serializer;

import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.converter.Converter;
import io.github.ysdaeth.jmodularcrypt.core.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.core.parser.Section;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class is thread safe configurable serializer / deserializer.
 * Class purpose is to provide flexible functionality and creating own parsing output rules.
 * This class converts between serialized {@link String} representations and Java objects
 * which fields are annotated with annotations such as:
 * {@link Module}.
 * Class can use {@link SerializerConfig} which provides {@link Parser} and {@link Converter}
 * for parsing strings and converting them to values.
 * During the first encounter of given class, the parser uses reflection to build immutable metadata
 * for modules and entity class, along with assigned implementation of serializer and
 * deserializer for each module
 * <h2>Cache:</h2>
 * <ol>
 *     <li>Unreflected field getters and setters. {@link MethodHandle}</li>
 *     <li>Modules order</li>
 *     <li>Modules data type</li>
 *     <li>Modules field name</li>
 *     <li>Serializer implementation</li>
 *     <li>Deserializer implementation</li>
 * </ol>
 * Implementation of serializer and deserializer depends on {@link SerializerCreator} presence
 * <h3>Terminology</h3>
 * <ul>
 *     <li><b>Module</b> - is a field wrapper with metadata, getter and setter for a class object</li>
 * </ul>
 * When using annotations there are several ways of serialization / deserialization.
 * <b>No args constructor</b>
 * <ol>
 *     <li>Public no args constructor</li>
 *     <li>Fields annotated with {@link Module}</li>
 *     <li>Field types supported by the {@link Converter} </li>
 * </ol>
 * <b> constructor annotated with {@link SerializerCreator} </b>
 * <ol>
 *     <li>Constructor annotated with {@link SerializerCreator} </li>
 *     <li>Public constructor with parameters in the same order as {@link Module#order()}</li>
 *     <li>Fields annotated with {@link Module} </li>
 *     <li>Field types supported by the provided {@link Converter}</li>
 * </ol>
 */
public class ConfigurableSerializer implements Serializer {
    private static final Map<Class<?>, ClassSerializer> CACHE = new ConcurrentHashMap<>();
    private final Converter typeConverter;
    private final Parser parser;

    public ConfigurableSerializer(SerializerConfig configuration){
        typeConverter = configuration.typeConverter();
        parser = configuration.parser();
    }

    /**
     * Serialize provided object to string value. Object class fields must
     * contain {@link Module} annotations with order that index starts from 0
     * {@link Parser} is used to create string value
     * @param mcfObject object to serialize
     * @return Object of specified type
     */
    public String serialize(Object mcfObject){
        Section[] sections = CACHE.computeIfAbsent(
                mcfObject.getClass(), this::createClassSerializer
        ).serialize(mcfObject);
        return parser.compose(sections);
    }

    /**
     * Deserialize provided string MCF value to specified class instance.
     * Class must contain {@link Module} annotations, and if fields are final,
     * then all args constructor should be annotated with {@link SerializerCreator} and must contain
     * parameters in the same order as {@link Module#order()}. Order index must start with 0
     * Constructors must be public. If there is only no args constructor, then fields cannot be final.
     * {@link Parser} is used to create MCF string value
     * @param serialized MCF string value
     * @param mcfClass class that matches serialized string representation
     * @return Object of specified type
     * @param <T> return type
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String serialized, Class<T> mcfClass){
        Section[] sections = parser.parse(serialized);
        return (T)CACHE.computeIfAbsent(
                mcfClass, this::createClassSerializer
        ).deserialize(sections);
    }


    /**
     * Check if provided constructor is annotated with {@link SerializerCreator}
     * and return deserializer implementation which uses that constructor to assign values to the fields.
     * When no such annotation is found, then implementation of deserializer will use field setters
     * instead constructor for assigning values to the fields.
     * @param type Class that will be reflected and scanned for annotations.
     * @return Class serializer with serialization implementation based on the available constructor
     * @param <T> Class type
     */
    private <T> ClassSerializer createClassSerializer(Class<T> type){
        Constructor<T> constructor = resolveConstructor(type);
        Function<Section[],Object> deserializer;
        List<ModuleAccessor> modules = createModuleAccessors(type);

        if(constructor.isAnnotationPresent(SerializerCreator.class) ){
            deserializer = createConstructorDeserializer(modules,typeConverter,constructor);
        }else{
            deserializer = createFieldsDeserializer(modules,typeConverter,constructor);
        }

        Function<Object,Section[]> serializer = createClassSerializer(modules, typeConverter);
        return new ClassSerializer(modules,serializer,deserializer);
    }

    /**
     * Return public constructor annotated with {@link SerializerCreator} or public no args constructor.
     * if no constructor is available, then throw runtime exception
     * @param mcfClass class that constructor will be scanned with Java reflections
     * @return constructor of specified class
     * @param <T> class type of the constructor
     */
    private static <T> Constructor<T> resolveConstructor(Class<T> mcfClass){
        Constructor<T> constructor = getArgsConstructor(mcfClass)
                .orElseGet(
                        ()-> getNoArgsConstructor(mcfClass).orElse(null)
                );
        if(constructor == null){
            throw new IllegalArgumentException(
                    "Annotated args constructor, or no args constructor must be declared in the class: " + mcfClass);
        }
        try{
            constructor.setAccessible(true);
        }catch (InaccessibleObjectException e){
            throw new RuntimeException(
                    "Constructor must be public in the class: " + mcfClass, e);
        }

        return constructor;
    }

    /**
     * Get constructor annotated with {@link SerializerCreator} by using java reflections.
     * if there is no such constructor return null, or if more than one constructor uses this annotation,
     * then throw illegal argument Exception.
     * Constructor parameters order must match with {@link Module#order()} value.
     * @param tClass class that constructor will be found with reflection
     * @return All args {@code constructor} or {@code null}.
     * @param <T> type of the class constructor
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<Constructor<T>> getArgsConstructor(Class<T> tClass){
        Constructor<?>[] constructors =  Arrays.stream(tClass.getConstructors()).
                filter(c->c.isAnnotationPresent(SerializerCreator.class))
                .toArray(Constructor<?>[]::new);

        if(constructors.length == 0) return Optional.empty();
        if(constructors.length > 1 ) throw new IllegalArgumentException(
                "There is more than one " + SerializerCreator.class + " annotated constructor in class: "+ tClass);
        Constructor<T> constructor = (Constructor<T> )constructors[0];
        return Optional.of(constructor);
    }

    /**
     * Get public no args constructor, or null if not available
     * @param tClass class type that constructor will be reflected
     * @return constructor of specified class
     * @param <T> type of constructor class
     */
    private static <T> Optional<Constructor<T>> getNoArgsConstructor(Class<T> tClass) {
        try{
            Constructor<T> constructor = tClass.getConstructor();
            return Optional.of(constructor);
        }catch (NoSuchMethodException e){
            return Optional.empty();
        }
    }

    /**
     * Create class serializer that calls {@link ModuleAccessor} and invoke
     * get() method on object field. {@link Converter} is used to convert fields values
     * to string representation, and return sections as a key value pairs where
     * key is a field name and value as string representation.
     * @param accessors class field accessors
     * @return implementation of serializer for specified class of the object
     * @param <T> instance parameter type
     */
    private static <T> Function<T, Section[]> createClassSerializer(
            List<ModuleAccessor> accessors, Converter converter){

        return (obj)->{
            Section[] sections = new Section[accessors.size()];
            try{
                for(ModuleAccessor module: accessors){
                    var value = module.getter().invoke(obj);
                    String strValue = converter.convert(value,String.class);
                    sections[module.order()] = new Section(module.name(),strValue);
                }
            }catch (Throwable e){
                throw new RuntimeException("failed to serialize object. Cause:" + e.getCause(),e);
            }
            return sections;
        };
    }

    /**
     * When there is no constructor annotated with {@link SerializerCreator}, then that deserializer
     * implementation will use modules setters, rather than all args constructor. In such case
     * fields can not be final.
     * @param modules list of modules assigned for instance that will use this method
     * @param converter type that will convert string to field value as castable object.
     * @param constructor public no args constructor without annotations
     * @return deserialization implementation
     */
    private static Function<Section[],Object> createFieldsDeserializer(
            List<ModuleAccessor> modules, Converter converter, Constructor<?> constructor){
        return (sections)->{
            Object instance;
            try{
                instance = constructor.newInstance();
                for(ModuleAccessor module :modules){
                    String value = sections[module.order()].value();
                    var arg = converter.convert(value,module.type());
                    module.setter().invoke(instance,arg);
                }
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
            return instance;
        };
    }

    /**
     * Create deserializer implementation that should be used when McfClass constructor was
     * annotated with {@link SerializerCreator}, then it will be used to create instance,
     * rather than modules setters. That means that class fields may be final.
     * @param modules list of modules assigned for instance that will use this method
     * @param converter type that will convert string to field value as castable object.
     * @param constructor annotated with {@link SerializerCreator} that contains parameters
     *                    in the same order as specified with {@link Module#order()}
     * @return deserialization implementation
     */
    private static Function<Section[],Object> createConstructorDeserializer(
            List<ModuleAccessor> modules, Converter converter, Constructor<?> constructor){
        return (sections)->{
            Object instance;
            try{
                Object[] args = new Object[sections.length];
                for(int i =0; i < modules.size(); i++){
                    ModuleAccessor module = modules.get(i);
                    String value = sections[module.order()].value();
                    var arg = converter.convert(value,module.type());
                    args[i] = arg;
                }
                instance = constructor.newInstance(args);
            }catch (Exception e){
                throw new RuntimeException("Could not instantiate object. " +
                        "Make sure parameters in constructor are in the same in the same order as" +
                        " annotation order value on fields. : " + e.getMessage(), e);
            }
            return instance;
        };
    }

    /**
     * Read all fields from a class by using java reflections to create field accessors, and metadata
     * such as: field type, field name, field order.
     * If setter is not accessible due to {@link IllegalAccessException}, then method handle will be null.
     * If getter is not accessible then {@link RuntimeException} will be thrown.
     * @param mcfClass class that fields will be scanned using java reflections.
     * @return sorted accessors by {@link Module#order()} in ascending order.
     */
    private static List<ModuleAccessor> createModuleAccessors(Class<?> mcfClass){
        Field[] mcfField = mcfClass.getDeclaredFields();
        List<ModuleAccessor> moduleAccessors = new ArrayList<>();
        for(Field field : mcfField){
            ModuleAccessor moduleAccessor = createModuleAccessor(field);
            if(moduleAccessor.order() <0) continue;
            moduleAccessors.add(moduleAccessor);
        }
        if(moduleAccessors.isEmpty()) throw new IllegalArgumentException("There are no fields with MCF annotations in " + mcfClass);
        return sortModulesOrder(moduleAccessors);
    }

    /**
     * Read field from a class by using java reflections to create field accessors, and metadata
     * such as: field type, field name, field order. Since final fields does not have write access then setter
     * for a field will be null.
     * If setter is not accessible due to {@link IllegalAccessException}, then method handle will be null.
     * If getter is not accessible then {@link RuntimeException} will be thrown.
     * @param field field of a class which should be MCF entity
     * @return field metadata with getters and setters
     */
    private static ModuleAccessor createModuleAccessor(Field field){
        int order = getFieldOrder(field);
        field.setAccessible(true);
        Class<?> type = field.getType();
        MethodHandle getter;
        MethodHandle setter;
        try{
            getter = MethodHandles.lookup().unreflectGetter(field);
        }catch (Exception e){
            throw new RuntimeException("Failed to create module accessor. Cause: " +e.getMessage(), e);
        }
         try{
             setter = MethodHandles.lookup().unreflectSetter(field);
         }catch (IllegalAccessException e){
             setter = null;
         }

        return new ModuleAccessor(order,type,field.getName(),getter,setter);
    }

    /**
     * Return order of the field specified by {@link Module}
     * If field does not have any annotation then order is a negative value
     * @param field field with annotation
     * @return specified order, or -1 when annotation is not present
     */
    private static int getFieldOrder(Field field){
        int order;
        if(field.isAnnotationPresent(Module.class)){
            order = field.getAnnotation(Module.class).order();
        } else return -1;

        if(order < 0 ){
            throw new IllegalArgumentException("Module order must be positive, but was" + order);
        }
        return order;
    }

    /**
     * Make a sorted list of module accessors, which are ordered by {@link ModuleAccessor#order()}
     * Order of module accessor depends on {@link Module#order()}
     * @param modules fields representing ModularCryptFormat sections
     * @return validated ordered list of modules where {@code moduleList.get(i).order() == i}
     */
    private static List<ModuleAccessor> sortModulesOrder(List<ModuleAccessor> modules){
        var sortedModules = new ArrayList<>(modules).stream()
                .sorted(ModuleAccessor::compareTo).toList();
        return validateModulesOrder(sortedModules);
    }

    /**
     * Validate if fields of Modular Crypt Format class representation are in correct order, order is unique, and
     * order is not skipped. list order must follow rule {@code moduleList.get(i).order() == i}.
     * Order is specified by {@link Module#order()}
     * @param ordered list that is expected to be sorted ascending order.
     * @return the same list of modules.
     * @throws IllegalArgumentException when list is not ordered correctly.
     */
    private static List<ModuleAccessor> validateModulesOrder(List<ModuleAccessor> ordered){
        int lastOrder =-1;
        for (int i=0; i<ordered.size(); i++) {
            ModuleAccessor mcf = ordered.get(i);
            if (lastOrder == mcf.order()) {
                throw new IllegalArgumentException(
                        "MCF module order collision detected. Two or more modules have the same order: " + lastOrder);
            }
            if (lastOrder > mcf.order()) {
                throw new RuntimeException("MCF sections are not sorted");
            }
            if(mcf.order() != i) throw new IllegalArgumentException(
                    String.format("Module order has skipped value. Expected order: %d but %d was found for field ",i,mcf.order()));
            lastOrder = mcf.order();
        }
        return ordered;
    }

}