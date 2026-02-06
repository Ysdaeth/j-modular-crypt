package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

public class HMacSha512 extends AbstractHMac{

    private static final String IDENTIFIER = "HMAC-SHA512";

    public HMacSha512(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha512");
        super(hMac, secretKey, IDENTIFIER);
    }

}
