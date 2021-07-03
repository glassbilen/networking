package me.glassbilen.network;

public interface Packet {
	String getPacketName();

	int getArgsWanted();

	void onReceive(CNetworkManager manager, String[] args);
}
