package io.github.ysdaeth.jmodularcrypt.impl.encryptor;

import io.github.ysdaeth.jmodularcrypt.api.Encryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class RsaEncryptorsTest {

    private static final String SECRET = "secret 123$% ąóźć \uD83D\uDE3A";
    private static byte[] getSecret(){
        return SECRET.getBytes(StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldNotReturnBlank(Encryptor encryptor) throws Exception{
        KeyPair keyPair = keyGen();
        byte[] credentials = getSecret();
        String encrypted = encryptor.encrypt(credentials, keyPair.getPublic());
        Assertions.assertFalse(encrypted.isBlank(),"Encrypted is blank for: "+encryptor.getClass());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldNotContainUnencryptedSecret(Encryptor encryptor) throws Exception{
        KeyPair keyPair = keyGen();
        byte[] secret = getSecret();

        String mcf = encryptor.encrypt(secret,keyPair.getPublic());
        String encryptedBase64 = Arrays.stream(mcf.split("\\$")).toList().getLast();
        byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);

        boolean isEncrypted = !Arrays.equals(encrypted,secret);
        Assertions.assertTrue(isEncrypted,"Secret is not encrypted for "+encryptor.getClass());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldReturnTheSameSecret(Encryptor encryptor) throws Exception{
        KeyPair keyPair = keyGen();

        byte[] secret = getSecret();
        String mcf = encryptor.encrypt(secret,keyPair.getPublic());

        byte[] decrypted = encryptor.decrypt(mcf,keyPair.getPrivate());
        boolean isTheSameSecret = Arrays.equals(secret,decrypted);
        Assertions.assertTrue(isTheSameSecret,"Secret after decryption is not the same");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldThrowExceptionOnIncorrectKeyClass(Encryptor encryptor){
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        SecretKey secretKey = new SecretKeySpec(keyBytes,"AES");
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            encryptor.encrypt(new byte[]{1,2,3},secretKey);
        });
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowExceptionOnIncorrectKeyClass(Encryptor encryptor){
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        SecretKey secretKey = new SecretKeySpec(keyBytes,"AES");
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            encryptor.decrypt("",secretKey);
        },"Incorrect exception thrown for incorrect key type.  "+ encryptor.getClass() );
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowKeyExceptionOnOtherKey(Encryptor encryptor) throws Exception{
        KeyPair validKeyPair = keyGen();
        KeyPair invalidKeyPair = keyGen();

        byte[] secret = getSecret();
        String encrypted = encryptor.encrypt(secret,validKeyPair.getPublic());

        Assertions.assertThrows(KeyException.class,()->{
            encryptor.decrypt(encrypted, invalidKeyPair.getPrivate());
        });
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowIncorrectAlgorithmException(Encryptor encryptor) throws Exception{
        KeyPair keyPair = keyGen();
        byte[] secret = getSecret();

        String mcf = encryptor.encrypt(secret, keyPair.getPublic());
        List<String> modules = Arrays.stream(mcf.split("\\$"))
                .filter(s->!s.isBlank()).toList();
        ArrayList<String> parts = new ArrayList<>(modules);

        parts.set(0,"RSA-OAEP");
        String changedMcf = "$"+ String.join("$",parts);

        Assertions.assertThrows(IncorrectAlgorithmException.class,()->{
            encryptor.decrypt(changedMcf, keyPair.getPrivate());
        });
    }


    public static Stream<Encryptor> provider(){
        return Stream.of(
                new EncryptorRsaOaep(),
                new EncryptorRsaOaepAesGcm()
        );
    }

    private static KeyPair keyGen() throws Exception{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        return keygen.generateKeyPair();
    }

}