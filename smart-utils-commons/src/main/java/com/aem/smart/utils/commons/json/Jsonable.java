package com.aem.smart.utils.commons.json;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

/**
 * Objects that implement this interface are capable to provide their JSON representation.
 */
public interface Jsonable {
    /**
     * Gets JSON representation of this object.
     * @return JSON representation of this instance
     */
    JSONObject toJson() throws JSONException;
}
