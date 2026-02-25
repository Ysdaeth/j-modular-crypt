package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class is wrapper for the HMac Sha512 cipher instance provided by the {@link java.security.Provider}.
 * It produces sign for a message in the Modular Crypt Format
 * {@link AbstractHMac}
 */
public class HMacSha512 extends AbstractHMac{
    private static final String IDENTIFIER = "HMAC-SHA512";

    /**
     * Create HMac Sha 512 instance
     * @param secretKey secret key to make sign for message
     */
    public HMacSha512(SecretKey secretKey) {
        super(new BaseHMac("HmacSha512"), secretKey, IDENTIFIER);
    }

}
