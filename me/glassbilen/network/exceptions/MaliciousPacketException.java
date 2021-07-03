package me.glassbilen.network.exceptions;

public class MaliciousPacketException extends Exception {
	private String reason;

	public MaliciousPacketException(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
