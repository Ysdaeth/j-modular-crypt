package io.github.ysdaeth.jmodularcrypt.core.mac;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.util.Arrays;

public class BaseHMac {
    private final String algorithm;

    /**
     * Create HMac from Java provider
     * @param algorithm HMac instance algorithm
     */
    public BaseHMac(String algorithm) {
        if(algorithm == null) throw new IllegalArgumentException("Algorithm must not be null");
        this.algorithm = algorithm;
    }

    /**
     * Create sign based on message and secret key
     * @param message message to sign
     * @param secretKey secret key
     * @return sign based on message and secret key
     */
    public byte[] sign(byte[] message, SecretKey secretKey) {
        Mac mac;
        try{
            mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(message);
        }catch (Exception e){
            throw new RuntimeException("Failed to create sign." + e.getMessage(),e);
        }
    }

    /**
     * Check is sign matches message
     * @param message original message
     * @param sign sign
     * @param secretKey key used for sign
     * @return true if sign match message
     */
    public boolean verify(byte[] message, byte[] sign, SecretKey secretKey) {
        byte[] signed = sign(message,secretKey);
        return Arrays.equals(signed,sign);
    }
}
