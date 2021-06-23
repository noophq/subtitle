package fr.noop.subtitle.util;

public class SubtitleFrameRate {
    public enum FrameRate {
        FR23976("23.976", 23.976f),
        FR24("24", 24),
        FR25("25", 25),
        FR2997("29.97", 29.97f),
        FR30("30", 30);

        private String value;
        private float frameRate;

        FrameRate(String value, float frameRate) {
            this.value = value;
            this.frameRate = frameRate;
        }

        public String getValue() {
            return this.value;
        }

        public float getFrameRate() {
            return this.frameRate;
        }

        public static FrameRate getEnum(String value) {
            for(FrameRate v : values())
                if(v.getValue().equalsIgnoreCase(value)) return v;
            throw new IllegalArgumentException();
        }
    }
}
