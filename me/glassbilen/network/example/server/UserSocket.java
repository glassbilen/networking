package me.glassbilen.network.example.server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import me.glassbilen.network.CNetworkManager;
import me.glassbilen.network.CPacketManager;
import me.glassbilen.network.exceptions.MaliciousPacketException;
import me.glassbilen.network.exceptions.NoPacketHandlerException;

public class UserSocket extends CNetworkManager {
	public UserSocket(Socket socket, CPacketManager packetManager) {
		super(socket, packetManager);

		try {
			init(false);
			start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String encryptionKey = UUID.randomUUID().toString().replace("-", "");

		try {
			sendLine(encryptionKey);
			setEncryptionKey(encryptionKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProcess(String input) {
		if (!isAccepted()) {
			System.out.println("Packet rejected since it hasnt been verified yet[" + input + "].");
			return;
		}

		try {
			handleDefaultPacket(input);
		} catch (MaliciousPacketException | NoPacketHandlerException e) {
			e.printStackTrace();
		}
	}

	// Delayed
	@Override
	public void onAuthenticate() {}

	@Override
	public void onClose() {}

	public String getIdentifier() {
		return getIp();
	}
}