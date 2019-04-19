package me.vrekt.origin.iq;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

import java.util.List;

public final class InviteStanza extends Stanza {

    public static final String ELEMENT = "message";
    public static final String BODY = "body";

    @Override
    public String toString() {
        return "";
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder(enclosingNamespace);
        buf.halfOpenElement(ELEMENT);
        enclosingNamespace = addCommonAttributes(buf, enclosingNamespace);
        buf.optAttribute("type", "ebisu-invite");
        buf.rightAngleBracket();

        buf.element("body", "Origin.OFR.50.0002694;JOINABLE;;;194908;;;");
        buf.element("subject", "Origin.OFR.50.0002694;JOINABLE;;;194908;;;p0_PC_1005557042551_98fdfd6378a19a3");
        buf.element("thread", "042c1fa611734030bc05968a765b1ec5");

        buf.append(List.of(new ChatStateExtension(ChatState.active)), enclosingNamespace);
        buf.closeElement(ELEMENT);
        return buf;
    }
}
