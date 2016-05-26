package com.aem.smart.utils.hc.configuration.util;

import com.google.common.base.Joiner;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Simple iterator to hierarchically traverse through runmode names.
 */
final class RunmodeNamesHierarchicalIterator implements Iterator<String> {

    private final List<String> items = new ArrayList<>();
    private int currentIndex;
    private final Joiner joiner;

    public RunmodeNamesHierarchicalIterator(String runmode) {
        joiner = Joiner.on(".");
        if (StringUtils.isNotBlank(runmode)) {
            items.addAll(Arrays.asList(runmode.split("\\.")));
            currentIndex = items.size();
        }
    }

    public boolean hasNext() {
        return items.size() > 0 && currentIndex >= 0;
    }

    public String next() {
        return joiner.join(items.subList(0, currentIndex--));
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
