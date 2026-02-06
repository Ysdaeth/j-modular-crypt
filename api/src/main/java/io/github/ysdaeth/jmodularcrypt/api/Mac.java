package io.github.ysdaeth.jmodularcrypt.api;

/**
 * Message authenticator that provides message authenticity
 * and data integrity
 */
public interface Mac {

    /**
     * Generate Generate Modular Crypt Format sign for message bytes
     * @param message bytes to be signed
     * @return Modular crypt format sign
     */
    String sign(byte[] message);

    /**
     * Verify if Generate Modular Crypt Format sign matches message
     * @param sign sign to compare
     * @param message message to verify
     * @return true if sign matches message
     */
    boolean verify(String sign, byte[] message);

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
