package io.github.ysdaeth.jmodularcrypt.common.serializer;

import io.github.ysdaeth.jmodularcrypt.common.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.common.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.common.converter.ConversionRegistry;
import io.github.ysdaeth.jmodularcrypt.common.converter.McfConverterBase64;
import io.github.ysdaeth.jmodularcrypt.common.parser.McfParser;
import io.github.ysdaeth.jmodularcrypt.common.parser.Parser;
import io.github.ysdaeth.jmodularcrypt.common.serializer.ConfigurableSerializer;
import io.github.ysdaeth.jmodularcrypt.common.serializer.Serializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

/**
 * Integration test for {@link ConfigurableSerializer} with
 * {@link SerializerConfig}
 */
class ConfigurableSerializerTest {

    @ParameterizedTest
    @MethodSource("parserProvider")
    void serializedStringShouldNotBeNull(Serializer parser, Object model){
        String serialized = parser.serialize(new PublicFields());
        Assertions.assertNotNull(serialized,"serialized string was null");
    }

    @ParameterizedTest
    @MethodSource("parserProvider")
    void serializedStringShouldNotBeBlank(Serializer parser, Object model){
        String serialized = parser.serialize(model);
        Assertions.assertFalse(serialized.isBlank(),"serialized string was null");
    }

    @ParameterizedTest
    @MethodSource("parserProvider")
    void deserializedShouldBeEqual(Serializer parser, Object model){
        PublicFields expected = new PublicFields();
        String serialized = parser.serialize(expected);
        PublicFields actual = parser.deserialize(serialized, PublicFields.class);
        Assertions.assertEquals(expected,actual,"The same MCF object does not equal after deserializing");
    }


    public static Stream<Arguments> parserProvider(){

        SerializerConfig configuration = new SerializerConfig() {
            @Override
            public ConversionRegistry typeConverter() {return new McfConverterBase64();}
            @Override
            public Parser parser() {return new McfParser();}
        };
        ConfigurableSerializer serializer = new ConfigurableSerializer(configuration);

        return Stream.of(
                Arguments.of(serializer, new PublicFields()),
                Arguments.of(serializer, new PrivateFields_ConstructorCreator()),
                Arguments.of(serializer, new PrivateFinalFields_ConstructorCreator())
        );
    }

    static class PublicFields {
        @Module(order = 0)
        public String identifier ="BasicMcfTypes";
        @Module(order = 1)
        public String version ="v=1";
        @Module(order = 2)
        public int iterations =2;
        @Module(order =3)
        public byte[] secret = new byte[]{1,2,3};

        public PublicFields(){}

        @Override
        public boolean equals(Object o){
            if(!(o instanceof PublicFields target)) return false;
            return this.identifier.equals(target.identifier) &&
                    this.version.equals(target.version) &&
                    this.iterations == target.iterations &&
                    Arrays.equals(secret,target.secret);
        }
        @Override
        public String toString(){
            return identifier + " " + version + " "+ iterations +
                    " "+ Base64.getEncoder().encodeToString(secret);
        }
    }


    static class PrivateFields {
        @Module(order = 0)
        private String identifier ="BasicMcfTypes";
        @Module(order = 1)
        private String version ="v=1";
        @Module(order = 2)
        private int iterations =2;
        @Module(order =3)
        private byte[] secret = new byte[]{1,2,3};


        public PrivateFields(){}

        @Override
        public boolean equals(Object o){
            if(!(o instanceof PublicFields target)) return false;
            return this.identifier.equals(target.identifier) &&
                    this.version.equals(target.version) &&
                    this.iterations == target.iterations &&
                    Arrays.equals(secret,target.secret);
        }
        @Override
        public String toString(){
            return identifier + " " + version + " "+ iterations +
                    " "+ Base64.getEncoder().encodeToString(secret);
        }
    }

    static class PrivateFields_ConstructorCreator {
        @Module(order = 0)
        private String identifier ="BasicMcfTypes";
        @Module(order = 1)
        private String version ="v=1";
        @Module(order = 2)
        private int iterations =2;
        @Module(order =3)
        private byte[] secret = new byte[]{1,2,3};


        public PrivateFields_ConstructorCreator(){}

        @SerializerCreator
        public PrivateFields_ConstructorCreator(String id, String version, int iterations, byte[] secret){
            this.identifier = id;
            this.version = version;
            this.iterations = iterations;
            this.secret = secret;

        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof PublicFields target)) return false;
            return this.identifier.equals(target.identifier) &&
                    this.version.equals(target.version) &&
                    this.iterations == target.iterations &&
                    Arrays.equals(secret,target.secret);
        }
        @Override
        public String toString(){
            return identifier + " " + version + " "+ iterations +
                    " "+ Base64.getEncoder().encodeToString(secret);
        }
    }

    static class PrivateFinalFields_ConstructorCreator {
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final int iterations;
        @Module(order =3)
        private final byte[] secret;

        public PrivateFinalFields_ConstructorCreator(){
            this.identifier ="BasicMcfTypes";
            this.version ="v=1";
            this.iterations =2;
            this.secret= new byte[]{1,2,3};
        }

        @SerializerCreator
        public PrivateFinalFields_ConstructorCreator(String id, String version, int iterations, byte[] secret){
            this.identifier = id;
            this.version = version;
            this.iterations = iterations;
            this.secret = secret;

        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof PublicFields target)) return false;
            return this.identifier.equals(target.identifier) &&
                    this.version.equals(target.version) &&
                    this.iterations == target.iterations &&
                    Arrays.equals(secret,target.secret);
        }
        @Override
        public String toString(){
            return identifier + " " + version + " "+ iterations +
                    " "+ Base64.getEncoder().encodeToString(secret);
        }
    }

}