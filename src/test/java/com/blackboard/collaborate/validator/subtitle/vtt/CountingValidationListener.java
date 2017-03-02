/*
 * Title: CountingValidationListener
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

import com.blackboard.collaborate.validator.subtitle.model.ValidationIssue;
import com.blackboard.collaborate.validator.subtitle.model.ValidationListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdvorak on 25.1.2017.
 * Helper class for testing
 */
@Slf4j
public class CountingValidationListener implements ValidationListener {
    private int count;
    private final List<ValidationIssue> issues = new ArrayList<>();


    @Override
    public void onValidation(ValidationIssue event) {
        count++;
        issues.add(event);
        log.debug(event.toString());
    }

    public int getCount() {
        return count;
    }

    public void checkAssert(int maxErrors) {
        String msg1 = "Error count exceeded: " + getCount() + " >= " + maxErrors;
        Assert.assertTrue(msg1, getCount() <= maxErrors);
        if (maxErrors == 0 && getCount() > 0) {
            String msg2 = "Errors detected in valid data: " + getCount();
            Assert.assertTrue(msg2, getCount() > 0);
        }
        if (maxErrors > 0 && getCount() == 0) {
            String msg2 = "Errors not detected in invalid data: ";
            Assert.assertTrue(msg2, getCount() > 0);
        }
    }

    public void exactAssert(String file, int errors) {
        String msg = "Error count does not match: " + file + ": " + getCount() + " <> " + errors;
        if (getCount() != errors) {
            System.out.println();
            for (ValidationIssue issue : issues) {
                System.err.println("  " + issue.toString());
            }
        }
        Assert.assertTrue(msg, getCount() == errors);
    }

    public void reset() {
        count = 0; // reset the count
    }
}
