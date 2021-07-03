package me.glassbilen.network;

import java.io.IOException;

public class HeartbeatThread extends Thread {
	private static final long HEARTBEAT_INTERVAL = 30L * 1000L;
	private static final int MAX_FAILS = 2;

	private CNetworkManager networkManager;
	private long lastHeartbeat;
	private int failedStreak;

	private boolean quitOnEnd;

	public HeartbeatThread(CNetworkManager networkManager, boolean quitOnEnd) {
		this.networkManager = networkManager;
		networkManager.updateLastHeartbeat();
		lastHeartbeat = -1;
		failedStreak = 0;
		this.quitOnEnd = quitOnEnd;
	}

	@Override
	public void run() {
		while (networkManager.isRunning()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			if (networkManager.getEncryptionKey() == null) {
				continue;
			}

			if (networkManager.getSocket().isClosed()) {
				break;
			}

			long time = System.currentTimeMillis();

			if (failedStreak >= MAX_FAILS
					|| time - networkManager.getLastHeartbeat() > MAX_FAILS * HEARTBEAT_INTERVAL) {
				break;
			}

			if (lastHeartbeat == -1 || time - lastHeartbeat > HEARTBEAT_INTERVAL) {
				try {
					networkManager.sendLine("Heartbeat");
					failedStreak = 0;
				} catch (IOException e) {
					failedStreak++;
				}

				lastHeartbeat = time;
			}
		}

		networkManager.onClose();

		if (quitOnEnd) {
			System.exit(0);
		} else {
			if (networkManager != null && networkManager.isRunning()) {
				networkManager.close();
			}
		}
	}
}
