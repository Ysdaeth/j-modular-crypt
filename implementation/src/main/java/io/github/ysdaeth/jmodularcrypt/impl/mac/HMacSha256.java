package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class that is wrapper for HMac Sha 256 cipher instance.
 * It produces sign for message and verification of that sign.
 * It is designed to provide Modular Crypt Format standard output.
 */
public final class HMacSha256 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA256";

    /**
     * Create HMac Sha 256 instance
     * @param secretKey secret key to make sign for message
     */
    public HMacSha256(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha256");
        super(hMac, secretKey, IDENTIFIER);
    }

}
