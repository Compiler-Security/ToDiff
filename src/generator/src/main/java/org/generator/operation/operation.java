package org.generator.operation;

import java.util.Map;

public interface operation {
    boolean decode(String st);
    void encode(StringBuilder buf);
    String encode_to_str();
    OpType getType();
    Map<String, String> getArgs();
    void setArgs(Map<String, String> args);
    void setArg(String field_name, String val);

    default void setIntArg(String field_name, int val){
        setArg(field_name, String.valueOf(val));
    }

    default void setDoubleArg(String field_name, double val){
        setArg(field_name, String.valueOf(val));
    }

    default String getArgs(String field_name){
        return getArgs().get(field_name);
    }

    default int getIntArg(String field_name){
        return Integer.parseInt(getArgs(field_name));
    }

    default double getDoubleArg(String field_name){
        return Double.parseDouble(field_name);
    }
}
