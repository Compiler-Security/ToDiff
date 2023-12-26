package org.generator.util.net;

public class IPRange extends IPBase{
    public static IPRange of(String ip_st){
        var ip = new IPRange();
        if (ip.fromStr(ip_st, false)){
            ip.setIsId(false);
            return ip;
        }else return null;
    }

    public static IPRange of(long num, int prefix){
        return of(convertToCIDR(num, prefix));
    }

    /** IpRange contains ip iff address in range && mask == mask */
    public  boolean contains(IP ip){
        if(ip.getMask() != getMask()) return false;
        return containsIp(ip);
    }

    //1.1.1.1/22 -> 22
    public int getMask(){
        return getMaskOfIp();
    }

    /** this string is address/mask, not address&mask/mask */
    @Override
    public String toString() {
        return super.toString();
    }

    /** this string is address&mask/mask */
    public String toRangeString(){
        return super.toNetString();
    }


    /**this equal means address1 & mask1 == address2 & mask2 && mask1 == mask2*/
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**this hashcode satisfy that two equal IPRanges have the same hashCode*/
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
