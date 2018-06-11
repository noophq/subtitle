package fr.noop.subtitle.util;

public class StringUtils {
    private static final String[] BOM_CHARS = {
            "\uFEFF"
    };

    public static String removeBOM(String text) {
        for (int i=0; i<BOM_CHARS.length; i++) {
            String bomChar = BOM_CHARS[i];

            if (text.startsWith(bomChar)) {
                text = text.substring(1);

                // There is only bom char at the beginning of text
                break;
            }
        }

        return text;
    }
}
