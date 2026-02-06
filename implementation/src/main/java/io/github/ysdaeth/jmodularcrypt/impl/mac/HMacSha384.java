package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

public class HMacSha384 extends AbstractHMac {
    private static final String IDENTIFIER = "HMAC-SHA384";

    public HMacSha384(SecretKey secretKey) {
        BaseHMac hMac = new BaseHMac("HmacSha384");
        super(hMac, secretKey, IDENTIFIER);
    }
}
