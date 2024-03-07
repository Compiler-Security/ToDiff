package org.generator.util.net;

import java.util.Objects;

public class IP extends IPBase{
    public static IP of(String ip_st){
        var ip = new IP();
        if (ip.fromStr(ip_st, false)){
            ip.setIsId(false);
            return ip;
        }else return null;
    }

    public static IP of(long num, int prefix){
        return of(convertToCIDR(num, prefix));
    }

    /** 1.1.1.1/22 -> 22 */
    public int getMask(){
        return getMaskOfIp();
    }

    /** 1.1.1.1/22 -> 22, not & mask */
    public String getAddress(){
        return getAddressOfIp().toString();
    }
    /** this string is address/mask, not address&mask/mask */
    @Override
    public String toString() {
        return super.toString();
    }


    /**this equal means address1 == address2  && mask1 == mask2, not address & mask*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP ip = (IP) o;
        return ip.toString().equals(toString());
    }

    /**this hashcode satisfy that two equal IPs have the same hashCode*/
    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public IP copy(){
        return IP.of(toString());
    }
}
