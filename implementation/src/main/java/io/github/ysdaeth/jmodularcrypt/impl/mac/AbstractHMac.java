package io.github.ysdaeth.jmodularcrypt.impl.mac;

import io.github.ysdaeth.jmodularcrypt.api.Mac;
import io.github.ysdaeth.jmodularcrypt.core.annotations.Module;
import io.github.ysdaeth.jmodularcrypt.core.annotations.SerializerCreator;
import io.github.ysdaeth.jmodularcrypt.core.serializer.Serializer;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerFactory;
import io.github.ysdaeth.jmodularcrypt.core.serializer.factory.SerializerType;
import io.github.ysdaeth.jmodularcrypt.core.mac.BaseHMac;

import javax.crypto.SecretKey;

/**
 * Common implementation for HMac instances for the Modular Crypt Format outputs.
 * Class is responsible for providing MCF layer implementation for message signing and
 * verification of message with the sign.
 */
abstract class AbstractHMac implements Mac {

    private final Serializer serializer;

    private final String identifier;
    private final BaseHMac baseHMac;
    private final SecretKey secretKey;

    /**
     * Creates shared implementation for HMac implementations
     * @param baseHMac preconfigured implementation of the HMac algorithm
     * @param secretKey secret key for the message signing and verification
     */
    AbstractHMac(BaseHMac baseHMac, SecretKey secretKey, String identifier){
        this.baseHMac = baseHMac;
        this.secretKey = secretKey;
        this.identifier = identifier;
        serializer = SerializerFactory.getInstance(SerializerType.MCF_HEXADECIMAL);
    }

    /**
     * Generates sign in the Modular Crypt Format
     * <blockquote><pre>
     *     byte[] message = new byte[]{1,2,3}
     *     Mac hmac = new HMacSha256();
     *     String sign = hmac.sign(message);
     * </pre></blockquote>
     * @param message bytes to be signed
     * @return Modular crypt format sign
     */
    @Override
    public String sign(byte[] message) {
        byte[] sign = baseHMac.sign(message,secretKey);
        McfModel model = new McfModel(
                identifier(),
                version(),
                sign);
        return serializer.serialize(model);
    }

    /**
     * Returns the version of this algorithm
     * @return version
     */
    @Override
    public String version(){
        return "v=1";
    }

    /**
     * Verifies if the generated Modular Crypt Format sign matches the message
     * <blockquote><pre>
     *     byte[] message = new byte[]{1,2,3}
     *     Mac hmac = new HMacSha256();
     *     String sign = hmac.sign(message);
     *     boolean matches = hmac.verify(sign,message);
     * </pre></blockquote>
     * @param sign sign to compare
     * @param message message to verify
     * @return true if sign matches message
     */
    @Override
    public boolean verify(String sign, byte[] message) {
        McfModel model = serializer.deserialize(sign,McfModel.class);
        return baseHMac.verify(message, model.sign, secretKey);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    private static final class McfModel{
        @Module( order = 0)
        private final String identifier;
        @Module(order = 1)
        private final String version;
        @Module(order = 2)
        private final byte[] sign;

        @SerializerCreator
        public McfModel(String identifier, String version, byte[] sign) {
            this.identifier = identifier;
            this.version = version;
            this.sign = sign;
        }
    }
}
