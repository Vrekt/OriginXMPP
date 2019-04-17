package me.vrekt.origin.extension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Provider basic support for the activity extension.
 * This allows the status to be set.
 */
public final class ActivityExtension implements ExtensionElement {

    public static final String NAMESPACE = "http://jabber.org/protocol/activity";
    public static final String ELEMENT = "activity";
    private final String text;

    public static EmbeddedExtensionProvider<ActivityExtension> provider() {
        return new Provider();
    }

    public ActivityExtension(final String text) {
        this.text = text;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    public String getText() {
        return text;
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

    private static final class Provider extends EmbeddedExtensionProvider<ActivityExtension> {
        @Override
        protected ActivityExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
            final var element = content.stream().filter(e -> e.getElementName().equals("text")).findAny().orElse(null);
            if (element == null) return new ActivityExtension(null);

            try {
                final var factory = XmlPullParserFactory.newInstance();
                final var xml = factory.newPullParser();
                xml.setInput(new StringReader(element.toXML(NAMESPACE).toString()));

                xml.next();
                return new ActivityExtension(xml.nextText());
            } catch (final XmlPullParserException | IOException exception) {
                return new ActivityExtension(null);
            }
        }
    }
}