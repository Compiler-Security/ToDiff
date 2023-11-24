package org.generator.util.net;


import org.generator.util.exception.Unimplemented;

public class IPV4 {
    public IPV4(){}
    public IPV4(String ip_st){
        if (ip_st == null){}
    }

    public boolean hasSubNet(IPV4 ip){
        new Unimplemented();
        return false;
    }

    static public IPV4 Of(int num){
        new Unimplemented();
        return new IPV4("");
    }
    @Override
    public String toString() {
        new Unimplemented();
        return super.toString();
    }

    public long toInt(){
        new Unimplemented();
        return 0;
    }
}
