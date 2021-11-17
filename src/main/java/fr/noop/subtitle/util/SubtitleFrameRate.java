package fr.noop.subtitle.util;

public class SubtitleFrameRate {
    public enum FrameRate {
        FR23976("23.976", 23.976f, 24000, 1001),
        FR24("24", 24, 24, 1),
        FR25("25", 25, 25, 1),
        FR2997("29.97", 29.97f, 30000, 1001),
        FR30("30", 30, 30, 1);

        private String value;
        private float frameRate;
        private int numerator;
        private int denominator;

        FrameRate(String value, float frameRate, int numerator, int denominator) {
            this.value = value;
            this.frameRate = frameRate;
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public String getValue() {
            return this.value;
        }

        public float getFrameRate() {
            return this.frameRate;
        }

        public int getFrameRateNumerator() {
            return this.numerator;
        }

        public int getFrameRateDenominator() {
            return this.denominator;
        }

        public static FrameRate getEnum(String value) {
            for(FrameRate v : values())
                if(v.getValue().equalsIgnoreCase(value)) return v;
            throw new IllegalArgumentException();
        }

        public static FrameRate getEnumFromFloat(float value) {
            for(FrameRate v : values())
                if(Math.abs(value - v.getFrameRate()) < 0.01) return v;
            throw new IllegalArgumentException();
        }
    }
}
