package io.github.ysdaeth.jmodularcrypt.core.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.*;
import java.util.Objects;


/**
 * Thread safe class, every encryption and decryption creates a new cipher instance.
 * Base functionality of RSA algorithm with block size matching key size.
 * Uses RSA with SHA256 and MGF1(SHA256) padding.
 */
public final class BaseRsaOaep implements BaseRsa{
    public static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * Encrypt data with public key
     * @param rawData data to be encrypted
     * @return encrypted data
     * @throws KeyException when key is invalid.
     */
    @Override
    public byte[] encrypt(byte[] rawData, PublicKey publicKey) throws KeyException{
        validateKey(publicKey);
        return cipher(rawData,publicKey);
    }

    /**
     * Decrypt specified bytes with private key
     * @param encrypted encrypted data
     * @return decrypted data
     * @throws KeyException when key does not match encrypted data
     */
    @Override
    public byte[] decrypt(byte[] encrypted, PrivateKey privateKey) throws KeyException{
        validateKey(privateKey);
        return cipher(encrypted,privateKey);
    }

    /**
     * Check if key is instance of RSA and not null
     * @param key key to check
     * @return the same key if valid
     */
    private static <T extends Key> T validateKey(T key) {
        String keyAlgorithm = key.getAlgorithm();
        if(!"RSA".equals(keyAlgorithm)){
            throw new IllegalArgumentException("Key must be instance of RSA, but provided was " +keyAlgorithm);
        }
        return Objects.requireNonNull(key);
    }


    /**
     * A {@link PublicKey} is used for encryption, while a {@link PrivateKey}
     * is used for decryption.</br>
     * @param data bytes to encrypt or decrypt
     * @param key must be a {@link PublicKey} for encryption or a {@link PrivateKey} for decryption
     * @return encrypted or decrypted bytes
     * @throws KeyException when key is invalid or does not match encrypted bytes in decrypt mode
     * @throws IllegalArgumentException if the key type is not supported.
     */
    private static byte[] cipher(byte[] data, Key key) throws KeyException{
        int cipherMode = cipherMode(key);
        byte[] deciphered;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(cipherMode,key);
            deciphered = cipher.doFinal(data);
        }catch (BadPaddingException | InvalidKeyException e){
            throw new KeyException("Key does not match encrypted data."+e.getMessage(),e);
        }catch (Exception e){
            throw new RuntimeException("Invalid configuration. "+e.getMessage(), e);
        }
        return deciphered;
    }

    /**
     * Determines the {@link Cipher} mode based on the provided key type.
     * <ul>
     *     <li>if a {@link PublicKey} is provided, {@link Cipher#ENCRYPT_MODE} is returned.</li>
     *     <li>If a {@link PrivateKey} is provided, {@link Cipher#DECRYPT_MODE} is returned.</li>
     * </ul>
     * @param key {@link PublicKey} or {@link PrivateKey}
     * @return cipher mode constant
     * @throws IllegalArgumentException if the key is not a {@link PublicKey} or {@link PrivateKey}
     */
    private static int cipherMode(Key key){
        if( key instanceof PublicKey) return Cipher.ENCRYPT_MODE;
        if( key instanceof PrivateKey) return Cipher.DECRYPT_MODE;
        throw new IllegalArgumentException(
                    "Key must be instance of PrivateKey or PublicKey, but provided was: " + key.getClass().getName());
    }

}
