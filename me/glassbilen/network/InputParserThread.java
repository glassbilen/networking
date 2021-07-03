package me.glassbilen.network;

import java.io.IOException;

public class InputParserThread extends Thread {
	private CNetworkManager networkManager;

	public InputParserThread(CNetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	public void run() {
		while (networkManager.isRunning()) {
			String line = "";

			try {
				line = networkManager.getReader().readLine();
			} catch (IOException e) {
				networkManager.setRunning(false);
				break;
			}

			if (line == null) {
				break;
			}

			if (line.length() > 0) {
				networkManager.onProcess(line);
			}
		}

		networkManager.setRunning(false);
	}
}