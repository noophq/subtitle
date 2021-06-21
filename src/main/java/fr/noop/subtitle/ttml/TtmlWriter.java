/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.ttml;

import fr.noop.subtitle.model.*;
import fr.noop.subtitle.util.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by clebeaupin on 02/10/15.
 */
public class TtmlWriter implements SubtitleWriter {
    private final static String NS_TT = "http://www.w3.org/ns/ttml";
    private final static String NS_TTP = "http://www.w3.org/ns/ttml#parameter";
    private final static String NS_TTM = "http://www.w3.org/ns/ttml#metadata";
    private final static String NS_TTS = "http://www.w3.org/ns/ttml#styling";
    private final static String NS_XML = "http://www.w3.org/XML/1998/namespace";

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) {
        TtmlObject ttmlObject = new TtmlObject(subtitleObject);
        
        // Prepare XML
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

        try {
            StringWriter sw = new StringWriter();
            XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(sw);
            xsw.writeStartDocument("utf-8", "1.0");
            xsw.setPrefix("tt", NS_TT);
            xsw.setPrefix("ttp", NS_TTP);
            xsw.setPrefix("tts", NS_TTS);
            xsw.setPrefix("ttm", NS_TTM);
            xsw.setPrefix("xml", NS_XML);
            xsw.writeStartElement("tt");
            xsw.writeDefaultNamespace(NS_TT);
            xsw.writeNamespace("tt", NS_TT);
            xsw.writeNamespace("ttp", NS_TTP);
            xsw.writeNamespace("tts", NS_TTS);
            xsw.writeNamespace("ttm", NS_TTM);
            xsw.writeNamespace("xml", NS_XML);

            if (ttmlObject.hasProperty(SubtitleObject.Property.FRAME_RATE)) {
                xsw.writeAttribute(
                        NS_TTP,
                        "frameRate",
                        String.valueOf(ttmlObject.getProperty(SubtitleObject.Property.FRAME_RATE)));
            }

            // Write header
            xsw.writeStartElement("head");

            // Write metadata
            this.writeMetadata(ttmlObject, xsw);

            // Write styles
            this.writeStyles(ttmlObject, xsw);

            // Write regions
            this.writeRegions(ttmlObject, xsw);

            // End of head
            xsw.writeEndElement();

            // Write cues
            this.writeCues(ttmlObject, xsw);

            // End of tt
            xsw.writeEndElement();

            byte[] bytes = sw.toString().getBytes();
            InputStream is = new ByteArrayInputStream(bytes);
            StreamSource ss = new StreamSource(is);
            StreamResult sr = new StreamResult(os);

            // Create pretty xml
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(ss, sr);
            } catch (TransformerConfigurationException e) {

            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } catch (XMLStreamException e) {

        }
    }

    private void writeMetadata(TtmlObject ttmlObject, XMLStreamWriter xsw) throws XMLStreamException {
        // Start metadata
        xsw.writeStartElement("metadata");

        // Write title
        xsw.writeStartElement(NS_TTM, "title");
        xsw.writeCharacters((String) ttmlObject.getProperty(SubtitleObject.Property.TITLE));
        xsw.writeEndElement();

        // End of metadata
        xsw.writeEndElement();
    }

    private void writeStyles(TtmlObject ttmlObject, XMLStreamWriter xsw) throws XMLStreamException {
        // Start layout that contains all regions
        xsw.writeStartElement("styling");

        for (Map.Entry<String, SubtitleStyle> entry: ttmlObject.getStyles().entrySet()) {
            SubtitleStyle style = entry.getValue();
            String styleId = entry.getKey();

            // Writer style
            xsw.writeStartElement("style");
            xsw.writeAttribute(NS_XML, "id", styleId);


            if (style.getColor() != null) {
                xsw.writeAttribute(NS_TTS, "color", style.getColor());
            }

            // Text align
            if (style.getTextAlign() != null) {
                String textAlign = "center";

                if (style.getTextAlign() == SubtitleStyle.TextAlign.LEFT) {
                    textAlign = "left";
                } else if (style.getTextAlign() == SubtitleStyle.TextAlign.RIGHT) {
                    textAlign = "right";
                }

                xsw.writeAttribute(NS_TTS, "textAlign", textAlign);
            }

            // Text direction
            if (style.getDirection() != null) {
                String direction = "ltr";

                if (style.getDirection() == SubtitleStyle.Direction.RTL) {
                    direction = "rtl";
                }

                xsw.writeAttribute(NS_TTS, "direction", direction);
            }

            xsw.writeEndElement();
        }

        // End of layout
        xsw.writeEndElement();
    }

    private void writeRegions(TtmlObject ttmlObject, XMLStreamWriter xsw) throws XMLStreamException {
        // Start layout that contains all regions
        xsw.writeStartElement("layout");

        for (Map.Entry<String, SubtitleRegion> entry: ttmlObject.getRegions().entrySet()) {
            SubtitleRegion region = entry.getValue();
            String regionId = entry.getKey();

            // Write region
            xsw.writeStartElement("region");
            xsw.writeAttribute(NS_XML, "id", regionId);

            // With US locale to format number with dot instead of comma
            xsw.writeAttribute(NS_TTS, "origin", String.format(Locale.US, "0%% %.2f%%", region.getY()));
            xsw.writeAttribute(NS_TTS, "extent", String.format(Locale.US, "100%% %.2f%%", region.getHeight()));

            // Vertical align to bottom
            xsw.writeAttribute(NS_TTS, "displayAlign", "after");

            // End of region
            xsw.writeEndElement();
        }

        // End of layout
        xsw.writeEndElement();
    }

    private void writeCues(TtmlObject ttmlObject, XMLStreamWriter xsw) throws XMLStreamException {
        // Start of cues
        xsw.writeStartElement("body");
        xsw.writeStartElement("div");

        for (SubtitleCue cue : ttmlObject.getCues()) {
            TtmlCue ttmlCue = (TtmlCue) cue;

            // Start ttmlCue
            xsw.writeStartElement("p");

            xsw.writeAttribute(NS_XML, "id", ttmlCue.getId());

            // Write region
            if (ttmlCue.getRegion() != null) {
                xsw.writeAttribute("region", ttmlObject.getRegionId(ttmlCue.getRegion()));
            }

            // Write start and end time codes
            xsw.writeAttribute("begin", this.formatTimeCode(ttmlCue.getStartTime()));
            xsw.writeAttribute("end", this.formatTimeCode(ttmlCue.getEndTime()));

            // Write ttmlCue text
            int lineIndex = 0;

            for (SubtitleLine line: ttmlCue.getLines()) {
                lineIndex++;

                for (SubtitleText text: line.getTexts()) {
                    xsw.writeStartElement("span");

                    if (text instanceof SubtitleStyledText) {
                        // Apply a style on this text
                        xsw.writeAttribute("style", ttmlObject.getStyleId(((SubtitleStyledText) text).getStyle()));
                    }

                    xsw.writeCharacters(text.toString());
                    xsw.writeEndElement();
                }

                // Add line break between rows
                if (lineIndex < ttmlCue.getLines().size()) {
                    xsw.writeStartElement("br");
                    xsw.writeEndElement();
                }
            }

            // End of ttmlCue
            xsw.writeEndElement();
        }

        // End of cues
        xsw.writeEndElement();
        xsw.writeEndElement();
    }

    private String formatTimeCode(SubtitleTimeCode timeCode) {
        return String.format("%02d:%02d:%02d.%03d",
                timeCode.getHour(),
                timeCode.getMinute(),
                timeCode.getSecond(),
                timeCode.getMillisecond());
    }
}
