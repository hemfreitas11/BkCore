package me.bkrmt.bkcore.heads;

public class Head {
    private final String uuid;
    private final String playerName;
    private final String texture;
    private final long timeStamp;

    public Head(String uuid, String playerName, String texture, long timeStamp) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.texture = texture;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTexture() {
        return texture;
    }
}
