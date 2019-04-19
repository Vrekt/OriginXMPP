package me.vrekt.origin.extension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class CustomCapsExtension implements ExtensionElement {

    private static final String NAMESPACE = "http://jabber.org/protocol/caps";
    private static final String ELEMENT = "c";

    private final String node, ver, hash;

    public CustomCapsExtension(String node, String version, String hash) {
        this.node = node;
        this.ver = version;
        this.hash = hash;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("ver", ver).attribute("hash", hash).attribute("node", node);
        xml.closeEmptyElement();
        return xml;
    }
}
