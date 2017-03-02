
/*
 * Title: VttStyle
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a CSS parser for STYLE blocks in Webvtt files. Supports only a subset of the CSS
 * features.
 */
public class VttStyle {

    enum Token {
        IDENT("<id>"),
        NUMBER("<number>"),
        QUOTED_STR("<quoted>"),
        LPAREN("("),
        RPAREN(")"),
        SLASH("/"),
        EQ("="),
        H_LPAREN("["),
        H_RPAREN("]"),
        S_LPAREN("{"),
        S_RPAREN("}"),
        COLON(":"),
        SEMICOLON(";"),
        COMMA(","),
        PERIOD("."),
        SHARP("#"),
        STAR("*"),
        WS("<ws>"),
        EOF("<eof>"),
        ERROR("<error>");

        private final String val;

        Token(String val) {
            this.val = val;
        }

        public String getString() {
            return val;
        }
    }

    private static final String[] PSEUDO = { "cue", "cue-region" };

    private static final String PROPERTY_COLOR = "color";
    private static final String PROPERTY_BGCOLOR = "background-color";
    private static final String PROPERTY_FONT_FAMILY = "font-family";
    private static final String PROPERTY_FONT_WEIGHT = "font-weight";
    private static final String PROPERTY_TEXT_DECORATION = "text-decoration";

    private static final String VALUE_BOLD = "bold";
    private static final String VALUE_UNDERLINE = "underline";
    private static final String PROPERTY_FONT_STYLE = "font-style";
    private static final String VALUE_ITALIC = "italic";


    private final ValidationReporter reporter;
    private SubtitleReader input; // parsing position

    private final List<VttCssRule> rules;
    private VttCssRule currentRule;

    private Token token;
    private String tokenStr;


    public VttStyle(ValidationReporter reporter, VttObject vttObject) {
        this.reporter = reporter;
        rules = new ArrayList<>();
    }

    private void finishProperty(@NonNull String prop, String value) {
        // TODO: validate CSS property names and values

        switch (prop) {
            case PROPERTY_COLOR:
            case PROPERTY_BGCOLOR:
                parseCssColor(value);
                break;
            case PROPERTY_TEXT_DECORATION:
                if (VALUE_UNDERLINE.equals(value)) {
                    //style.setUnderline(true);
                }
                break;
            case PROPERTY_FONT_FAMILY:
                //style.setFontFamily(value);
                break;
            case PROPERTY_FONT_WEIGHT:
                if (VALUE_BOLD.equals(value)) {
                    //style.setBold(true);
                }
                break;
            case PROPERTY_FONT_STYLE:
                if (VALUE_ITALIC.equals(value)) {
                    //style.setItalic(true);
                }
                break;
        }

        if (currentRule != null) {
            currentRule.addProperty(prop, value);
        }
    }

    private void parseCssColor(@NonNull  String value) {
        if (value.matches("#[0-9a-fA-F]+")) {
            // hex format
        } else if ((value.startsWith("rgb(") || value.startsWith("hsl(")) && value.endsWith(")")) {
            // 3 numbers or percentages
        } else if ((value.startsWith("rgba(") || value.startsWith("hsla(")) && value.endsWith(")")) {
            // 3 numbers or percentages + alpha
        } else if (value.matches("[a-zA-Z]+")) {
            // color name
        } else {
            reporter.notifyWarning("Invalid CSS color syntax: '" + value + "'");
        }
    }


    public void parse(SubtitleReader reader) throws IOException {
        input = reader;
        style();
    }

    private void style() throws IOException {
        nextToken();
        ws();
        currentRule = new VttCssRule();

        while (selectors()) {

            ws();
            if (!props()) {
                reporter.notifyError("No properties in CSS style");
                return;
            }
            ws();
            rules.add(currentRule);
        }
        ws();
        if (!is(Token.EOF)) {
            reporter.notifyError("Unexpected symbol '" + tokenStr + "' instead of '" + Token.EOF.getString() + "'");
        }
    }

    private boolean props() throws IOException {
        if (!expect(Token.S_LPAREN)) {
            return false;
        }
        ws();
        if (!propDef()) {
            return false;
        }
        ws();
        while (propDef()) {
            ws();
        }

        return expect(Token.S_RPAREN);
    }

    private boolean propDef() throws IOException {
        String propName = tokenStr;
        if (!propName()) {
            return false;
        }
        ws();
        if (!expect(Token.COLON)) {
            return false;
        }
        ws();
        String propValue = propValue();
        if (propValue == null || propValue.isEmpty()) {
            reporter.notifyError("Expected CSS property value");
            return false;
        }
        ws();
        if (!expect(Token.SEMICOLON) && !is(Token.S_RPAREN)) {
            // neither ';' nor '}' after a prop def
            return false;
        }

        finishProperty(propName, propValue);
        return true;
    }

    private boolean propName() throws IOException {
        return accept(Token.IDENT);
    }

    private String propValue() throws IOException {
        StringBuilder bld = new StringBuilder();

        while (!is(Token.SEMICOLON) && !is(Token.S_RPAREN) && !is(Token.EOF)) {
            bld.append(tokenStr);
            nextToken();
            if (is(Token.SLASH)) {
                ws();
                reporter.notifyError("Comment inside CSS property value");
                return null;
            }
        }
        return bld.toString();
    }

    private String pseudo() throws IOException {
        if (!accept(Token.COLON)) {
            return null;
        }
        if (!expect(Token.COLON)) {
            return null;
        }
        String pseudo = tokenStr;
        if (!accept(Token.IDENT)) {
            reporter.notifyError("Expected CSS pseudo element");
            return null;
        }
        return pseudo;
    }

    private void xws() throws IOException {
        while (accept(Token.WS)) {
            // N/A
        }
    }

    private boolean ws() throws IOException {
        xws();
        if (accept(Token.SLASH) && accept(Token.STAR)) {

            // skip comment
            int c;
            do {
                do {
                    c = input.read();
                } while (c != '*' && c != -1);

                if (c == -1) {
                    reporter.notifyError("Disclosed CSS comment");
                    return false;
                }

                c = input.read();
            } while (c != '/' && c != -1);

            if (c == -1) {
                reporter.notifyError("Disclosed CSS comment");
                return false;
            }
        }
        xws();
        return true;
    }

    private boolean selectors() throws IOException {
        int i = 0;
        while (selector()) {
            i++;
            ws();
            if (!accept(Token.COMMA)) {
                break;
            }
            ws();
        }
        return i > 0;
    }

    private boolean selector() throws IOException {
        // ::cue(v#anId.class1.class2[voice="Robert"] )

        VttCssSelector.VttCssSelectorBuilder bld = VttCssSelector.builder();

        String pseudo = pseudo();
        boolean found = false;
        if (pseudo != null) {
            for (String value : PSEUDO) {
                if (value.equals(pseudo)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                reporter.notifyError("Mismatched CSS pseudo element name '" + pseudo + "'");
            }
        }
        if (!found) {
            return false;
        }

        bld.pseudo(pseudo);

        ws();
        if (accept(Token.LPAREN)) {
            int elems = 0;
            ws();
            if (pseudoSelector()) { // pseudo selector
                elems++;
            }

            String t = tokenStr;
            if (accept(Token.IDENT)) { // element
                bld.elem(t);
                elems++;
            }

            if (accept(Token.SHARP)) {
                t = tokenStr;
                if (!expect(Token.IDENT)) { // id
                    return false;
                }
                bld.id(t);
                elems++;
            }

            List<String> classes = new ArrayList<>();
            while (accept(Token.PERIOD)) { // classes
                t = tokenStr;
                if (!expect(Token.IDENT)) {
                    return false;
                }
                classes.add(t);
                elems++;
            }
            bld.classes(classes);
            ws();
            List<String> attrs = new ArrayList<>();
            while (accept(Token.H_LPAREN)) {
                String attrName = tokenStr;
                if (!expect(Token.IDENT)) {
                    return false;
                }
                ws();
                if (!expect(Token.EQ)) {
                    return false;
                }
                ws();
                String attrValue = tokenStr;
                if (!expect(Token.QUOTED_STR)) {
                    return false;
                }
                ws();
                if (!expect(Token.H_RPAREN)) {
                    return false;
                }
                ws();
                attrs.add(attrName + "=" + attrValue);
                elems++;
            }
            bld.attrs(attrs);

            ws();
            if (!expect(Token.RPAREN)) {
                return false;
            }
            else if (elems == 0) {
                reporter.notifyError("Empty CSS selector definition");
            }
        }

        if (currentRule != null) {
            currentRule.addSelector(bld.build());
        }
        return true;
    }

    private boolean pseudoSelector() throws IOException {
        // pseudo selectors like :past :lang(en)
        if (accept(Token.COLON)) {
            if (!expect(Token.IDENT)) {
                return false;
            }
            if (accept(Token.LPAREN)) {
                if (!expect(Token.IDENT)) {
                    return false;
                }
                if (!expect(Token.RPAREN)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean is(Token tok) {
        return token == tok;
    }

    private boolean accept(Token tok) throws IOException {
        if (token == tok) {
            nextToken();
            return true;
        }
        return false;
    }

    private boolean expect(Token tok) throws IOException {
        if (!accept(tok)) {
            reporter.notifyError("Unexpected symbol '" + tokenStr + "' instead of '" + tok.getString() + "'");
            return false;
        }
        return true;
    }

    private void nextToken() throws IOException {
        // depends on state
        StringBuilder bld = new StringBuilder();
        int c = input.read();
        Token tok = null;
        
        switch (c) {
            case -1:
                tok = Token.EOF;
                break;
            case '(':
                tok = Token.LPAREN;
                break;
            case ')':
                tok = Token.RPAREN;
                break;
            case '[':
                tok = Token.H_LPAREN;
                break;
            case ']':
                tok = Token.H_RPAREN;
                break;
            case '{':
                tok = Token.S_LPAREN;
                break;
            case '}':
                tok = Token.S_RPAREN;
                break;
            case ':':
                tok = Token.COLON;
                break;
            case ';':
                tok = Token.SEMICOLON;
                break;
            case '=':
                tok = Token.EQ;
                break;
            case ',':
                tok = Token.COMMA;
                break;
            case '.':
                tok = Token.PERIOD;
                break;
            case '#':
                tok = Token.SHARP;
                break;
            case '/':
                tok = Token.SLASH;
                break;
            case '*':
                tok = Token.STAR;
                break;
        }
        if (tok == null) {
            if (Character.isWhitespace(c)) {
                int nlCount = 0;
                tok = Token.WS;
                do {
                    if (c == '\n') {
                        nlCount++;
                        if (nlCount > 1) {
                            tok = Token.EOF;
                            bld.append("<eof>");
                            break; // two new lines - the rest of input does not belong to CSS block
                        }
                    }

                    c = input.lookNext();
                    if (Character.isWhitespace(c)) {
                        c = input.read();
                    } else {
                        break;
                    }
                } while (true);

            } else if (Character.isDigit(c)) {
                do {
                    bld.append((char) c);
                    c = input.lookNext();
                    if (Character.isDigit(c)) {
                        c = input.read();
                    } else {
                        break;
                    }
                } while (true);

                tok = Token.NUMBER;
            } else if (Character.isLetter(c)) {
                do {
                    bld.append((char) c);
                    c = input.lookNext();
                    if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                        c = input.read();
                    } else {
                        break;
                    }
                } while (true);

                tok = Token.IDENT;
            } else if (c == '"') {
                bld.append((char) c);
                do {
                    c = input.read();
                    if (c == -1) {
                        reporter.notifyError("expected closing quote");
                    } else {
                        bld.append((char) c);
                    }
                } while (c != -1 && c != '"');

                tok = Token.QUOTED_STR;
            } else {
                bld.append((char) c);
                tok = Token.ERROR; // unrecognized
            }
        } else {
            bld.append((char) c);
        }

        token = tok;
        tokenStr = bld.toString();
        //System.out.println("    TOKEN: " + token);
    }

    public String toString() {
        StringBuilder bld = new StringBuilder();
        if (!rules.isEmpty()) {
            bld.append(VttParser.STYLE_START);
            bld.append("\n");
            for (VttCssRule rule : rules) {
                bld.append(rule.toString());
            }
        }
        return bld.toString();
    }
}

