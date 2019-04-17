package me.vrekt.origin.extension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Provider basic support for the activity extension.
 * This allows the status to be set.
 */
public final class ActivityExtension implements ExtensionElement {

    private static final String NAMESPACE = "http://jabber.org/protocol/activity";
    private final String text;

    public ActivityExtension(final String text) {
        this.text = text;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        final var xml = new XmlStringBuilder();
        xml.halfOpenElement("activity");
        xml.xmlnsAttribute(NAMESPACE).rightAngleBracket();
        xml.halfOpenElement("relaxing");
        xml.xmlnsAttribute(NAMESPACE).rightAngleBracket();
        xml.halfOpenElement("gaming");
        xml.xmlnsAttribute(NAMESPACE).closeEmptyElement();
        xml.closeElement("relaxing");
        xml.element("text", text).closeEmptyElement();
        xml.closeElement("activity");
        return xml;
    }

}