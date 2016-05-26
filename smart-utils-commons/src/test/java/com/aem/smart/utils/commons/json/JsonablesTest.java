package com.aem.smart.utils.commons.json;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonablesTest {

    @Mock
    private Jsonable input1;
    @Mock
    private Jsonable input2;
    @Mock
    private JSONObject output1;
    @Mock
    private JSONObject output2;

    @Test
    public void shouldCorrectlyTransformToJson() throws JSONException {
        when(input1.toJson()).thenReturn(output1);
        when(input2.toJson()).thenReturn(output2);

        Collection<JSONObject> actual = Jsonables.toJson(Arrays.asList(input1, input2));

        assertEquals("Collection should contain two items", 2, actual.size());
        assertTrue("Collection should contain all output items", actual.containsAll(Arrays.asList(output1, output2)));
    }
}