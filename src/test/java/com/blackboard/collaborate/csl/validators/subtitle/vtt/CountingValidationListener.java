package com.blackboard.collaborate.csl.validators.subtitle.vtt;

import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationIssue;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationListener;
import org.junit.Assert;

/**
 * Created by jdvorak on 25.1.2017.
 * Helper class for testing
 */
public class CountingValidationListener implements ValidationListener {
    int count;

    @Override
    public void onValidation(ValidationIssue event) {
        count++;
        System.out.println(event.toString());
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

    public void exactAssert(int errors) {
        String msg = "Error count does not match: " + getCount() + " <> " + errors;
        Assert.assertTrue(msg, getCount() == errors);
    }

    public void reset() {
        count = 0; // reset the count
    }
}
