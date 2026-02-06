package io.github.ysdaeth.jmodularcrypt.core.encryptor.aes;

/**
 * Factory for internal base AES implementations only. It provides
 * base implementations.
 */
public class BaseAesFactory {

    public static BaseAes getInstance(String identifier){
        return switch (identifier){
            case "GCM" ->new BaseAesGcm();
            default -> throw new IllegalArgumentException("No such instance: "+ identifier);
        };
    }
}
