package io.github.ysdaeth.jmodularcrypt.impl.encryptor;

import io.github.ysdaeth.jmodularcrypt.api.Encryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class EncryptorAesGcmTest {

    private static final String SECRET = "secret 123$% ąóźć \uD83D\uDE3A";
    private static byte[] secretBytes(){
        return SECRET.getBytes();
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_ShouldNotReturnBlank(EncryptorAesGcm encryptor) throws Exception{
        SecretKey secretKey = keyGen();
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret,secretKey);
        Assertions.assertFalse(encrypted.isBlank(),"Encrypted is blank for: "+encryptor.getClass());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_ShouldReturnFourModules(EncryptorAesGcm encryptor) throws Exception{
        SecretKey secretKey = keyGen();
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret,secretKey);
        int expectedSize = 4;
        int actualSize = Arrays.stream(encrypted.split("\\$"))
                .filter(s->!s.isBlank()).collect(Collectors.toSet()).size();

        Assertions.assertEquals(expectedSize,actualSize,
                "There should be 4 modules but, was found: "+actualSize);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_ShouldNotContainUnencryptedSecret(EncryptorAesGcm encryptor) throws Exception{
        SecretKey secretKey = keyGen();
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret,secretKey);

        String encryptedBase64 = Arrays.stream(encrypted.split("\\$")).toList().getLast();
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        boolean isEncrypted = !Arrays.equals(secret,encryptedBytes);
        Assertions.assertTrue(isEncrypted,"Secret is not encrypted");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldReturnTheSameSecret(EncryptorAesGcm encryptor) throws Exception{
        SecretKey secretKey = keyGen();
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret, secretKey);
        byte[] decrypted = encryptor.decrypt(encrypted, secretKey);
        boolean equals = Arrays.equals(decrypted, secret);
        Assertions.assertTrue(equals,"Secret after decryption does not match");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldThrowExceptionOnIncorrectKeyClass(Encryptor encryptor) throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();

        Assertions.assertThrows(IllegalArgumentException.class,()->{
            encryptor.encrypt(new byte[]{1,2,3},privateKey);
        });
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowExceptionOnIncorrectKeyClass(Encryptor encryptor) throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            encryptor.decrypt("",privateKey);
        });
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowKeyExceptionOnOtherKeyTheSameClass(Encryptor encryptor) throws Exception{
        SecretKey validKey = keyGen();
        SecretKey invalidKey = keyGen();
        byte[] secret = secretBytes();

        String encrypted = encryptor.encrypt(secret,validKey);

        Assertions.assertThrows(KeyException.class,()->{
            encryptor.decrypt(encrypted, invalidKey);
        });
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldThrowIncorrectAlgorithmException(Encryptor encryptor) throws Exception{
        SecretKey secretKey = keyGen();
        byte[] secret = secretBytes();

        String mcf = encryptor.encrypt(secret, secretKey);
        List<String> modules = Arrays.stream(mcf.split("\\$"))
                .filter(s->!s.isBlank()).toList();
        ArrayList<String> parts = new ArrayList<>(modules);

        parts.set(0,"RSA-OAEP");
        String changedMcf = "$"+ String.join("$",parts);

        Assertions.assertThrows(IncorrectAlgorithmException.class,()->{
            encryptor.decrypt(changedMcf, secretKey);
        });
    }



    public static Stream<EncryptorAesGcm> provider() throws Exception{
        return Stream.of(
                new EncryptorAesGcm()
        );
    }

    private static SecretKey keyGen() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return  keyGen.generateKey();
    }

}