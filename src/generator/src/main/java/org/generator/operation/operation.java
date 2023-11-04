package org.generator.operation;

import java.util.Map;

public interface operation {
    boolean decode(String st);
    void encode(StringBuilder buf);
    String encode_to_str();
    OpType getType();
    Map<String, String> getFields();

    default String getField(String field_name){
        return getFields().get(field_name);
    }

    default int getIntField(String field_name){
        return Integer.parseInt(getField(field_name));
    }

    default double getDoubleField(String field_name){
        return Double.parseDouble(field_name);
    }
}
