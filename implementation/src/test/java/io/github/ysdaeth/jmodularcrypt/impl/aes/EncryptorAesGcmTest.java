package io.github.ysdaeth.jmodularcrypt.impl.aes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.stream.Stream;


class EncryptorAesGcmTest {

    private static final String SECRET = "secret 123$% ąóźć \uD83D\uDE3A";
    private static byte[] secretBytes(){
        return SECRET.getBytes();
    }

    @ParameterizedTest
    @MethodSource("provider")
    void encrypt_ShouldNotReturnBlank(EncryptorAesGcm encryptor) throws Exception{
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret);
        Assertions.assertFalse(encrypted.isBlank(),"Encrypted is blank for: "+encryptor.getClass());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_ShouldNotReturnTheSameSecret(EncryptorAesGcm encryptor) throws Exception{
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret);
        Assertions.assertNotEquals(SECRET,encrypted,"Secret is not encrypted");
    }

    @ParameterizedTest
    @MethodSource("provider")
    void decrypt_shouldReturnSecret(EncryptorAesGcm encryptor) throws Exception{
        byte[] secret = secretBytes();
        String encrypted = encryptor.encrypt(secret);
        byte[] decrypted = encryptor.decrypt(encrypted);
        boolean equals = Arrays.equals(decrypted,secret);
        Assertions.assertTrue(equals,"Secret after decryption does not match");
    }

    public static Stream<EncryptorAesGcm> provider() throws Exception{
        return Stream.of(
                new EncryptorAesGcm(keyGen())
        );
    }

    private static SecretKey keyGen() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return  keyGen.generateKey();
    }

}