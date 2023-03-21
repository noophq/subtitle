package fr.noop.subtitle.ass;

import org.apache.commons.lang3.StringUtils;

public class HexBGR {
    public enum Color {
        BLACK("&H000000&", "black"),
        RED("&H0000FF&", "red"),
        GREEN("&H008000&", "green"),
        YELLOW("&H00FFFF&", "yellow"),
        BLUE("&HFF0000&", "blue"),
        MAGENTA("&HFF00FF&", "magenta"),
        CYAN("&HFFFF00&", "cyan"),
        WHITE("&HFFFFFF&", "white"),
        LIME("&H00FF00&", "lime");

        private String hex;
        private String colorName;

        Color(String hex, String colorName) {
            this.hex = hex;
            this.colorName = colorName;
        }

        public String getHexValue() {
            return this.hex;
        }

        public String getColorName() {
            return this.colorName;
        }

        public static Color getEnumFromName(String colorName) {
            for(Color v : values())
                if(v.getColorName().equalsIgnoreCase(colorName)) return v;
            throw new IllegalArgumentException();
        }

        public static Color getEnumFromHex(String hex) {
            for(Color v : values())
                if(v.getHexValue().equalsIgnoreCase(hex)) return v;
            throw new IllegalArgumentException();
        }

        public static Color parseAlphaBGR(String value) {
            String bgr = StringUtils.right(value, 6);
            for(Color v : values()) {
                if(v.getHexValue().replaceAll("&H", "").replaceAll("&", "").equalsIgnoreCase(bgr)) return v;
            }
            throw new IllegalArgumentException();
        }
    }
}
