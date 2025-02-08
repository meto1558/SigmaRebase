package com.mentalfrostbyte.jello.util.client;

import com.mentalfrostbyte.jello.Client;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viamcp.protocolinfo.ProtocolInfo;

import java.util.ArrayList;
import java.util.List;

import static de.florianmichael.viamcp.protocolinfo.ProtocolInfo.PROTOCOL_INFOS;

public class JelloPortal {

    public static int getCurrentVersionIndex() {
        return Client.currentVersionIndex;
    }

    private static List<ProtocolVersion> getAvailableVersions() {
        ArrayList<ProtocolVersion> availableVersions = new ArrayList<>();

        for (ProtocolInfo version : PROTOCOL_INFOS) {
            availableVersions.add(version.getProtocolVersion());
        }

        return availableVersions;
    }

    public static ProtocolVersion getVersion() {
        int index = getCurrentVersionIndex();
        List<ProtocolVersion> availableVersions = getAvailableVersions();
        if (index < 0 || index >= availableVersions.size()) {
            return ProtocolInfo.R1_16_4.getProtocolVersion(); // Fallback version
        }
        return availableVersions.get(index);
    }

}
