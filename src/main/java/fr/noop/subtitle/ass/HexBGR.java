package fr.noop.subtitle.ass;

public class HexBGR {
    public enum Color {
        BLACK("&H000000&", "black"),
        RED("&H0000FF&", "red"),
        GREEN("&H000800&", "green"),
        YELLOW("&H00FFFF&", "yellow"),
        BLUE("&HFF0000&", "blue"),
        MAGENTA("&HFF00FF&", "magenta"),
        CYAN("&HFFFF00&", "cyan"),
        WHITE("&HFFFFFF&", "white");

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
    }
    
}
