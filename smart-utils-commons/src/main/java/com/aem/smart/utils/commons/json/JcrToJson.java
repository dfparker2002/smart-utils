package com.aem.smart.utils.commons.json;

import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

import java.util.Set;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

/**
 * The type Set to json.
 */
public final class JcrToJson {

    private static final String VALUE = "value";

    /**
     * Convert Set of String to json object array with given structure:
     *
     * {
     *      [
     *          {
     *              "jcr:title": "{@value} from Set",
     *              "value" : "{@value} from Set"
     *          },
     *          {...}
     *      ],
     *     "jcr:title": {@value} from collectionName,
     *     "jcr:primaryType": "sling:OrderedFolder",
     * }
     *
     * @param items          the items
     * @param collectionName the collection name
     * @return the json object
     * @throws JSONException the json exception
     */
    public static JSONObject toJson(final Set<String> items, final String collectionName) throws JSONException {

        JSONObject hitsJSONObject = new JSONObject();

        for (String item : items) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JCR_TITLE, item);
            jsonObject.put(VALUE, item);
            hitsJSONObject.put(item, jsonObject);
        }
        hitsJSONObject.put(JCR_TITLE, collectionName);
        hitsJSONObject.put(JCR_PRIMARYTYPE, "sling:OrderedFolder");

        return hitsJSONObject;
    }

    private JcrToJson() {
    }
}
