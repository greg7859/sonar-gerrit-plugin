package fr.techad.sonar.gerrit;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rule.Severity;

public final class ReviewUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewUtils.class);
    private static final String LOG_MESSAGE = "[GERRIT PLUGIN] Got review level {}, level is now {}";
    private static final String UNKNOWN = "UNKNOWN";
    private static final int INFO_VALUE = 0;
    private static final int MINOR_VALUE = 1;
    private static final int MAJOR_VALUE = 2;
    private static final int CRITICAL_VALUE = 3;
    private static final int BLOCKER_VALUE = 4;
    private static final int UNKNOWN_VALUE = -1;

    private ReviewUtils() {

    }

    public static boolean isEmpty(ReviewInput ri) {
        return ri.getComments().isEmpty();
    }

    public static int thresholdToValue(String threshold) {
        int thresholdValue = UNKNOWN_VALUE;

        if (StringUtils.equals(threshold, Severity.INFO)) {
            thresholdValue = INFO_VALUE;
        } else if (StringUtils.equals(threshold, Severity.MINOR)) {
            thresholdValue = MINOR_VALUE;
        } else if (StringUtils.equals(threshold, Severity.MAJOR)) {
            thresholdValue = MAJOR_VALUE;
        } else if (StringUtils.equals(threshold, Severity.CRITICAL)) {
            thresholdValue = CRITICAL_VALUE;
        } else if (StringUtils.equals(threshold, Severity.BLOCKER)) {
            thresholdValue = BLOCKER_VALUE;
        } else {
            thresholdValue = UNKNOWN_VALUE;
        }

        return thresholdValue;
    }

    public static String valueToThreshold(int value) {
        String threshold = UNKNOWN;

        switch (value) {
        case INFO_VALUE:
            threshold = Severity.INFO;
            break;
        case MINOR_VALUE:
            threshold = Severity.MINOR;
            break;
        case MAJOR_VALUE:
            threshold = Severity.MAJOR;
            break;
        case CRITICAL_VALUE:
            threshold = Severity.CRITICAL;
            break;
        case BLOCKER_VALUE:
            threshold = Severity.BLOCKER;
            break;

        default:
            break;
        }

        return threshold;
    }

    public static int maxLevel(ReviewInput reviewInput) {
        int lvl = 0;

        for (Iterator<List<ReviewFileComment>> i = reviewInput.getComments().values().iterator(); i.hasNext();) {
            List<ReviewFileComment> lrfc = i.next();
            for (ReviewFileComment review : lrfc) {
                if (StringUtils.contains(review.getMessage(), Severity.INFO)) {
                    lvl = Math.max(lvl, thresholdToValue(Severity.INFO));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(LOG_MESSAGE, Severity.INFO, lvl);
                    }
                } else if (StringUtils.contains(review.getMessage(), Severity.MINOR)) {
                    lvl = Math.max(lvl, thresholdToValue(Severity.MINOR));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(LOG_MESSAGE, Severity.MINOR, lvl);
                    }
                } else if (StringUtils.contains(review.getMessage(), Severity.MAJOR)) {
                    lvl = Math.max(lvl, thresholdToValue(Severity.MAJOR));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(LOG_MESSAGE, Severity.MAJOR, lvl);
                    }
                } else if (StringUtils.contains(review.getMessage(), Severity.CRITICAL)) {
                    lvl = Math.max(lvl, thresholdToValue(Severity.CRITICAL));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(LOG_MESSAGE, Severity.CRITICAL, lvl);
                    }
                } else if (StringUtils.contains(review.getMessage(), Severity.BLOCKER)) {
                    lvl = Math.max(lvl, thresholdToValue(Severity.BLOCKER));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(LOG_MESSAGE, Severity.BLOCKER, lvl);
                    }
                }
            }
        }

        return lvl;
    }
}
