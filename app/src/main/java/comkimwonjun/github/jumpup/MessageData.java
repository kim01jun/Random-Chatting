package comkimwonjun.github.jumpup;

/**
 * JumpUp
 * Created by KimWonJun on 7/24/2018.
 */
public class MessageData {
    private int type;
    private String uuid;
    private String message;

    public MessageData() {
    }

    public MessageData(int type, String uuid, String message) {
        this.type = type;
        this.uuid = uuid;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
