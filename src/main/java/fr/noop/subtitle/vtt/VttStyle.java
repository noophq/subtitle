/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.ValidationReporter;
import fr.noop.subtitle.util.SubtitleReader;

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

        private String val;

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


    private ValidationReporter reporter;
    private SubtitleReader input; // parsing position

    private final List<VttCssRule> rules;
    private VttCssRule currentRule;

    private Token token;
    private String tokenStr;


    public VttStyle(ValidationReporter reporter, VttObject vttObject) {
        this.reporter = reporter;
        rules = new ArrayList<>();
    }

    private void finishProperty(String prop, String value) {
        // FIXME - validate CSS property names and values

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

    private void parseCssColor(String value) {
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
                reporter.notifyError("no properties");
                return;
            }
            ws();
            rules.add(currentRule);
        }
        ws();
        if (!is(Token.EOF)) {
            reporter.notifyError("unexpected symbol '" + tokenStr + "' instead of '" + token.EOF.getString() + "'");
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
            reporter.notifyError("expected prop-value");
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
                reporter.notifyError("Comment inside property value");
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
            reporter.notifyError("expected identifier");
            return null;
        }
        return pseudo;
    }

    private boolean xws() throws IOException {
        while (accept(Token.WS)) {
            //
        }
        return true;
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
                    reporter.notifyError("disclosed comment");
                    return false;
                }

                c = input.read();
            } while (c != '/' && c != -1);

            if (c == -1) {
                reporter.notifyError("disclosed comment");
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
                reporter.notifyError("Mismatched pseudo element name '" + pseudo + "'");
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
                reporter.notifyError("Empty selector definition");
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
            reporter.notifyError("unexpected symbol '" + tokenStr + "' instead of '" + tok.getString() + "'");
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
}

