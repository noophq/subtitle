package com.blackboard.collaborate.csl.validators.subtitle.model;

/**
 * Provides line:column information of current parsing position.
 */
public interface ParsePositionProvider {
    /**
     * @return Line number.
     */
    int getLineNumber();

    /**
     * @return Column.
     */
    int getColumn();
}
