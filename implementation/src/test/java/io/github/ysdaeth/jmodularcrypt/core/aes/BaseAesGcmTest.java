package io.github.ysdaeth.jmodularcrypt.core.aes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.stream.Stream;


class BaseAesGcmTest {

    private final SecretKey secretKey = keyGen(256);
    private final byte[] iv = generateInitialVector();
    private static final byte[] secret = "seCre7 !#%$ \uD83D\uDE3A".getBytes(StandardCharsets.UTF_8);

    private static byte[] generateInitialVector(){
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    static Stream<BaseAes> rsaProvider(){
        return Stream.of(
                BaseAesFactory.getInstance("GCM")
        );
    }

    private static SecretKey keyGen(int keySize){
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            return keyGenerator.generateKey();
        }catch (Exception e){
            throw new RuntimeException("failed to generate secret key");
        }
    }

    private boolean compareArrayBytes(byte[] src, byte[]target){
        if(src.length != target.length) return false;
        for(int i =0; i<src.length; i++){
            if(src[i] != target[i]) return false;
        }
        return true;
    }


    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldReturnNotNull(BaseAes aes) throws Exception{
        byte[] encrypted = aes.encrypt(secret.clone(), secretKey,iv);
        Assertions.assertNotNull(encrypted,"encrypted value was null for: "+ aes.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldNotReturnSameValue(BaseAes aes) throws Exception{
        byte[] encrypted = aes.encrypt(secret.clone(), secretKey,iv);
        boolean isEqual = compareArrayBytes(secret,encrypted);
        Assertions.assertFalse(isEqual,"Encrypted value equals raw secret for:" +aes.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldNotReturnSameArray(BaseAes aes) throws Exception{
        byte[] secretBytes = secret.clone();
        byte[] encrypted = aes.encrypt(secretBytes, secretKey,iv);
        Assertions.assertNotEquals(encrypted,secretBytes,"Original array was modified" +aes.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void decrypt_shouldEqualSource(BaseAes aes) throws Exception{
        byte[] encrypted = aes.encrypt(secret.clone(), secretKey,iv);
        byte[] decrypted = aes.decrypt(encrypted,secretKey,iv);
        boolean isEqual = compareArrayBytes(decrypted, secret);
        Assertions.assertTrue(isEqual,"Secret does not match after decryption for: "+ aes.getClass());
    }

}