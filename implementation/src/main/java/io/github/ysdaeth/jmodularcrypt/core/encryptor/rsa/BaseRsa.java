package io.github.ysdaeth.jmodularcrypt.core.encryptor.rsa;

import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * interface for internal base RSA implementation.
 */
public interface BaseRsa {
    byte[] encrypt(byte[] rawData, PublicKey publicKey) throws KeyException;
    byte[] decrypt(byte[] encrypted, PrivateKey privateKey) throws KeyException;
}
