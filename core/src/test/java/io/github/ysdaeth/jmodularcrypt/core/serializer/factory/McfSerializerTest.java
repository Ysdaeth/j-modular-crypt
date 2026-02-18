package io.github.ysdaeth.jmodularcrypt.core.serializer.factory;

import io.github.ysdaeth.jmodularcrypt.core.serializer.mocked.McfClass;
import io.github.ysdaeth.jmodularcrypt.core.serializer.mocked.McfClassPrivateFinalFields;
import io.github.ysdaeth.jmodularcrypt.core.serializer.mocked.McfClassProtectedFields;
import io.github.ysdaeth.jmodularcrypt.core.serializer.mocked.McfRecordClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

class McfSerializerTest {

    private static final McfSerializer serializer = new McfSerializer();

    private static final String EXPECTED_NAME = "Jeff";
    private static final Supplier<byte[]> EXPECTED_BYTES = ()->new byte[]{1,2,3};
    private static final String EXPECTED = "$Jeff$AQID";

    @ParameterizedTest
    @MethodSource("mcfClassProvider")
    public void serialize_shouldReturnString(McfClass mcfClass){
        String actual = serializer.serialize(mcfClass);
        Assertions.assertEquals(EXPECTED,actual);
    }

    @ParameterizedTest
    @MethodSource("mcfClassProvider")
    public void deserialize_shouldReturnObject(McfClass mcfClass){
        McfClass mcf = serializer.deserialize(EXPECTED, mcfClass.getClass());

        byte[] expectedBytes = EXPECTED_BYTES.get();
        String actualName = mcf.getName();
        byte[] actualBytes = mcf.getBytes();

        boolean bytesMatches = Arrays.equals(expectedBytes, actualBytes);
        Assertions.assertTrue(bytesMatches,"Bytes array does not match");
        Assertions.assertEquals(EXPECTED_NAME,actualName);
    }

    public static Stream<McfClass> mcfClassProvider(){

        return Stream.of(
                new McfClassPrivateFinalFields(EXPECTED_NAME, EXPECTED_BYTES.get()),
                new McfClassProtectedFields(EXPECTED_NAME, EXPECTED_BYTES.get()),
                new McfRecordClass(EXPECTED_NAME, EXPECTED_BYTES.get())
        );

    }

}