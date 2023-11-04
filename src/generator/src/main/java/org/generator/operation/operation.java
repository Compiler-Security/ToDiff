package org.generator.operation;

import org.generator.util.net.IPV4;

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

    default void setIpArg(String field_name, IPV4 ip){
        setArg(field_name, ip.toString());
    }

    default String getArg(String field_name){
        return getArgs().get(field_name);
    }

    default int getIntArg(String field_name){
        return Integer.parseInt(getArg(field_name));
    }

    default double getDoubleArg(String field_name){
        return Double.parseDouble(field_name);
    }

    default IPV4 getIPArg(String field_name){
        return new IPV4(field_name);
    }
}
