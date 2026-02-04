package io.github.ysdaeth.jmodularcrypt.api;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Symmetric encryptor that requires single secret key to encrypt and decrypt credentials.
 * Interface extends {@link Encryptor}
 */
public interface AsymmetricEncryptor extends Encryptor{

    /**
     * Set keys that will be used for encryption and decryption
     * @param publicKey encryption key
     * @param privateKey decryption key
     */
    void setKey(PublicKey publicKey, PrivateKey privateKey);
}
