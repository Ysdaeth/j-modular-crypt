package io.github.ysdaeth.jmodularcrypt.api;

import java.security.KeyException;

/**
 * Interface responsible for encrypting and decrypting data
 * using cryptographic algorithms that produce output in
 * Modular Crypt Format (MCF). Purpose of implementation is to encrypt
 * user data, that is required to be reused for service functionality.
 */
public interface Encryptor {

    /**
     * Encrypt given character sequence and return its representation
     * in Modular Crypt Format
     * @param charSequence credentials to be encrypted
     * @return Modular Crypt Format string value
     * @throws KeyException when key is invalid, missing or not recognized
     */
    String encrypt(byte[] charSequence) throws KeyException;

    /**
     * Decrypt given Modular Crypt Format string representation
     * and return decrypted value
     * @param mcf Modular Crypt Format string produced by the algorithm.
     * @return decrypted credentials
     * @throws KeyException when key is invalid, missing or not recognized
     */
    byte[] decrypt(String mcf) throws KeyException;

    /**\
     * Returns the cryptographic algorithm identifier.
     * Identifier should be first section of Modular Crypt Format
     * @return algorithm identifier
     */
    String identifier();

    /**
     * Return version of the encryptor
     * @return version
     */
    String version();
}
