package me.glassbilen.network.example.server.packets;

import me.glassbilen.network.CPacketManager;
import me.glassbilen.network.example.common.packets.Heartbeat;
import me.glassbilen.network.example.server.packets.types.PrintPacket;

public class ServerPacketManager extends CPacketManager {
	public ServerPacketManager() {
		addPacket(new Heartbeat());
		addPacket(new PrintPacket());
	}
}
