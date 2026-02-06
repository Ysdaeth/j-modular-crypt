package io.github.ysdaeth.jmodularcrypt.core.encryptor.aes;

import javax.crypto.SecretKey;
import java.security.KeyException;

/**
 * interface for internal base AES implementation.
 */
public interface BaseAes {
    byte[] encrypt(byte[] rawSecret, SecretKey secretKey, byte[] initialVector) throws KeyException;
    byte[] decrypt(byte[] encrypted,SecretKey secretKey, byte[] initialVector) throws KeyException;
}
