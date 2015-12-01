/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.base;

/**
 * Created by clebeaupin on 09/10/15.
 */

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleLine;
import fr.noop.subtitle.util.SubtitleTextLine;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubtitleCue implements SubtitleCue {
    private String id; // Id of cue. 1 or c1
    private SubtitleTimeCode startTime; // Start displaying the cue at this time code
    private SubtitleTimeCode endTime; // Stop displaying the cue at this time code
    private List<SubtitleLine> lines; // Lines composed of texts

    protected BaseSubtitleCue(SubtitleCue cue) {
        this.id = cue.getId();
        this.startTime = cue.getStartTime();
        this.endTime = cue.getEndTime();
        this.lines = new ArrayList<>(cue.getLines());
    }

    protected BaseSubtitleCue() {
        this.lines = new ArrayList<>();
    }

    protected BaseSubtitleCue(SubtitleTimeCode startTime, SubtitleTimeCode endTime) {
        this.lines = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected BaseSubtitleCue(SubtitleTimeCode startTime, SubtitleTimeCode endTime, List<SubtitleLine> lines) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.lines = lines;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubtitleTimeCode getStartTime() {
        return this.startTime;
    }

    public void setStartTime(SubtitleTimeCode startTime) {
        this.startTime = startTime;
    }

    public SubtitleTimeCode getEndTime() {
        return this.endTime;
    }

    public void setEndTime(SubtitleTimeCode endTime) {
        this.endTime = endTime;
    }

    public List<SubtitleLine> getLines() {
        return this.lines;
    }

    public void setLines(List<SubtitleLine> lines) {
        this.lines = lines;
    }

    public void addLine(SubtitleLine line) {
        this.lines.add(line);
    }

    public void subtractTime(SubtitleTimeCode toSubtract) {
        this.setStartTime(this.getStartTime().subtract(toSubtract));
        this.setEndTime(this.getEndTime().subtract(toSubtract));
    }

    public String getText() {
        String[] texts = new String[this.lines.size()];

        for (int i=0; i<texts.length; i++) {
            texts[i] = this.lines.get(i).toString();
        }

        return String.join("\n", texts);
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
