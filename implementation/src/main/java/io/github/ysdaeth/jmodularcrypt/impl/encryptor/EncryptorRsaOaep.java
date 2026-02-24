package io.github.ysdaeth.jmodularcrypt.impl.encryptor;

import io.github.ysdaeth.jmodularcrypt.api.Encryptor;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerFactory;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerType;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsa;
import io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa.BaseRsaFactory;
import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.core.serializer.Serializer;

import java.security.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h2>RSA encryptor</h2>
 * Class uses RSA OAEP SHA256 with MGF1 padding provided by the {@link java.security.Provider}
 * Class purpose is to encrypt data and return it in a Modular Crypt Format standard output.
 * If data exceeds algorithm capacity then use hybrid {@link EncryptorRsaOaepAesGcm}
 * For more details see {@link Encryptor}
 * <p>Example Modular Crypt Output Format</p>
 * $RSA-OAEP-SHA256-MGF1$v=1$encryptedBytesBase64
 */
public final class EncryptorRsaOaep implements Encryptor {
    public static final String IDENTIFIER = "RSA-OAEP-SHA256-MGF1";
    private static final String VERSION = "v=1";
    private final Serializer serializer ;
    private final BaseRsa baseRsa;

    /**
     * Creates an instance of the asymmetric RSA OAEP SHA256 MGF1 padding, provided
     * by the {@link java.security.Provider} and implemented with a basic configuration.
     */
    public EncryptorRsaOaep(){
        this.serializer = SerializerFactory.getInstance(SerializerType.MCF_BASE64);
        this.baseRsa = BaseRsaFactory.getInstance("OAEP");
    }

    /**
     * Encrypts data with provided key and returns encrypted data in Modular Crypt Format.
     * Key must be an instance of the {@link PublicKey}
     * Does not modify the original byte array.
     * <blockquote><pre>
     *     PublicKey pubKey = // get a public key;
     *     byte[] secret = new byte[]{1,2,3};
     *     Encryptor rsa = new EncryptorRsaOaep();
     *     String mcf = rsa.encrypt(secret, secretKey);
     * </pre></blockquote>
     * @param data Secret to be encrypted
     * @return encrypted data as a Modular Crypt Format string representation.
     * @param encryptionKey instance of the {@link PublicKey}
     * @throws KeyException when key does not match or is invalid
     */
    @Override
    public String encrypt(byte[] data, Key encryptionKey) throws KeyException{
        if(encryptionKey == null)
            throw new IllegalArgumentException("Encryption key must not be null");

        if(! (encryptionKey instanceof PublicKey castedPublicKey))
            throw new IllegalArgumentException("Encryption key must be instance of the "+ PublicKey.class);

        byte[] rawBytes = data.clone();
        try{
            byte[] encrypted = baseRsa.encrypt(rawBytes,castedPublicKey);
            RsaMcfEntity mcf = new RsaMcfEntity(IDENTIFIER,VERSION,encrypted);
            return serializer.serialize(mcf);
        }finally {
            Arrays.fill(rawBytes,(byte)0);
        }
    }

    /**
     * Decrypts encrypted data stored in Modular Crypt Format string representation,
     * and returns decrypted data as the bytes array. Provided key must
     * be an instance of the {@link PrivateKey}
     * <blockquote><pre>
     *     PrivateKey privKey = // get a private key;
     *     String mcf = "$RSA-OAEP-SHA256-MGF1$v=1$encryptedBytesBase64";
     *     Encryptor rsa = new EncryptorRsaOaep();
     *     byte[] secret = rsa.decrypt(mcf,privKey);
     * </pre></blockquote>
     * @param serializedMcf Modular Crypt Format string with encrypted value
     * @param decryptionKey instance of the {@link PrivateKey}
     * @return decrypted data as bytes array
     * @throws KeyException when key does not match the encrypted data or is invalid
     */
    @Override
    public byte[] decrypt(String serializedMcf, Key decryptionKey) throws KeyException{
        if(decryptionKey == null) {
            throw new IllegalArgumentException("Decryption key must not be null");
        }

        if(!(decryptionKey instanceof PrivateKey castedPrivateKey)) {
            throw new IllegalArgumentException("Decryption key must be an instance of the " + PrivateKey.class);
        }

        RsaMcfEntity model = serializer.deserialize(serializedMcf, RsaMcfEntity.class);
        if(!IDENTIFIER.equals(model.identifier)){
            throw new IncorrectAlgorithmException(String.format(
                    "Incorrect algorithm. Required is '%s' but provided was '%s'.", IDENTIFIER, model.identifier)
            );
        }

        byte[] encrypted = model.encrypted();
        return baseRsa.decrypt(encrypted,castedPrivateKey);
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
     * Class is used as model for Modular Crypt Format representation
     */
    private static final class RsaMcfEntity {
        @Module(order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final byte[] encrypted;

        @SerializerCreator
        public RsaMcfEntity(String identifier, String version, byte[] encrypted) {
            this.identifier = Objects.requireNonNull(identifier,"Identifier must not be null");
            this.version = Objects.requireNonNull(version,"Version must not be null");
            this.encrypted = Objects.requireNonNull(encrypted,"Encrypted data must not be null");
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
