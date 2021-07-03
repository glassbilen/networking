package me.glassbilen.network.exceptions;

public class NoPacketHandlerException extends Exception {
	private String packetName;

	public NoPacketHandlerException(String packetName) {
		this.packetName = packetName;
	}

	public String getPacketName() {
		return packetName;
	}
}
