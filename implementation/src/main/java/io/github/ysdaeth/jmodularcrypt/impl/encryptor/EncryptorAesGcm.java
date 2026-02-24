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
import java.security.Key;
import java.security.KeyException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h2>AES GCM encryptor</h2>
 * Class purpose is to encrypt data and is designed to provide Modular Crypt Format standard output.
 * Internally it uses AES GCM with 96bit initial vector.
 * It uses cipher instance provided by the {@link java.security.Provider}
 * For more details see {@link Encryptor}.
 * <p>Example Modular Crypt Output Format</p>
 * <blockquote>$AES-GCM$v=1$iv=aBc$encryptedBytesBase64</blockquote>
 */
public class EncryptorAesGcm implements Encryptor {

    public static final String IDENTIFIER = "AES-GCM";
    private static final String VERSION = "v=1";
    private final Serializer modelSerializer;
    private final Serializer paramsSerializer;

    private final BaseAes baseAes;

    /**
     *
     * Creates an instance of the symmetric AES GCM, provided
     * by the {@link java.security.Provider} and implemented with a basic configuration.
     */
    public EncryptorAesGcm(){
        modelSerializer = SerializerFactory.getInstance(SerializerType.MCF_BASE64);
        paramsSerializer = SerializerFactory.getInstance(SerializerType.MCF_PARAMETER);
        baseAes = BaseAesFactory.getInstance("GCM");
    }

    /**
     * Encrypts data, and returns it in a Modular Crypt Format string representation.
     * Initially generates random 96bit initial vector.
     * Bytes array passed as an argument is cloned and after encryption the clone is filled with 0 bytes.
     * Original array is not being modified.
     * <blockquote><pre>
     *     SecretKey aesKey = new SecretKeySpec(...);
     *     byte[] secret = new byte[]{1,2,3};
     *     Encryptor gcm = new EncryptorAesGcm();
     *     String mcf = gcm.encrypt(secret, secretKey);
     * </pre></blockquote>
     * @param secret secret to be encrypted
     * @param encryptionKey key for data encryption
     * @return Modular Crypt Format string representation
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public String encrypt(byte[] secret, Key encryptionKey) throws KeyException {
        if(encryptionKey == null)
            throw new IllegalArgumentException("Encryption key must not be null");

        if(!(encryptionKey instanceof SecretKey castedSecretKey))
            throw new IllegalArgumentException("Encryption key must be an instance of the "+ SecretKey.class);

        byte[] credentials = secret.clone();
        try{
            return encryptUnsafe(credentials,castedSecretKey);
        }finally {
            Arrays.fill(credentials,(byte)0);
        }
    }

    /**
     * Does the same as the {@link this#encrypt(byte[],Key)}, but does not clone or clear the secret bytes
     * @param secret secret to encrypt
     * @param encryptionKey key for data encryption
     * @return MCF string format
     * @throws KeyException when key does not match or is invalid
     */
    private String encryptUnsafe(byte[] secret, SecretKey encryptionKey) throws KeyException{
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        byte[] encrypted = baseAes.encrypt(secret,encryptionKey,iv.clone());
        String params = paramsSerializer.serialize(new ParamsMcf(iv));
        AesMcfEntity aesMcfEntity = new AesMcfEntity(IDENTIFIER,VERSION,params,encrypted);
        return modelSerializer.serialize(aesMcfEntity);
    }

    /**
     * Decrypts encrypted data from string MCF, and returns it as raw bytes array.
     * <p>Decryption example</p>
     * <blockquote><pre>
     *     Key secretKey = new SecretKeySpec(...);
     *     String mcf ="$AES-GCM$v=1$iv=aBc$encryptedBytesBase64";
     *     Encryptor gcm = new EncryptorAesGcm();
     *     byte[] secret = gcm.decrypt(mcf, secretKey);
     * </pre></blockquote>
     * @param encrypted secret to be decrypted from the Modular Crypt Format
     *                      string representation
     * @return decrypted secret as byte array
     * @throws KeyException when key does not match encrypted data or is not suitable for this algorithm.
     */
    @Override
    public byte[] decrypt(String encrypted, Key decryptionKey) throws KeyException {
        if(decryptionKey == null){
            throw new IllegalArgumentException("Decryption key must not be null");
        }
        if(!(decryptionKey instanceof SecretKey castedSecretKey)){
            throw new IllegalArgumentException("Decryption key must be an instance of the "+ SecretKey.class);
        }

        AesMcfEntity model = modelSerializer.deserialize(encrypted, AesMcfEntity.class);
        if(!IDENTIFIER.equals(model.identifier)){
            throw new IncorrectAlgorithmException(String.format(
                    "Incorrect algorithm. Required is '%s' but provided was '%s'.", IDENTIFIER, model.identifier)
            );
        }

        ParamsMcf params = paramsSerializer.deserialize(model.params, ParamsMcf.class);
        return baseAes.decrypt(model.encrypted, castedSecretKey, params.iv);
    }

    /**
     * @return Identifier of this algorithm instance
     */
    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    /**
     * @return the version of this algorithm instance
     */
    @Override
    public String version() {
        return VERSION;
    }

    /**
     * Class is used as entity for Modular Crypt Format representation for
     * this instance algorithm output.
     */
    private static class AesMcfEntity {
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final String params;
        @Module(order = 3)
        private final byte[] encrypted;

        @SerializerCreator
        public AesMcfEntity(String identifier, String version, String params, byte[] encrypted){
            this.identifier = Objects.requireNonNull(identifier,"Identifier module must not be null");
            this.version = Objects.requireNonNull(version,"Version module must not be null");
            this.params = Objects.requireNonNull(params,"Params module must not be null");
            this.encrypted = Objects.requireNonNull(encrypted,"Encrypted module must not be null");
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
            this.iv = Objects.requireNonNull(iv,"Initial vector module must not be null");
        }
    }
}
