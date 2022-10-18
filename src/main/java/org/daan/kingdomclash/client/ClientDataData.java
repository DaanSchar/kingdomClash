package org.daan.kingdomclash.client;

public class ClientDataData {

    private static int playerData;
    private static int chunkData;

    public static void set(int playerData, int chunkData) {
        ClientDataData.playerData = playerData;
        ClientDataData.chunkData = chunkData;
    }

    public static int getChunkData() {
        return chunkData;
    }

    public static int getPlayerData() {
        return playerData;
    }
}
