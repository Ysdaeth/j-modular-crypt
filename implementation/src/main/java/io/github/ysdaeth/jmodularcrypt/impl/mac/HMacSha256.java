package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

public final class HMacSha256 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA256";

    public HMacSha256(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha256");
        super(hMac, secretKey, IDENTIFIER);
    }

}
