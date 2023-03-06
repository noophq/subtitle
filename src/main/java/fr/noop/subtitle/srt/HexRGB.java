package fr.noop.subtitle.srt;

public class HexRGB {
    public enum Color {
        BLACK("#000000", "black"),
        RED("#ff0000", "red"),
        GREEN("#008000", "green"),
        YELLOW("#ffff00", "yellow"),
        BLUE("#0000ff", "blue"),
        MAGENTA("#ff00ff", "magenta"),
        CYAN("#00ffff", "cyan"),
        WHITE("#ffffff", "white"),
        LIME("#00ff00", "lime");

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

        public static Color getEnumFromHexCode(String hexCode) {
            for(Color v : values())
                if(v.getHexValue().equalsIgnoreCase(hexCode)) return v;
            throw new IllegalArgumentException();
        }
    }
}
