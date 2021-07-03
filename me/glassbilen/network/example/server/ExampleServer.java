package me.glassbilen.network.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.glassbilen.network.CPacketManager;
import me.glassbilen.network.example.server.packets.ServerPacketManager;

public class ExampleServer {
	public static boolean RUNNING = true;

	public static final int PORT = 3000;

	private List<UserSocket> users;
	private CPacketManager packetManager;
	private ServerSocket serverSocket;

	public ExampleServer() {
		users = new ArrayList<>();
		packetManager = new ServerPacketManager();

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Running server at port " + PORT + ".");

		while (RUNNING) {
			Socket socket = null;

			try {
				socket = serverSocket.accept();

				UserSocket user = new UserSocket(socket, packetManager);
				users.add(user);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		new ExampleServer();
	}
}
