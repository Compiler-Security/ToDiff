package org.generator.util.net;

public class ID extends  IPBase{
    public static ID of(String id_st){
        var ip = new ID();
        if (ip.fromStr(id_st, true)){
            ip.setIsId(true);
            return ip;
        }else return null;
    }

    static public ID of(long num){
        return of(coverToStrId(num));
    }

    public boolean contains(ID id){
        return containsId(id);
    }

    public Long toLong(){
        return IDtoLong();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    //this equal means str xxx.xxx.xxx.xxx is equal
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public ID copy(){
        return ID.of(toLong());
    }
}
