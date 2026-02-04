package io.github.ysdaeth.jmodularcrypt.common.serializer;

import io.github.ysdaeth.jmodularcrypt.common.converter.TypeConverter;
import io.github.ysdaeth.jmodularcrypt.common.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.common.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Section;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class is thread safe configurable serializer / deserializer.
 * Class purpose is to provide flexible functionality and creating own parsing output rules.
 * This class converts between serialized {@link String} representations and Java objects
 * which fields are annotated with annotations such as:
 * {@link Module}.
 * Class can use {@link SerializerConfiguration} which provides {@link Parser} and {@link TypeConverter}
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
 *     <li>Field types supported by the {@link TypeConverter} </li>
 * </ol>
 * <b> constructor annotated with {@link SerializerCreator} </b>
 * <ol>
 *     <li>Constructor annotated with {@link SerializerCreator} </li>
 *     <li>Public constructor with parameters in the same order as {@link Module#order()}</li>
 *     <li>Fields annotated with {@link Module} </li>
 *     <li>Field types supported by the provided {@link TypeConverter}</li>
 * </ol>
 */
public final class ConfigurableSerializer implements Serializer {
    private static final Map<Class<?>, ClassSerializer> CACHE = new ConcurrentHashMap<>();
    private final TypeConverter typeConverter;
    private final Parser parser;

    public ConfigurableSerializer(SerializerConfiguration configuration){
        typeConverter = configuration.typeConverter();
        parser = configuration.parser();
    }

    /**
     * Serialize provided object to string value. Object class fields must
     * contain {@link Module} annotations with order that index starts from 0
     * {@link McfParser} is used to create MCF string value
     * @param mcfObject object to serialize
     * @return Object of specified type
     */
    public String serialize(Object mcfObject){
        Section[] sections = CACHE.computeIfAbsent(
                mcfObject.getClass(), this::createClassMetadata
                ).serialize(mcfObject);
        return parser.compose(sections);
    }

    /**
     * Deserialize provided string MCF value to specified class instance.
     * Class must contain {@link Module} annotations, and if fields are final,
     * then all args constructor should be annotated with {@link SerializerCreator} and must contain
     * parameters in the same order as {@link Module#order()}. Order index must start with 0
     * Constructors must be public. If there is only no args constructor, then fields cannot be final.
     * {@link McfParser} is used to create MCF string value
     * @param serialized MCF string value
     * @param mcfClass class that matches serialized string representation
     * @return Object of specified type
     * @param <T> return type
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String serialized, Class<T> mcfClass){
        Section[] sections = parser.parse(serialized);
        return (T)CACHE.computeIfAbsent(
                mcfClass, this::createClassMetadata
                ).deserialize(sections);
    }

    /**
     * Creates serialization metadata for the given MCF model class.
     * Resolves the constructor, and serialization, deserialization implementation
     * specific for provided class limited by {@link MethodHandles.Lookup} and
     * used annotations.
     * @param type class type to build metadata
     * @return serialization / deserialization implementation holder
     * @param <T> type of provided class
     */
    private <T> ClassSerializer createClassMetadata(Class<T> type){
        Constructor<T> constructor = resolveConstructor(type);
        List<ModuleAccessor> modules = createModules(type);
        Function<Object,Section[]> serializer = resolveSerializer(modules,typeConverter);
        Function<Section[],Object> deserializer = resolveDeserializer(modules,typeConverter,constructor);
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
        Constructor<T> ctor;
        try{
            ctor = getArgsConstructor(mcfClass);
            if(ctor == null) ctor = getNoArgsConstructor(mcfClass);
            ctor.setAccessible(true);
        }catch (Exception e){
            throw new RuntimeException(
                    "All args constructor or no args constructor must be public and declared in the MCF class: "
                            + mcfClass+" Root cause: " + e.getMessage(), e);
        }
        return ctor;
    }

    /**
     * Get constructor annotated with {@link SerializerCreator} by using java reflections.
     * if there is no such constructor return null, or if more than one constructor uses this annotation,
     * then throw illegal argument Exception.
     * Constructor parameters order must match with {@link Module#order()} value.
     * @param tClass class that constructor will be found with reflection
     * @return All args constructor constructor.
     * @param <T> type of the class constructor
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getArgsConstructor(Class<T> tClass){
        Constructor<?>[] ctors =  Arrays.stream(tClass.getConstructors()).
                filter(c->c.isAnnotationPresent(SerializerCreator.class))
                .toArray(Constructor<?>[]::new);
        if(ctors.length == 0) return null;
        if(ctors.length > 1 ) throw new IllegalArgumentException(
                "There is more than one " + SerializerCreator.class + " annotated constructor");
        return (Constructor<T> )ctors[0];
    }

    /**
     * Get public no args constructor, or null if not available
     * @param tClass class type that constructor will be reflected
     * @return constructor of specified class
     * @param <T> type of constructor class
     */
    private static <T> Constructor<T> getNoArgsConstructor(Class<T> tClass) throws Exception{
        return tClass.getConstructor();
    }

    /**
     * Based on provided entity class return implementation that will match requirements
     * and boundary set by {@link MethodHandles.Lookup} and {@link SerializerCreator}.
     * method is orchestrator if other implementations will be required
     * @param modules list of modules assigned for MCF class that will use this method
     * @return implementation for serializer that match {@link MethodHandles.Lookup} requirements
     * @param <T> type of object
     */
    private static <T> Function<T,Section[]> resolveSerializer(
            List<ModuleAccessor> modules,TypeConverter converter){

        return createSerializer(modules,converter);
    }

    /**
     * Create serializer for fields that are accessible and not annotated as private.
     * @param modules list of modules assigned for MCF class that will use this method
     * @return implementation of deserializer
     * @param <T> instance parameter type
     */
    private static <T> Function<T, Section[]> createSerializer(
            List<ModuleAccessor> modules, TypeConverter converter){

        return (obj)->{
            Section[] sections = new Section[modules.size()];
            try{
                for(ModuleAccessor module: modules){
                    var value = module.getter().invoke(obj);
                    String strValue = converter.objectToString(value);
                    if(value == null) throw new IllegalArgumentException("field value must not be null.");
                    sections[module.order()] = new Section(module.name(),strValue);
                }
            }catch (Throwable e){
                throw new RuntimeException("failed to serialize object. Cause:",e);
            }
            return sections;
        };
    }

    /**
     * Check if provided MCF class constructor is annotated with {@link SerializerCreator} and based
     * on that it will return proper implementation of deserializer
     * @param modules list of modules assigned for instance that will use this method
     * @param converter type that will convert string to field value as castable object.
     * @param constructor that will be checked
     * @return deserialization implementation
     */
    private static Function<Section[],Object> resolveDeserializer(
            List<ModuleAccessor> modules, TypeConverter converter, Constructor<?> constructor){
        if(constructor.isAnnotationPresent(SerializerCreator.class) ){
            return createConstructorDeserializer(modules,converter,constructor);
        }else{
            return createFieldsDeserializer(modules,converter,constructor);
        }
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
            List<ModuleAccessor> modules, TypeConverter converter, Constructor<?> constructor){
        return (sections)->{
            Object instance;
            try{
                instance = constructor.newInstance();
                for(ModuleAccessor module :modules){
                    String value = sections[module.order()].value();
                    var arg = converter.stringToObject(value,module.type());
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
            List<ModuleAccessor> modules, TypeConverter converter, Constructor<?> constructor){
        return (sections)->{
            Object instance;
            try{
                Object[] args = new Object[sections.length];
                for(int i =0; i < modules.size(); i++){
                    ModuleAccessor module = modules.get(i);
                    String value = sections[module.order()].value();
                    var arg = converter.stringToObject(value,module.type());
                    args[i] = arg;
                }
                instance = constructor.newInstance(args);
            }catch (Exception e){
                throw new RuntimeException("Could not instantiate object. " +
                        "Make sure parameters in constructor are in the same in the same order as" +
                        " annotation order value on fields. Root cause: " + e.getMessage(), e);
            }
            return instance;
        };
    }

    /**
     * Read all fields from a class by using java reflections, and create field metadata
     * with {@link ConfigurableSerializer#createModule(Field)}
     * returned list is sorted by specified order with {@link Module#order()}
     * @param mcfClass class that fields will be scanned using java reflections
     * @return list of fields wrapped with object with unreflected getters and setters
     */
    private static List<ModuleAccessor> createModules(Class<?> mcfClass){
        Field[] mcfField = mcfClass.getDeclaredFields();
        List<ModuleAccessor> moduleAccessors = new ArrayList<>();
        for(Field field : mcfField){
            ModuleAccessor moduleAccessor = createModule(field);
            if(moduleAccessor.order() <0) continue;
            moduleAccessors.add(moduleAccessor);
        }
        if(moduleAccessors.isEmpty()) throw new IllegalArgumentException("There are no fields with MCF annotations in " + mcfClass);
        return sortModulesOrder(moduleAccessors);
    }

    /**
     * Collect metadata from a field like field type, field order specified by
     * {@link Module} by using java reflection and then unreflect getter and setter.
     * @param field field of a class which should be MCF entity
     * @return field metadata
     */
    private static ModuleAccessor createModule(Field field){
        int order = getFieldOrder(field);
        field.setAccessible(true);
        Class<?> type = field.getType();
        MethodHandle getter;
        MethodHandle setter;
        try{
            getter = MethodHandles.lookup().unreflectGetter(field);
            setter = MethodHandles.lookup().unreflectSetter(field);
        }catch (Exception e){
            throw new RuntimeException("Failed to create module for MCF. Cause: " +e.getMessage(),e);
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