package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Thread safe class.
 * Class that is wrapper for HMac Sha 512 cipher instance.
 * It produces sign for message and verification of that sign.
 * It is designed to provide Modular Crypt Format standard output.
 */
public class HMacSha512 extends AbstractHMac{
    private static final String IDENTIFIER = "HMAC-SHA512";

    /**
     * Create HMac Sha 512 instance
     * @param secretKey secret key to make sign for message
     */
    public HMacSha512(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha512");
        super(hMac, secretKey, IDENTIFIER);
    }

}
