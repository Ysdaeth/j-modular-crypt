package io.github.ysdaeth.jmodularcrypt.core.rsa;

/**
 * Factory for internal base RSA implementations only. It provides
 * base implementations.
 */
public final class BaseRsaFactory {
    private BaseRsaFactory(){}
    public static BaseRsa getInstance(String identifier){
        return switch (identifier){
            case "OAEP" -> new BaseRsaOaep();
            default -> throw new IllegalArgumentException("No such instance:" +identifier);
        };
    }
}
