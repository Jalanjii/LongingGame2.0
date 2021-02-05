package dk.brams.android.myfirstgame;

public class Sound {

    private String mName;
    private String mAssetPath;
    private Integer mSoundId;
    private Integer mStreamId;

    public Sound(String assetPath) {
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String fileName = components[components.length-1];
        mName= fileName.replace(".mp3", "");
    }


    public String getName() {
        return mName;
    }

    public String getAssetPath() {

        return mAssetPath;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }

    public Integer getStreamId() {
        return mStreamId;
    }

    public void setStreamId(Integer streamId) {
        mStreamId = streamId;
    }
}

