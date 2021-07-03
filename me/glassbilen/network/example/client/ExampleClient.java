package me.glassbilen.network.example.client;

import java.io.IOException;
import java.net.Socket;

import me.glassbilen.network.CNetworkManager;
import me.glassbilen.network.CPacketManager;
import me.glassbilen.network.example.client.packets.ClientPacketManager;
import me.glassbilen.network.exceptions.MaliciousPacketException;
import me.glassbilen.network.exceptions.NoPacketHandlerException;

public class ExampleClient {
	public static final String SERVER_IP = "127.0.0.1";
	public static final int SERVER_PORT = 3000;

	private Socket socket;
	private CPacketManager packetManager;
	private CNetworkManager networkManager;

	public ExampleClient() {
		packetManager = new ClientPacketManager();

		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		networkManager = new CNetworkManager(socket, packetManager) {
			@Override
			public void onProcess(String line) {
				if (!networkManager.isRunning()) {
					return;
				}

				try {
					networkManager.handleDefaultPacket(line);
				} catch (MaliciousPacketException | NoPacketHandlerException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAuthenticate() {
				System.out.println("Authenticated.");

				try {
					networkManager.sendLine("Print", "Sent from server! Im authenticated now.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onClose() {
				System.out.println("Closed.");
			}
		};

		try {
			networkManager.init(true);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		networkManager.start();
	}

	public static void main(String[] args) {
		new ExampleClient();
	}
}
