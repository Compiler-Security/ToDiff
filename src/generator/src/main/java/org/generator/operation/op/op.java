package org.generator.operation.op;

import org.generator.util.net.IPV4;

import java.util.Map;

public interface op {
    boolean decode(String st);
    boolean encode(StringBuilder buf);
    String encode_to_str();
    OpType Type();
    Map<String, String> Args();
    void putArgs(Map<String, String> args);
    void putArg(String field_name, String val);

    default void putIntArg(String field_name, Integer val){
        assert (val != null);
        putArg(field_name, String.valueOf(val));
    }

    default void putDoubleArg(String field_name, Double val){
        assert val != null;
        putArg(field_name, String.valueOf(val));
    }

    default void putIpArg(String field_name, IPV4 ip){
        assert ip != null;
        putArg(field_name, ip.toString());
    }


    default  void putLongArg(String field_name, Long val){
        putArg(field_name, String.valueOf(val));
    }

    default String Arg(String field_name){
        assert Args().get(field_name) != null;
        return Args().get(field_name);
    }

    default int IntArg(String field_name){
        return Integer.parseInt(Arg(field_name));
    }

    default long LongArg(String field_name){
        return Long.parseLong(Arg(field_name));
    }

    default double DoubleArg(String field_name){
        return Double.parseDouble(field_name);
    }

    default IPV4 IPArg(String field_name){
        return IPV4.IPOf(field_name);
    }

    default IPV4 IDArg(String field_name){
        return IPV4.IDOf(field_name);
    }
    @Override
    String toString();
}
