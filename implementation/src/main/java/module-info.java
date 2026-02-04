module io.github.ysdaeth.jmodularcrypt.implementation {
    requires io.github.ysdaeth.jmodularcrypt.api;
    requires io.github.ysdaeth.jmodularcrypt.common;
    opens io.github.ysdaeth.jmodularcrypt.impl.aes to io.github.ysdaeth.jmodularcrypt.common;
    opens io.github.ysdaeth.jmodularcrypt.impl.rsa to io.github.ysdaeth.jmodularcrypt.common;
}