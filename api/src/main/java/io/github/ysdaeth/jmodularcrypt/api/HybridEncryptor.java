package io.github.ysdaeth.jmodularcrypt.api;

/**
 * Hybrid encryptor that combines asymmetric with a symmetric encryptor, that follows
 * Modular Crypt Format (MCF)
 * <p>How it works:</p>
 * <ol>
 *   <li>A random symmetric secret key is generated.</li>
 *   <li>The actual credentials are encrypted using the symmetric encryptor (e.g., AES-GCM)
 *       with this secret key.</li>
 *   <li>The symmetric secret key is then encrypted using asymmetric algorithm public key</li>
 *   <li>The final output combines the encrypted symmetric secret key and the symmetrically encrypted
 *       credentials.</li>
 * </ol>
 *
 * <p> Output format </p>
 * {@code $identifier$version$base64(encrypted-secret-key)$base64(symmetrically-encrypted-credentials)}
 * <p>Example</p>
 * $RSA-OAEP-SHA256-MGF1+AES-GCM-256 $v=1 $iv=abc,tLen=128 $encryptedKey $encryptedValue  (without spaces)
 */
public interface HybridEncryptor extends AsymmetricEncryptor{
}
