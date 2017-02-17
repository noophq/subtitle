package com.blackboard.collaborate.csl.validators.subtitle.base;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleParser;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleReader;

import java.io.IOException;

/**
 * Created by jdvorak on 20.1.2017.
 */
//@Slf4j
public abstract class BaseSubtitleParser implements SubtitleParser {
    protected final ValidationReporter reporter;
    protected final SubtitleReader reader; // the reader

    public BaseSubtitleParser(ValidationReporter reporter, SubtitleReader reader) {
        this.reporter = reporter;
        this.reader = reader;
    }

    @Override
    public SubtitleObject parse() throws IOException {
        return parse(0,-1, true);
    }

    @Override
    public SubtitleObject parse(boolean strict) throws IOException {
        return parse(0,-1, strict);
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, boolean strict) throws IOException {
        return parse(subtitleOffset,-1, strict);
    }


    @Override
    public void notifyWarning(String msg) {
        reporter.notifyWarning(msg);
    }

    @Override
    public void notifyError(String msg) {
        reporter.notifyError(msg);
    }
}
