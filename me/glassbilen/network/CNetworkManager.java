package me.glassbilen.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

import me.glassbilen.network.exceptions.MaliciousPacketException;
import me.glassbilen.network.exceptions.NoPacketHandlerException;

public abstract class CNetworkManager implements INetworkManager {
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private String encryptionKey;
	private CPacketManager packetManager;
	private boolean authenticated;

	private boolean running;
	private Thread inputThread;

	private long lastHeartbeat;
	private Thread heartbeatThread;

	public CNetworkManager(Socket socket, CPacketManager packetManager) {
		this.socket = socket;
		this.packetManager = packetManager;
	}

	public void init(boolean quitOnEnd) throws IOException {
		output = new PrintWriter(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		inputThread = new InputParserThread(this);
		heartbeatThread = new HeartbeatThread(this, quitOnEnd);
	}

	public void start() {
		running = true;
		inputThread.start();
		heartbeatThread.start();
	}

	public void sendLine(String packet, Object... args) throws IOException {
		StringBuilder lineBuilder = new StringBuilder(Base64.getEncoder().encodeToString(packet.getBytes()));

		for (Object arg : args) {
			lineBuilder.append("-" + Base64.getEncoder().encodeToString(arg.toString().getBytes()));
		}

		output.println((encryptionKey != null ? EncryptionUtils.encryptPacket(encryptionKey, lineBuilder.toString())
				: lineBuilder.toString()));
	}

	public byte[] getBytesEncrypted(byte[] array) {
		String lineToSend = new String(Base64.getEncoder().encode(array));
		return encryptionKey != null ? EncryptionUtils.encryptPacketToBytes(encryptionKey, lineToSend)
				: lineToSend.getBytes();
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getWriter() {
		return output;
	}

	public BufferedReader getReader() {
		return input;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public void close() {
		running = false;
		encryptionKey = null;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isAccepted() {
		return encryptionKey != null;
	}

	public long getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void updateLastHeartbeat() {
		lastHeartbeat = System.currentTimeMillis();
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void handleDefaultPacket(String line) throws MaliciousPacketException, NoPacketHandlerException {
		String formattedLine;

		if (getEncryptionKey() == null) {
			String encryptionKey = line;

			if (encryptionKey.endsWith("-")) {
				encryptionKey = encryptionKey.substring(0, encryptionKey.length() - 1);
			}

			setEncryptionKey(new String(Base64.getDecoder().decode(encryptionKey)));

			try {
				setAuthenticated(true);
				sendLine("Heartbeat");
			} catch (IOException e) {
			}

			onAuthenticate();
			return;
		}

		try {
			formattedLine = EncryptionUtils.decryptPacket(getEncryptionKey(), line);
		} catch (NumberFormatException e) {
			throw new MaliciousPacketException("Failed to decrypt received packet.");
		}

		if (formattedLine.endsWith("-")) {
			formattedLine = formattedLine.substring(0, formattedLine.length() - 1);
		}

		String[] args = formattedLine.split("-", 2);

		String packetName = args[0];
		String[] packetArgs = args.length == 2 ? args[1].split("-") : new String[] {};

		try {
			packetName = new String(Base64.getDecoder().decode(packetName));

			for (int i = 0; i < packetArgs.length; i++) {
				if (packetArgs[i].length() > 0) {
					packetArgs[i] = new String(Base64.getDecoder().decode(packetArgs[i]));
				}
			}
		} catch (IllegalArgumentException e) {
			throw new MaliciousPacketException("Failed to base64 decode received packet.");
		}

		boolean found = false;

		for (Packet packet : packetManager.getPackets()) {
			String name = packet.getPacketName();

			if (packetName.equalsIgnoreCase(name)) {
				if (packetArgs.length == packet.getArgsWanted()) {
					packet.onReceive(this, packetArgs);
				} else {
					throw new MaliciousPacketException(
							"Received a packet[" + name + "] with incorrect amount of arguments. Got "
									+ packetArgs.length + ", wanted " + packet.getArgsWanted() + ".");
				}
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println(packetName);
			throw new NoPacketHandlerException(packetName);
		}
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public String getIp() {
		return getSocket().getInetAddress().toString().replace("/", "");
	}
}
