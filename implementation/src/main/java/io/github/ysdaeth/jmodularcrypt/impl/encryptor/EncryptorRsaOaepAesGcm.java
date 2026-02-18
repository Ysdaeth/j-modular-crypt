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

/**
 * Class uses RSA OAEP SHA256 with MGF1(SHA256) padding and AES GCM.
 * Class purpose is to encrypt credentials that size exceeds {@link EncryptorRsaOaep}
 * It is designed to provide Modular Crypt Format standard output with
 * {@link Serializer}
 * It generates new random 256bit {@link SecretKey} for every data encryption.
 * Randomly generated AES secret key is used to encrypt credentials. Initial vector
 * is random 12bytes length.
 * After encrypting credentials random key itself is encrypted with RSA.
 *
 * <p>Example</p>
 * $RSA-OAEP-SHA256-MGF1+AES-GCM-256 $v=1 $iv=abc $encryptedKey $encryptedValue  (without spaces)
 */
public class EncryptorRsaOaepAesGcm implements Encryptor {
    public static final String ID = "RSA-OAEP-SHA256-MGF1+AES-GCM-256";
    private static final String VERSION = "v=1";
    private final Serializer modelSerializer;
    private final Serializer paramsSerializer;

    private final BaseAes baseAes;
    private final BaseRsa baseRsa;
    private final KeyGenerator keyGenerator;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    /**
     * Create instance of Hybrid RSA OAEP SHA256 MGF1(SHA256) padding and AES GCM.
     * Keys provided as an argument do not need to be a pair, that means this instance can be used
     * for key rotations when non pair keys are needed.
     * @param publicKey key for encryption
     * @param privateKey key for decryption
     */
    public EncryptorRsaOaepAesGcm(PublicKey publicKey, PrivateKey privateKey) {
        try{
            this.publicKey = validateKey(publicKey);
            this.privateKey = validateKey(privateKey);
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            baseAes = BaseAesFactory.getInstance("GCM");
            baseRsa = BaseRsaFactory.getInstance("OAEP");
        }catch (Exception e){
            throw new RuntimeException("Could not configure class. Root cause"+ e.getMessage(), e);
        }
        modelSerializer = SerializerFactory.getInstance(SerializerType.MCF_BASE64);
        paramsSerializer = SerializerFactory.getInstance(SerializerType.MCF_PARAMETER);
    }

    /**
     * Encrypt bytes with generated random secret key and return Modular Crypt Format
     * string representation. It uses {@link Serializer} configured to match MCF format.
     * Random 256bit AES key is generated, and secret is encrypted with that key, then
     * key itself is encrypted with RSA public key. secret is cloned and filled with 0 bytes
     * after encryption, but secret passed as an argument is not modified.
     * @param secret credentials to be encrypted
     * @return Modular Crypt Format string representation
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public String encrypt(byte[] secret) throws KeyException {
        byte[] credentials = secret.clone();
        try{
            return encryptUnsafe(credentials);
        } finally {
            Arrays.fill(credentials,(byte)0);
        }
    }
    private String encryptUnsafe(byte[] secret) throws KeyException{
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encryptedKey = baseRsa.encrypt(secretKey.getEncoded(),publicKey);
        byte[] encryptedCredentials = baseAes.encrypt(secret,secretKey,iv);
        String params = paramsSerializer.serialize( new McfParams(iv) );
        McfModel model = new McfModel(ID,VERSION,params,encryptedKey,encryptedCredentials);
        return modelSerializer.serialize(model);
    }

    /**
     * Decrypt Modular Crypt Format string representation with private key
     * and return decrypted as string. It uses {@link Serializer} configured to match
     * MCF format. It decrypts encrypted AES 256bit key with RSA private key. Decrypted AES
     * key is used to decrypt main data
     * @param encrypted credentials to be decrypted from Modular Crypt Format
     *                      string representation
     * @return decrypted credentials as string
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public byte[] decrypt(String encrypted) throws KeyException {
        McfModel model = modelSerializer.deserialize(encrypted,McfModel.class);
        McfParams params = paramsSerializer.deserialize(model.params,McfParams.class);
        byte[] keyBytes = baseRsa.decrypt(model.encryptedKey,privateKey);
        SecretKey secretKey = new SecretKeySpec(keyBytes,"AES");
        return baseAes.decrypt(model.encryptedSecret,secretKey,params.iv);
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

    private static <T extends Key> T validateKey(T key){
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        String keyAlg = key.getAlgorithm();
        if (!"RSA".equals(keyAlg)) throw new IllegalArgumentException("Key algorithm must be RSA, but provided was: "+ keyAlg);
        return key;
    }

    /**
     * Class is used as model for Modular Crypt Format representation for
     * this instance algorithm output.
     */
    private static final class McfModel{
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
        public McfModel(String identifier, String version,String params, byte[] encryptedKey,byte[] encryptedSecret) {
            this.identifier = identifier;
            this.version = version;
            this.params = params;
            this.encryptedKey = encryptedKey;
            this.encryptedSecret = encryptedSecret;
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
            this.iv = iv;
        }
    }
}
