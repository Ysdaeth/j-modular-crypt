package io.github.ysdaeth.jmodularcrypt.impl.rsa;

import io.github.ysdaeth.jmodularcrypt.api.AsymmetricEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

public class RsaEncryptorsTest {

    private static final String SECRET = "secret 123$% ąóźć \uD83D\uDE3A";
    private static byte[] getSecret(){
        return SECRET.getBytes(StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldNotReturnBlank(AsymmetricEncryptor encryptor) throws Exception{
        byte[] credentials = getSecret();
        String encrypted = encryptor.encrypt(credentials);
        Assertions.assertFalse(encrypted.isBlank(),"Encrypted is blank for: "+encryptor.getClass());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_shouldNotReturnTheSameArray(AsymmetricEncryptor encryptor) throws Exception{
        byte[] expected = getSecret();
        String encrypted = encryptor.encrypt(expected);
        String base64 = Arrays.stream(encrypted.split("\\$")).toList().getLast();
        byte[] actual = Base64.getDecoder().decode(base64);
        boolean equals = Arrays.equals(actual,expected);
        Assertions.assertFalse(equals,"Secret is not encrypted");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldReturnSecret(AsymmetricEncryptor encryptor) throws Exception{
        byte[] expected = getSecret();
        String encrypted = encryptor.encrypt(expected);
        byte[] actual = encryptor.decrypt(encrypted);
        boolean equals = Arrays.equals(expected,actual);
        Assertions.assertTrue(equals,"Secret after decryption is not the same");
    }

    public static Stream<AsymmetricEncryptor> provider() throws Exception{
        KeyPair keyPair = keyGen();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        return Stream.of(
                new EncryptorRsaOaep(publicKey,privateKey),
                new EncryptorRsaOaepAesGcm(publicKey,privateKey)
        );
    }

    private static KeyPair keyGen() throws Exception{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        return keygen.generateKeyPair();
    }

}