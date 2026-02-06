package io.github.ysdaeth.jmodularcrypt.impl.mac;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.stream.Stream;


class HMacTest {

    @ParameterizedTest
    @MethodSource("provider")
    void sing_shouldNotReturnNullOrBlank(AbstractHMac hMac){
        byte[] message = new byte[]{1,2,3,4,5,6};
        String result = hMac.sign(message);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank(),"Sign was blank");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void verify_shouldReturnTrue(AbstractHMac hMac){
        byte[] message = new byte[]{1,2,3,4,5,6};
        String sign = hMac.sign(message);
        boolean isValid = hMac.verify(sign,message);
        Assertions.assertTrue(isValid,"verify returned false, when sign was valid");
    }

    @ParameterizedTest
    @MethodSource("differentKeysProvider")
    void verify_shouldReturnFalse(HMacPair pair){
        byte[] message = new byte[]{1,2,3,4,5,6};
        String sign = pair.first.sign(message);
        boolean isValid = pair.second.verify(sign,message);
        Assertions.assertFalse(isValid,"Verify returned true for different key");
    }

    static Stream<AbstractHMac> provider() throws Exception{
        SecretKey sk256 = KeyGenerator.getInstance("HmacSHA256").generateKey();
        SecretKey sk384 = KeyGenerator.getInstance("HmacSHA384").generateKey();
        SecretKey sk512 = KeyGenerator.getInstance("HmacSHA512").generateKey();
        return Stream.of(
                new HMacSha256(sk256),
                new HMacSha384(sk384),
                new HMacSha512(sk512)
        );
    }

    static Stream<HMacPair> differentKeysProvider() throws Exception{
        return Stream.of(
                new HMacPair("HmacSHA256"),
                new HMacPair("HmacSHA384"),
                new HMacPair("HmacSHA512")
        );
    }

    private static class HMacPair{
        private AbstractHMac first;
        private AbstractHMac second;

        private HMacPair(String keyAlg) throws Exception{
            SecretKey secretKey1 = KeyGenerator.getInstance(keyAlg).generateKey();
            SecretKey secretKey2 = KeyGenerator.getInstance(keyAlg).generateKey();

            if(keyAlg.equals("HmacSHA256")){
                first = new HMacSha256(secretKey1);
                second = new HMacSha256(secretKey2);
            }
            else if(keyAlg.equals("HmacSHA384")){
                first = new HMacSha384(secretKey1);
                second = new HMacSha384(secretKey2);
            }
            else if(keyAlg.equals("HmacSHA512")){
                first = new HMacSha512(secretKey1);
                second = new HMacSha512(secretKey2);

            }
            else throw new IllegalArgumentException("No such algorithm");
        }
    }

}