package io.github.ysdaeth.jmodularcrypt.impl.encryptor;


import io.github.ysdaeth.jmodularcrypt.api.Encryptor;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerFactory;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerType;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.aes.BaseAes;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.aes.BaseAesFactory;
import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.core.serializer.Serializer;

import javax.crypto.SecretKey;
import java.security.KeyException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Class uses AES GCM.
 * Class purpose is to encrypt credentials that are needed to be recovered for
 * service functionality.
 * It is designed to provide Modular Crypt Format standard output with {@link Serializer}.
 * For more details see {@link Encryptor}
 * <p>Example</p>
 * {@code $AES-GCM$v=1$iv=aBc$encryptedValue}
 */
public class EncryptorAesGcm implements Encryptor {

    public static final String ID = "AES-GCM";
    private static final String VERSION = "v=1";
    private final Serializer modelSerializer;
    private final Serializer paramsSerializer;

    private SecretKey secretKey;

    private final BaseAes baseAes;

    /**
     * Create instance of symmetric AES GCM.
     * Key provided as an argument is used for encryption and decryption.
     * @param secretKey key for encryption and decryption
     */
    public EncryptorAesGcm(SecretKey secretKey){
        modelSerializer = SerializerFactory.getInstance(SerializerType.MCF_BASE64);
        paramsSerializer = SerializerFactory.getInstance(SerializerType.MCF_PARAMETER);

        this.secretKey = validateKey(secretKey);
        baseAes = BaseAesFactory.getInstance("GCM");
    }

    /**
     * Encrypt bytes and return Modular Crypt Format string value.
     * It uses {@link Serializer} configured to match MCF format.
     * It generates random 96bit initial vector.
     * Although initial vector collision chance is extremely low it is recommended
     * to rotate a secret keys. Secret passed as an argument is cloned and filled with 0 bytes
     * after encryption. Secret passed as an argument itself is not modified.
     * @param secret secret to be encrypted
     * @return Modular Crypt Format string representation
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public String encrypt(byte[] secret) throws KeyException {
        byte[] credentials = secret.clone();
        try{
            return encryptUnsafe(credentials);
        }finally {
            Arrays.fill(credentials,(byte)0);
        }
    }

    /**
     * Do the same as {@link this#encrypt(byte[])}, but does not clone or clear secret
     * @param secret secret to encrypt
     * @return MCF string format
     * @throws KeyException when key does not match or is invalid
     */
    private String encryptUnsafe(byte[] secret) throws KeyException{
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        byte[] encrypted = baseAes.encrypt(secret,secretKey,iv.clone());
        String params = paramsSerializer.serialize(new ParamsMcf(iv));
        McfEntity mcfEntity = new McfEntity(params,encrypted);
        return modelSerializer.serialize(mcfEntity);
    }

    /**
     * Decrypt Modular Crypt Format string representation
     * and return decrypted secret as string. It uses {@link Serializer} configured to match
     * MCF format.
     * @param encrypted secret to be decrypted from Modular Crypt Format
     *                      string representation
     * @return decrypted secret
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public byte[] decrypt(String encrypted) throws KeyException {
        McfEntity model = modelSerializer.deserialize(encrypted, McfEntity.class);
        ParamsMcf params = paramsSerializer.deserialize(model.params,ParamsMcf.class);
        return baseAes.decrypt(model.encrypted,secretKey,params.iv);
    }

    /**
     * @return Identifier of this algorithm instance
     */
    @Override
    public String identifier() {
        return ID;
    }

    /**
     * @return version of this algorithm instance
     */
    @Override
    public String version() {
        return VERSION;
    }

    private static SecretKey validateKey(SecretKey secretKey){
        if(secretKey == null) throw new IllegalArgumentException("Secret key must not be null");
        String keyAlgorithm = secretKey.getAlgorithm();
        if(!"AES".equals(keyAlgorithm)) throw new IllegalArgumentException("Required key algorithm is AES, but provided was: "+ keyAlgorithm);
        return secretKey;
    }

    /**
     * Class is used as entity for Modular Crypt Format representation for
     * this instance algorithm output.
     */
    private static class McfEntity {
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final String params;
        @Module(order = 3)
        private final byte[] encrypted;

        @SerializerCreator
        public McfEntity(String identifier, String version, String params, byte[] encrypted){
            this.identifier = identifier;
            this.version = version;
            this.params = params;
            this.encrypted = encrypted;
        }

        public McfEntity(String params, byte[] encrypted){
            this.identifier = ID;
            this.version = VERSION;
            this.params = params;
            this.encrypted = encrypted;
        }
    }
    /**
     * Class is used as model for Modular Crypt Format parameters representation for
     * this instance algorithm output.
     */
    private static class ParamsMcf{
        @Module(order = 0)
        private final byte[] iv;

        @SerializerCreator
        public ParamsMcf(byte[] iv){
            this.iv = iv;
        }
    }
}
