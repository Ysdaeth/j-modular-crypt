package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class that is wrapper for HMac Sha 384 cipher instance.
 * It produces sign for message and verification of that sign.
 * It is designed to provide Modular Crypt Format standard output.
 */
public class HMacSha384 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA384";

    /**
     * Create HMac Sha 384 instance
     * @param secretKey secret key to make sign for message
     */
    public HMacSha384(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha384");
        super(hMac, secretKey, IDENTIFIER);
    }
}
