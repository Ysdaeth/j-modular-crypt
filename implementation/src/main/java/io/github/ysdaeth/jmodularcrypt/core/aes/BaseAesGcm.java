package io.github.ysdaeth.jmodularcrypt.core.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyException;

/**
 * Thread safe class, every encryption and decryption create a new cipher instance
 * default tag length is 128. Class is base implementation that other modules depends on.
 * It provides base implementation of AES GCM.
 */
final class BaseAesGcm implements BaseAes{
    private static final String CIPHER_ALG ="AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;

    BaseAesGcm(){}
    /**
     * Encrypt provided bytes with AES GCM with tag length of 128 bits.
     * <b>Secret passed as an argument is not cleared after encryption, so it relies on GC
     * or manual clear.</b>
     * @param rawSecret secret to be encrypted
     * @param secretKey secret AES key that encrypt secret
     * @param initialVector random array of bytes of length suggested by NIST
     * @return encrypted byte array
     */
    @Override
    public byte[] encrypt(byte[] rawSecret, SecretKey secretKey, byte[] initialVector) throws KeyException{
        if(rawSecret == null) throw new IllegalArgumentException("Secret cannot be null");
        if(initialVector == null) throw new IllegalArgumentException("Initial vector must not be null");
        validateKey(secretKey);
        byte[] encrypted;
        try{
            Cipher cipher = Cipher.getInstance(CIPHER_ALG);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH,initialVector);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,spec);
            encrypted = cipher.doFinal(rawSecret);
        }catch (KeyException e){
            throw new KeyException(e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return encrypted;
    }

    /**
     * Decrypt provided bytes with AES GCM with tag length of 128 bits.
     * @param encrypted encrypted secret
     * @param secretKey secret key used for encryption
     * @param initialVector initial vector used for encryption
     * @return decrypted bytes
     */
    @Override
    public byte[] decrypt(byte[] encrypted,SecretKey secretKey,byte[] initialVector) throws KeyException{
        if(encrypted == null) throw new IllegalArgumentException("Encrypted cannot be null");
        if(initialVector == null) throw new IllegalArgumentException("Initial vector must not be null");
        validateKey(secretKey);
        byte[] decrypted;
        try{
            Cipher cipher = Cipher.getInstance(CIPHER_ALG);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH,initialVector);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,spec);
            decrypted = cipher.doFinal(encrypted);
        }catch (KeyException e){
            throw new KeyException(e);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    /**
     * Check if key is not null and  algorithm is AES.
     * @param secretKey secret key to be checked
     * @return the same key
     */
    private static SecretKey validateKey(SecretKey secretKey){
        if(secretKey == null) throw new IllegalArgumentException("Secret key must not be null");
        String algorithm = secretKey.getAlgorithm();
        if(!"AES".equals(algorithm)) throw new IllegalArgumentException(
                "Key algorithm must be AES but provided was" + algorithm);
        return secretKey;
    }
}
