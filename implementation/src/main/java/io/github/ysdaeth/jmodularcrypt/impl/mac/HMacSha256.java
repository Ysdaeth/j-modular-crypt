package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class is wrapper for the HMac Sha256 cipher instance provided by the {@link java.security.Provider}.
 * It produces sign for a message in the Modular Crypt Format
 * {@link AbstractHMac}
 */
public final class HMacSha256 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA256";

    /**
     * Creates HMac Sha256 instance, that produces message sing in the Modular Crypt Format
     * @param secretKey secret key to create sign
     */
    public HMacSha256(SecretKey secretKey) {
        super(new BaseHMac("HmacSha256"), secretKey, IDENTIFIER);
    }

}
