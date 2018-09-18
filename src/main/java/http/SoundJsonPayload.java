package http;

public class SoundJsonPayload {
    private String sound = null;
    private String top_num = null;

    public SoundJsonPayload(String sound, String topNum) {
        this.sound = sound;
        this.top_num = topNum;
    }

    public String getSound() {
        return sound;
    }

    public String getTop_num() {
        return top_num;
    }
}
