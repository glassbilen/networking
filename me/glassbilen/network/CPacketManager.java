package me.glassbilen.network;

import java.util.ArrayList;
import java.util.List;

public class CPacketManager {
	private List<Packet> packets = new ArrayList<>();

	public void addPacket(Packet... packets) {
		for (Packet packet : packets) {
			getPackets().add(packet);
		}
	}

	public List<Packet> getPackets() {
		return packets;
	}
}
