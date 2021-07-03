package me.glassbilen.network;

public interface INetworkManager {
	void onProcess(String line);

	void onAuthenticate();

	void onClose();
}
