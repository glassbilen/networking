package me.glassbilen.network.example.client.packets;

import me.glassbilen.network.CPacketManager;
import me.glassbilen.network.example.client.packets.types.PrintPacket;
import me.glassbilen.network.example.common.packets.Heartbeat;

public class ClientPacketManager extends CPacketManager {

	public ClientPacketManager() {
		addPacket(new Heartbeat());
		addPacket(new PrintPacket());
	}

}
