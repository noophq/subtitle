package fr.noop.subtitle.base;

import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jdvorak on 20.1.2017.
 */
public abstract class BaseSubtitleParser implements SubtitleParser {
    @Override
    public SubtitleObject parse(InputStream is) throws IOException, SubtitleParsingException {
        return parse(is, 0,-1, true);
    }

    @Override
    public SubtitleObject parse(InputStream is, boolean strict) throws IOException, SubtitleParsingException {
        return parse(is, 0,-1, strict);
    }

    @Override
    public SubtitleObject parse(InputStream is, int subtitleOffset, boolean strict) throws IOException, SubtitleParsingException {
        return parse(is, subtitleOffset,-1, strict);
    }
}
