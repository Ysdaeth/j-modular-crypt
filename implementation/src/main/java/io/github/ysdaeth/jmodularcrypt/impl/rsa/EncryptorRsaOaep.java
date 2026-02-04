package io.github.ysdaeth.jmodularcrypt.impl.rsa;

import io.github.ysdaeth.jmodularcrypt.core.rsa.BaseRsa;
import io.github.ysdaeth.jmodularcrypt.core.rsa.BaseRsaFactory;
import io.github.ysdaeth.jmodularcrypt.api.AsymmetricEncryptor;
import io.github.ysdaeth.jmodularcrypt.common.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.common.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.common.serializer.ConfigurableSerializer;
import io.github.ysdaeth.jmodularcrypt.common.serializer.Serializer;
import io.github.ysdaeth.jmodularcrypt.common.serializer.SerializerConfiguration;
import io.github.ysdaeth.jmodularcrypt.config.ModelSerializerConfig;

import java.security.*;
import java.util.Arrays;

/**
 * Class uses RSA OAEP SHA256 with MGF1(SHA256) padding.
 * Class purpose is to encrypt secret that are needed to be recovered for
 * service functionality, like email address. If data exceeds algorithm capacity then use
 * {@link EncryptorRsaOaepAesGcm}
 * It is designed to provide Modular Crypt Format standard output.
 * For more details see {@link AsymmetricEncryptor}
 * <p>Example</p>
 * $RSA-OAEP-SHA256-MGF1 $v=1 $encryptedValue  (without spaces)
 */
public final class EncryptorRsaOaep implements AsymmetricEncryptor {
    public static final String ID = "RSA-OAEP-SHA256-MGF1";
    private static final String VERSION = "v=1";
    private final Serializer serializer ;
    private final BaseRsa baseRsa;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Create instance of asymmetric RSA OAEP SHA256 MGF1(SHA256) padding.
     * Keys provided as an argument do not need to be a pair, that means this instance can be used
     * for key rotations when non pair keys are needed.
     * @param publicKey key for encryption
     * @param privateKey key for decryption
     */
    public EncryptorRsaOaep(PublicKey publicKey, PrivateKey privateKey){
        try{
            setKey(publicKey,privateKey);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        SerializerConfiguration configuration = new ModelSerializerConfig();
        this.serializer = new ConfigurableSerializer(configuration);
        this.baseRsa = BaseRsaFactory.getInstance("OAEP");
    }

    /**
     * Set keys for encryption and decryption. Keys do not need to be a pair.
     * When are not a pair, then key rotation can be done by the same instance, but
     * encryption and decryption using key pair is recommended.
     * @param publicKey encryption key
     * @param privateKey decryption key
     * @throws IllegalArgumentException when key algorithm does not match, or null
     */
    @Override
    public void setKey(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = validateKey(publicKey);
        this.privateKey = validateKey(privateKey);
    }

    /**
     * Encrypt secret with public key and return Modular Crypt Format string value.
     * It uses {@link Serializer} configured to match MCF format.
     * Argument passed as an argument is cloned and filled with 0 bytes after encryption,
     * but secret itself is not modified.
     * @param secret credentials to be encrypted
     * @return Modular Crypt Format string representation
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public String encrypt(byte[] secret) throws KeyException{
        byte[] credentials = secret.clone();
        try{
            byte[] encrypted = baseRsa.encrypt(credentials,publicKey);
            McfEntity mcf = new McfEntity(ID,VERSION,encrypted);
            return serializer.serialize(mcf);
        }finally {
            Arrays.fill(credentials,(byte)0);
        }
    }

    /**
     * Decrypt Modular Crypt Format string representation with private key
     * and return decrypted string. It uses {@link Serializer} configured to match
     * MCF format
     * @param serializedMcf Modular Crypt Format string with encrypted value
     * @return decrypted secret
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public byte[] decrypt(String serializedMcf) throws KeyException{
        McfEntity mcf = serializer.deserialize(serializedMcf, McfEntity.class);
        byte[] encrypted = mcf.encrypted();
        return baseRsa.decrypt(encrypted,privateKey);
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
    private static final class McfEntity{
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final byte[] encrypted;

        @SerializerCreator
        public McfEntity(String identifier, String version, byte[] encrypted) {
            this.identifier = identifier;
            this.version = version;
            this.encrypted = encrypted;
        }

        public String identifier(){
            return identifier;
        }
        public String version(){
            return version;
        }
        public byte[] encrypted(){
            return encrypted;
        }
    }

}
