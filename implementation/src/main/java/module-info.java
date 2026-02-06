module io.github.ysdaeth.jmodularcrypt.implementation {
    requires io.github.ysdaeth.jmodularcrypt.api;
    requires io.github.ysdaeth.jmodularcrypt.common;
    opens io.github.ysdaeth.jmodularcrypt.impl.encryptor to io.github.ysdaeth.jmodularcrypt.common;
    opens io.github.ysdaeth.jmodularcrypt.impl.mac to io.github.ysdaeth.jmodularcrypt.common;
}