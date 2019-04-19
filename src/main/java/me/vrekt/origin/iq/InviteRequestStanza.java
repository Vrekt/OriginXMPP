package me.vrekt.origin.iq;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

import java.util.List;

public final class InviteRequestStanza extends Stanza {

    public static final String ELEMENT = "message";

    @Override
    public String toString() {
        return "";
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder(enclosingNamespace);
        buf.halfOpenElement(ELEMENT);
        enclosingNamespace = addCommonAttributes(buf, enclosingNamespace);
        buf.optAttribute("type", "ebisu-request-invite");
        buf.rightAngleBracket();

        buf.element("body", "Invite");
        buf.element("thread", "7450e51e6eb0467e95845208df8ea73c");

        buf.append(List.of(new ChatStateExtension(ChatState.active)), enclosingNamespace);
        buf.closeElement(ELEMENT);
        return buf;
    }
}
