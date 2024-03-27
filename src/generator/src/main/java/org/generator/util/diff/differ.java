package org.generator.util.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class differ {
    public static ObjectNode compareJson(JsonNode expect, JsonNode actual) {
        ObjectNode differences = new ObjectMapper().createObjectNode();
        expect.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value1 = entry.getValue();
            JsonNode value2 = actual.get(key);

            if (value2 == null) {
                differences.put(key, "Key not present in actual JSON");
            } else if (!value1.equals(value2)) {
                if (value1.isObject() && value2.isObject()) {
                    ObjectNode diff = compareJson(value1, value2);
                    if (diff.size() > 0) {
                        differences.set(key, diff);
                    }
                } else {
                    ObjectNode diff = differences.putObject(key);
                    diff.set("expect", value1);
                    diff.set("actual", value2);
                }
            }
        });

        actual.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value2 = entry.getValue();
            JsonNode value1 = expect.get(key);

            if (value1 == null) {
                differences.put(key, "Key not present in expect JSON");
            }
        });

        return differences;
    }
}
