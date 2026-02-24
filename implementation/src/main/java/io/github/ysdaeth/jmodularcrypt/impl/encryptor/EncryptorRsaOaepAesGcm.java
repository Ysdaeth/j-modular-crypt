package io.github.ysdaeth.jmodularcrypt.impl.encryptor;

import io.github.ysdaeth.jmodularcrypt.api.Encryptor;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerFactory;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerType;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.aes.BaseAes;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.aes.BaseAesFactory;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsa;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsaFactory;
import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.core.serializer.Serializer;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h2>Hybrid RSA and AES</h2>
 *
 * Class uses RSA OAEP SHA256 with MGF1 padding and AES GCM with 96bit initial vector
 * for a hybrid data encryption / decryption. It generates new random 256bit
 * {@link SecretKey} used by AES GCM, for every data encryption. Actual algorithms implementations are provided
 * by the {@link java.security.Provider}. Class purpose is to encrypt data that size exceeds
 * {@link EncryptorRsaOaep} and return encrypted data in Modular Crypt Format standard output.
 * After encrypting data with a randomly generated AES GCM key, AES key itself is encrypted with RSA.
 * <p>Example Modular Crypt Output Format</p>
 * $RSA-OAEP-SHA256-MGF1+AES-GCM-256$v=1$iv=abc$encryptedRandomKey$encryptedValue
 */
public class EncryptorRsaOaepAesGcm implements Encryptor {
    public static final String IDENTIFIER = "RSA-OAEP-SHA256-MGF1+AES-GCM-256";
    private static final String VERSION = "v=1";
    private final Serializer modelSerializer;
    private final Serializer paramsSerializer;

    private final BaseAes baseAes;
    private final BaseRsa baseRsa;
    private final KeyGenerator keyGenerator;

    /**
     * Creates an instance of the hybrid RSA OAEP SHA256 MGF1 padding and AES GCM, provided
     * by the {@link java.security.Provider} and implemented with a basic configuration.
     */
    public EncryptorRsaOaepAesGcm() {
        try{
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            baseAes = BaseAesFactory.getInstance("GCM");
            baseRsa = BaseRsaFactory.getInstance("OAEP");
        }catch (Exception e){
            throw new RuntimeException("Could not configure the class. Root cause"+ e.getMessage(), e);
        }
        modelSerializer = SerializerFactory.getInstance(SerializerType.MCF_BASE64);
        paramsSerializer = SerializerFactory.getInstance(SerializerType.MCF_PARAMETER);
    }

    /**
     * Encrypts data with the generated random secret key and returns encrypted
     * data in Modular Crypt Format string representation.
     * Generates a random 256bit AES key, and uses it to encrypt the data, then
     * key itself is encrypted with RSA public key. Bytes passed as an argument are
     * cloned and after encryption are filled with 0 bytes original array is not modified.
     * <blockquote><pre>
     *     PublicKey pubKey = // get a public key;
     *     byte[] secret = new byte[]{1,2,3};
     *     Encryptor rsa = new EncryptorRsaOaepAesGcm();
     *     String mcf = rsa.encrypt(secret, secretKey);
     * </pre></blockquote>
     * @param data data to be encrypted.
     * @param publicKey instance of the {@link PublicKey}.
     * @return encrypted data in Modular Crypt Format string representation.
     * @throws KeyException when key does not match this algorithm.
     */
    @Override
    public String encrypt(byte[] data, Key publicKey) throws KeyException {
        if(publicKey == null)
            throw new IllegalArgumentException("Encryption key must not be null");

        if(! (publicKey instanceof PublicKey castedPublicKey))
            throw new IllegalArgumentException("Encryption key must be instance of the "+ PublicKey.class);

        byte[] credentials = data.clone();
        try{
            return encryptUnsafe(credentials,castedPublicKey);
        } finally {
            Arrays.fill(credentials,(byte)0);
        }
    }
    private String encryptUnsafe(byte[] secret, PublicKey publicKey) throws KeyException{
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encryptedKey = baseRsa.encrypt(secretKey.getEncoded(),publicKey);
        byte[] encryptedCredentials = baseAes.encrypt(secret,secretKey,iv);
        String params = paramsSerializer.serialize( new McfParams(iv) );
        RsaAesMcfModel model = new RsaAesMcfModel(IDENTIFIER,VERSION,params,encryptedKey,encryptedCredentials);
        return modelSerializer.serialize(model);
    }

    /**
     * Decrypts data stored in the Modular Crypt Format string representation and returns
     * decrypted as the bytes array.
     * @param mcf data to be decrypted from Modular Crypt Format string representation
     * @return decrypted data as a bytes array
     * @throws KeyException when key does not match the encrypted secret or this algorithm
     */
    @Override
    public byte[] decrypt(String mcf, Key privateKey) throws KeyException {
        if(privateKey == null)
            throw new IllegalArgumentException("Decryption key must not be null");

        if(!(privateKey instanceof PrivateKey castedPrivateKey))
            throw new IllegalArgumentException("Decryption key must be an instance of the "+ PrivateKey.class);

        RsaAesMcfModel model = modelSerializer.deserialize(mcf, RsaAesMcfModel.class);

        if(!IDENTIFIER.equals(model.identifier)){
            throw new IncorrectAlgorithmException(String.format(
                    "Incorrect algorithm. Required is '%s' but provided was '%s'.", IDENTIFIER, model.identifier)
            );
        }

        McfParams params = paramsSerializer.deserialize(model.params,McfParams.class);
        byte[] keyBytes = baseRsa.decrypt(model.encryptedKey,castedPrivateKey);
        SecretKey secretKey = new SecretKeySpec(keyBytes,"AES");
        return baseAes.decrypt(model.encryptedSecret,secretKey,params.iv);
    }

    /**
     * @return Identifier of this algorithm instance
     */
    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    /**
     * @return version of this algorithm instance
     */
    @Override
    public String version() {
        return VERSION;
    }

    /**
     * Class is used as model for Modular Crypt Format representation for
     * this instance algorithm output.
     */
    private static final class RsaAesMcfModel {
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final String params;
        @Module(order = 3)
        private final byte[] encryptedKey;
        @Module(order =4)
        private final byte[] encryptedSecret;

        @SerializerCreator
        public RsaAesMcfModel(String identifier, String version, String params, byte[] encryptedKey, byte[] encryptedSecret) {
            this.identifier = Objects.requireNonNull(identifier,"Identifier module must not be null");
            this.version = Objects.requireNonNull(version,"Version module must not be null");
            this.params = Objects.requireNonNull(params,"Params module must not be null");
            this.encryptedKey = Objects.requireNonNull(encryptedKey,"Encrypted key module must not be null");
            this.encryptedSecret = Objects.requireNonNull(encryptedSecret,"Encrypted data module must not be null");
        }
    }

    /**
     * Class is used as model for Modular Crypt Format parameters representation for
     * this instance algorithm output.
     */
    private static final class McfParams{
        @Module(order = 0)
        private final byte[] iv;

        @SerializerCreator
        public McfParams(byte[] iv) {
            this.iv = Objects.requireNonNull(iv,"Initial vector must not be null");
        }
    }
}
