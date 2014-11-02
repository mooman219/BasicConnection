package com.gmail.mooman219.basicconnection.server;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class ConnectionManager {

    private final int maxConnection;

    public ConnectionManager(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getMaxConnections() {
        return maxConnection;
    }
}
