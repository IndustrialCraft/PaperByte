package com.github.industrialcraft.paperbyte.server;

/** Launches the server application. */
public class ServerLauncher {
	public static void main(String[] args) {
		if(args.length != 1)
			throw new IllegalArgumentException("Input port not specified");
		new GameServer(Integer.parseInt(args[0])).run();
	}
}