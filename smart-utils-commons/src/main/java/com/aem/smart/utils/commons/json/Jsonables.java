package com.aem.smart.utils.commons.json;

import java.util.Collection;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Static utility methods pertaining to {@link Jsonable} instances.
 */
public final class Jsonables {

    private static final Logger LOG = LoggerFactory.getLogger(Jsonables.class);

    /**
     * Transforms provided {@link Jsonable} objects to their respective JSON representations.
     * @param configurations collection of {@code Jsonable} objects to be transformed
     * @return collection with JSON representations of the provided {@code Jsonable} objects
     */
    public static Collection<JSONObject> toJson(final Collection<? extends Jsonable> configurations) {
        return Collections2.transform(configurations, new Function<Jsonable, JSONObject>() {
            @Override
            public JSONObject apply(final Jsonable config) {
                JSONObject result = new JSONObject();
                try {
                    result = config.toJson();
                } catch (JSONException ex) {
                    LOG.error("Fail to create JSON", ex);
                }
                return result;
            }
        });
    }

    /**
     * To json json array.
     *
     * @param items the items
     * @return the json array
     * @throws JSONException the json exception
     */
    public static JSONArray toJsonArray(final Collection<? extends Jsonable> items) throws JSONException {
        JSONArray result = new JSONArray();
        for (Jsonable item : items) {
            result.put(item.toJson());
        }
        return result;
    }

    private Jsonables() {
    }
}
