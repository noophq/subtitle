package fr.noop.subtitle.ass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitlePlainText;
import fr.noop.subtitle.util.SubtitleRegion;
import fr.noop.subtitle.util.SubtitleStyle;
import fr.noop.subtitle.util.SubtitleStyle.Effect;
import fr.noop.subtitle.util.SubtitleStyle.FontStyle;
import fr.noop.subtitle.util.SubtitleStyle.FontWeight;
import fr.noop.subtitle.util.SubtitleStyle.TextAlign;
import fr.noop.subtitle.util.SubtitleStyle.TextDecoration;
import fr.noop.subtitle.util.SubtitleStyledText;
import fr.noop.subtitle.util.SubtitleTextLine;
import fr.noop.subtitle.util.SubtitleTimeCode;
import fr.noop.subtitle.util.SubtitleRegion.VerticalAlign;


public class AssParser implements SubtitleParser {

    private enum CursorStatus {
        NONE,
        SCRIPT_INFO,
        STYLES,
        EVENTS;
    }

    private String charset;

    public AssParser(String charset) {
        this.charset = charset;
    }

    @Override
    public AssObject parse(InputStream is) throws IOException, SubtitleParsingException {
        return parse(is, true);
    }

    @Override
    public AssObject parse(InputStream is, boolean strict) throws IOException, SubtitleParsingException {
        AssObject assObject = new AssObject();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
        String line = "";
        CursorStatus cursorStatus = CursorStatus.NONE;

        List<String> stylesFormat = new ArrayList<>();
        List<String> dialoguesFormat = new ArrayList<>();
        Map<String, SubtitleStyle> styles = new HashMap<>();
        int resX = 1920;
        int resY = 1080;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (cursorStatus == CursorStatus.NONE && line.equalsIgnoreCase("[Script info]")) {
                cursorStatus = CursorStatus.SCRIPT_INFO;
                continue;
            }
            if (cursorStatus == CursorStatus.SCRIPT_INFO) {
                if (line.startsWith("Title:")) {
                    if (line.split(":").length > 1) {
                        assObject.setProperty(SubtitleObject.Property.TITLE, line.split(":")[1].trim());
                    }
                }
                if (line.startsWith("Original Script:")) {
                    if (line.split(":").length > 1) {
                        assObject.setProperty(SubtitleObject.Property.COPYRIGHT, line.split(":")[1].trim());
                    }
                }
                if (line.startsWith("ScriptType:")) {
                    if (line.split(":").length > 1) {
                        String version = line.split(":")[1].trim();
                        if (!version.equalsIgnoreCase("v4.00+") && !version.equalsIgnoreCase("v4.00")) {
                            System.out.println("Script version is older than 4.00, it may produce parsing errors.");
                        }
                    } else {
                        System.out.println("Script version should be set, it may produce parsing errors.");
                    }
                }
                if (line.startsWith("PlayResX:")) {
                    if (line.split(":").length > 1) {
                        resX = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                if (line.startsWith("PlayResY:")) {
                    if (line.split(":").length > 1) {
                        resY = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                if (line.equalsIgnoreCase("[v4 Styles]") ||
                    line.equalsIgnoreCase("[v4 Styles+]") ||
                    line.equalsIgnoreCase("[v4+ Styles]")
                ) {
                    cursorStatus = CursorStatus.STYLES;
                }
                continue;
            }
            if (cursorStatus == CursorStatus.STYLES) {
                if (line.startsWith("Format:")) {
                    if (line.split(":").length > 1) {
                        stylesFormat = Arrays.asList(line.split(":")[1].replaceAll("\\s", "").split(","));
                    } else {
                        System.err.println("Unable to parse styles format.");
                        System.exit(1);
                    }
                }
                if (line.startsWith("Style:")) {
                    if (line.split(":").length > 1) {
                        List<String> style = Arrays.asList(line.split(":")[1].replaceAll("\\s", "").split(",", stylesFormat.size()));
                        SubtitleStyle subtitleStyle = new SubtitleStyle();

                        if (stylesFormat.contains("PrimaryColour")) {
                            int index = stylesFormat.indexOf("PrimaryColour");
                            HexBGR.Color color = HexBGR.Color.parseAlphaBGR(style.get(index));
                            subtitleStyle.setColor(color.getColorName());
                        }
                        if (stylesFormat.contains("Bold")) {
                            int index = stylesFormat.indexOf("Bold");
                            int bold = Integer.parseInt(style.get(index));
                            if (bold == 1) {
                                subtitleStyle.setFontWeight(FontWeight.BOLD);
                            }
                        }
                        if (stylesFormat.contains("Italic")) {
                            int index = stylesFormat.indexOf("Italic");
                            int italic = Integer.parseInt(style.get(index));
                            if (italic == 1) {
                                subtitleStyle.setFontStyle(FontStyle.ITALIC);
                            }
                        }
                        if (stylesFormat.contains("Underline")) {
                            int index = stylesFormat.indexOf("Underline");
                            int underline = Integer.parseInt(style.get(index));
                            if (underline == 1) {
                                subtitleStyle.setTextDecoration(TextDecoration.UNDERLINE);
                            }
                        }
                        if (stylesFormat.contains("StrikeOut")) {
                            int index = stylesFormat.indexOf("StrikeOut");
                            int strikeout = Integer.parseInt(style.get(index));
                            if (strikeout == 1) {
                                subtitleStyle.setTextDecoration(TextDecoration.LINE_THROUGH);
                            }
                        }
                        if (stylesFormat.contains("BorderStyle")) {
                            int index = stylesFormat.indexOf("BorderStyle");
                            int border = Integer.parseInt(style.get(index));
                            if (border == 3) {
                                subtitleStyle.setEffect(Effect.BOX);
                            }
                        }
                        if (stylesFormat.contains("Alignment")) {
                            int index = stylesFormat.indexOf("Alignment");
                            int alignment = Integer.parseInt(style.get(index));
                            switch (alignment) {
                                case 2:
                                case 5:
                                case 8:
                                    subtitleStyle.setTextAlign(TextAlign.CENTER);
                                    break;
                                case 1:
                                case 4:
                                case 7:
                                    subtitleStyle.setTextAlign(TextAlign.LEFT);
                                    break;
                                case 3:
                                case 6:
                                case 9:
                                    subtitleStyle.setTextAlign(TextAlign.RIGHT);
                                    break;
                            }
                        }

                        if (stylesFormat.contains("Name")) {
                            int index = stylesFormat.indexOf("Name");
                            String name = style.get(index);
                            styles.put(name, subtitleStyle);
                        }
                    } else {
                        System.err.println("Unable to parse style.");
                        System.exit(1);
                    }
                }

                if (line.equalsIgnoreCase("[Events]")) {
                    cursorStatus = CursorStatus.EVENTS;
                }
                continue;
            }
            if (cursorStatus == CursorStatus.EVENTS) {
                if (line.startsWith("Format:")) {
                    if (line.split(":").length > 1) {
                        dialoguesFormat = Arrays.asList(line.split(":")[1].replaceAll("\\s", "").split(","));
                    } else {
                        System.err.println("Unable to parse dialogues format.");
                        System.exit(1);
                    }
                }
                if (line.startsWith("Dialogue:")) {
                    AssCue cue = new AssCue();
                    SubtitleRegion region = new SubtitleRegion(0, 0);
                    String nameFormat = null;

                    if (line.split(":").length > 1) {
                        List<String> dialogue = Arrays.asList(line.split(":", 2)[1].trim().split(",", dialoguesFormat.size()));
                        if (dialoguesFormat.contains("Start")) {
                            int index = dialoguesFormat.indexOf("Start");
                            String startTC = dialogue.get(index);
                            cue.setStartTime(SubtitleTimeCode.parseSingleHourTimeCode(startTC));
                        }
                        if (dialoguesFormat.contains("End")) {
                            int index = dialoguesFormat.indexOf("End");
                            String endTC = dialogue.get(index);
                            cue.setEndTime(SubtitleTimeCode.parseSingleHourTimeCode(endTC));
                        }
                        if (dialoguesFormat.contains("Style")) {
                            int index = dialoguesFormat.indexOf("Style");
                            nameFormat = dialogue.get(index);
                        }
                        if (dialoguesFormat.contains("MarginV")) {
                            int index = dialoguesFormat.indexOf("MarginV");
                            int verticalPosition = Integer.parseInt(dialogue.get(index));
                            region.setVerticalPosition(verticalPosition);
                            if (verticalPosition == 1) {
                                region.setVerticalAlign(VerticalAlign.TOP);
                            }
                        }
                        if (dialoguesFormat.contains("Text")) {
                            int index = dialoguesFormat.indexOf("Text");
                            String text = dialogue.get(index);
                            boolean italic = false;
                            boolean bold = false;
                            boolean underline = false;
                            boolean color = false;
                            String hexCode = null;

                            for (String textPart: text.split("\\\\N")) {
                                SubtitleTextLine textLine = new SubtitleTextLine();
                                SubtitleStyle textStyle = new SubtitleStyle(styles.get(nameFormat));

                                if (textPart.contains("{\\pos")) {
                                    Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
                                    Matcher matcher = pattern.matcher(textPart);
                                    if (matcher.find()) {
                                        String posXY = matcher.group();
                                        int posX = Integer.parseInt(posXY.split(",", 2)[0]);
                                        int posY = Integer.parseInt(posXY.split(",", 2)[1]);
                                        if (posX <= resX * 1 / 3) {
                                            textStyle.setTextAlign(TextAlign.LEFT);
                                        } else if (posX >= resX * 2 / 3) {
                                            textStyle.setTextAlign(TextAlign.RIGHT);
                                        } else {
                                            textStyle.setTextAlign(TextAlign.CENTER);
                                        }
                                        if (posY <= resY * 2 / 3) {
                                            region.setVerticalAlign(VerticalAlign.TOP);
                                        }
                                    }
                                    textPart = textPart.replaceAll("\\{\\\\pos\\([^)]*\\)\\}", "");
                                }
                                if (textPart.contains("{\\an8}")) {
                                    region.setVerticalAlign(VerticalAlign.TOP);
                                    textPart = textPart.replaceAll("\\{\\\\an8\\}", "");
                                }

                                if (textPart.contains("{\\c")) {
                                    color = true;
                                    Pattern pattern = Pattern.compile("&H(?:[A-F\\d]{3}){1,2}\\b&");
                                    Matcher matcher = pattern.matcher(textPart);
                                    if (matcher.find()) {
                                        hexCode = matcher.group();
                                    }
                                    textPart = textPart.replaceAll("\\{\\\\c&H(?:[A-F\\d]{3}){1,2}\\b&\\}", "");
                                }
                                if (color && hexCode != null) {
                                    textStyle.setColor(HexBGR.Color.getEnumFromHex(hexCode).getColorName());
                                }

                                if (textPart.contains("{\\i1}")) {
                                    italic = true;
                                    textPart = textPart.replaceAll("\\{\\\\i1\\}", "");
                                }
                                if (textPart.contains("{\\i0}")) {
                                    italic = false;
                                    textPart = textPart.replaceAll("\\{\\\\i0\\}", "");
                                }
                                if (italic) {
                                    textStyle.setFontStyle(FontStyle.ITALIC);
                                }
                                if (textPart.contains("{\\b1}")) {
                                    bold = true;
                                    textPart = textPart.replaceAll("\\{\\\\b1\\}", "");
                                }
                                if (textPart.contains("{\\b0}")) {
                                    bold = false;
                                    textPart = textPart.replaceAll("\\{\\\\b0\\}", "");
                                }
                                if (bold) {
                                    textStyle.setFontWeight(FontWeight.BOLD);
                                }
                                if (textPart.contains("{\\u1}")) {
                                    underline = true;
                                    textPart = textPart.replaceAll("\\{\\\\u1\\}", "");
                                }
                                if (textPart.contains("{\\u0}")) {
                                    underline = false;
                                    textPart = textPart.replaceAll("\\{\\\\u0\\}", "");
                                }
                                if (underline) {
                                    textStyle.setTextDecoration(TextDecoration.UNDERLINE);
                                }

                                if (textStyle.hasProperties()) {
                                    textLine.addText(new SubtitleStyledText(textPart, textStyle));
                                } else {
                                    textLine.addText(new SubtitlePlainText(textPart));
                                }
                                cue.addLine(textLine);
                            }
                                cue.setRegion(region);
                                assObject.addCue(cue);
                        }
                    } else {
                        System.err.println("Unable to parse dialogue.");
                        System.exit(1);
                    }
                }
                continue;
            }
        }

        return assObject;
    }
}
