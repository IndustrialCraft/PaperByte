package com.github.industrialcraft.paperbyte.server;

import org.pf4j.Plugin;

public class Logger {
    private static final String ANSI_RESET = "\u001B[0m";

    private final String source;
    public Logger(GameServer gameServer){
        this.source = "PaperByte";
    }
    public Logger(Plugin plugin){
        this.source = plugin.getWrapper().getPluginId();
    }
    public void info(String message, Object... data){
        System.out.println("[INFO](" + source + ")" + String.format(message, data));
    }
    public void warn(String message, Object... data){
        System.out.println("\u001B[33m" + "[WARN](" + source + ")" + String.format(message, data) + ANSI_RESET);
    }
    public void error(String message, Object... data){
        System.out.println("\u001B[31m" + "[ERROR](" + source + ")" + String.format(message, data) + ANSI_RESET);
    }
    public void fatal(String message, Object... data){
        System.out.println("\u001B[41m" + "\u001B[30m" + "[FATAL](" + source + ")" + String.format(message, data) + ANSI_RESET);
        System.exit(0);
    }
}
