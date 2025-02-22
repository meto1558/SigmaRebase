
/*

package com.mentalfrostbyte.jello.util;
import net.minecraft.network.IPacket;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Stream;
public class PacketCollectorUtil {
    public static String[] getPacketClasses(String type) {
        String[] packages = {
                "net.minecraft.network.play.client",
                "net.minecraft.network.play.server"
        };

        Set<Class<? extends Packet>> allPackets = new java.util.HashSet<>();
        for (String packageName : packages) {
            Reflections reflections = new Reflections(packageName);
            allPackets.addAll(reflections.getSubTypesOf(Packet.class));
        }

        Stream<Class<? extends Packet>> filteredPackets = getFilteredPackets(type, allPackets);

        return filteredPackets
                .map(Class::getSimpleName)
                .sorted()
                .toArray(String[]::new);
    }

    private static Stream<Class<? extends Packet>> getFilteredPackets(String type, Set<Class<? extends Packet>> allPackets) {
        private static Stream<Class<? extends Packet>> getFilteredPackets(String type, Set<Class<? extends Packet>> allPackets) {
            Stream<Class<? extends Packet>> filteredPackets = allPackets.stream();
            switch (type) {
                case "Incoming" -> {
                    filteredPackets = filteredPackets.filter(packet -> packet.getSimpleName().matches("^S[A-Za-z]+Packet$"));
                }
                case "Outgoing" -> {
                    filteredPackets = filteredPackets.filter(packet -> packet.getSimpleName().matches("^C[A-Za-z]+Packet$"));
                }
            }
            return filteredPackets;
        }

 */