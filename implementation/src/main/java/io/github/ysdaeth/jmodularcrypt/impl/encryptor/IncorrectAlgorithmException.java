package io.github.ysdaeth.jmodularcrypt.impl.encryptor;

public class IncorrectAlgorithmException extends RuntimeException {
    public IncorrectAlgorithmException(String message) {
        super(message);
    }

    public IncorrectAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

}
