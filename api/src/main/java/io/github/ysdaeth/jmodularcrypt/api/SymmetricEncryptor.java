package io.github.ysdaeth.jmodularcrypt.api;

import javax.crypto.SecretKey;

/**
 * Symmetric encryptor that requires single secret key to encrypt and decrypt credentials.
 * Interface is extended with {@link Encryptor}
 */
public interface SymmetricEncryptor extends Encryptor {

    /**
     * Set key that will be used for encryption and decryption
     * @param secretKey secret key for encryption and decryption
     */
    void setKey(SecretKey secretKey);
}
