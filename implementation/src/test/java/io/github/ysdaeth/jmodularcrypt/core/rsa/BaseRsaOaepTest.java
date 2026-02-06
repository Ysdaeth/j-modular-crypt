package io.github.ysdaeth.jmodularcrypt.core.rsa;


import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsa;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.stream.Stream;

class BaseRsaOaepTest {

    private final KeyPair keyPair = keyGen(2048);
    private static final byte[] secret = "seCre7 !#%$ \uD83D\uDE3A".getBytes(StandardCharsets.UTF_8);

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldReturnNotNull(BaseRsa rsa) throws Exception{
        byte[] encrypted = rsa.encrypt(secret.clone(), keyPair.getPublic());
        Assertions.assertNotNull(encrypted,"encrypted value was null for: "+ rsa.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldNotReturnSameValues(BaseRsa rsa) throws Exception{
        byte[] encrypted = rsa.encrypt(secret.clone(), keyPair.getPublic());
        boolean isEqual = compareArrayBytes(secret,encrypted);
        Assertions.assertFalse(isEqual,"Encrypted value equals raw secret for:" +rsa.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void decrypt_shouldEqualSource(BaseRsa rsa) throws Exception{
        byte[] encrypted = rsa.encrypt(secret.clone(), keyPair.getPublic());
        byte[] decrypted = rsa.decrypt(encrypted, keyPair.getPrivate());
        boolean isEqual = compareArrayBytes(decrypted, secret);
        Assertions.assertTrue(isEqual,"Secret does not match after decryption for: "+ rsa.getClass());
    }

    @ParameterizedTest
    @MethodSource("rsaProvider")
    void encrypt_shouldNotReturnSameArray(BaseRsa rsa) throws Exception{
        byte[] secretBytes = secret.clone();
        byte[] encrypted = rsa.encrypt(secretBytes, keyPair.getPublic());
        Assertions.assertNotEquals(encrypted,secretBytes,"Original array was modified" +rsa.getClass());
    }

    static Stream<BaseRsa> rsaProvider(){
        return Stream.of(
                BaseRsaFactory.getInstance("OAEP")
        );
    }

    private static KeyPair keyGen(int keySize){
        try{
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(keySize);
            return keyGenerator.generateKeyPair();
        }catch (Exception e){
            throw new RuntimeException("failed to generate key pair");
        }
    }

    private boolean compareArrayBytes(byte[] src, byte[]target){
        if(src.length != target.length) return false;
        for(int i =0; i<src.length; i++){
            if(src[i] != target[i]) return false;
        }
        return true;
    }

}