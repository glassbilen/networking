package me.glassbilen.network.example.client.packets.types;

import me.glassbilen.network.CNetworkManager;
import me.glassbilen.network.Packet;

public class PrintPacket implements Packet {

	@Override
	public String getPacketName() {
		return "Print";
	}

	@Override
	public int getArgsWanted() {
		return 1;
	}

	@Override
	public void onReceive(CNetworkManager manager, String[] args) {
		String message = args[0];

		System.out.println(message);
	}

}
