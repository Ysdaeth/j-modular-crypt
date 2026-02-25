package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class is wrapper for the HMac Sha384 cipher instance provided by the {@link java.security.Provider}.
 * It produces sign for a message in the Modular Crypt Format
 * {@link AbstractHMac}
 */
public class HMacSha384 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA384";

    /**
     * Create HMac Sha 384 instance
     * @param secretKey secret key to make sign for message
     */
    public HMacSha384(SecretKey secretKey) {
        super(new BaseHMac("HmacSha384"), secretKey, IDENTIFIER);
    }
}
