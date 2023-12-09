package org.generator.util.net;


import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;


public class IPV4 {
    public IPV4(){}

    public static IPV4 IDOf(String id_st){
        var ip = new IPV4();
        if (ip.fromStr(id_st, true)){
            ip.setIsId(true);
            return ip;
        }else return null;
    }

    public static IPV4 IPOf(String ip_st){
        var ip = new IPV4();
        if (ip.fromStr(ip_st, false)){
            ip.setIsId(false);
            return ip;
        }else return null;
    }

    static public IPV4 IDOf(long num){
        return IDOf(coverToStrId(num));
    }

    static  public IPV4 IPOf(long num, int prefix){
        return IPOf(convertToCIDR(num, prefix));
    }

    private boolean fromStr(@NotNull String ip_st, boolean id){
        if (id) {
            if (!ip_st.contains("/")) {
                ip_st = ip_st + "/32";
            }else{
                return false;
            }
        }
        try {
            utils = new SubnetUtils(ip_st);
        }catch (IllegalArgumentException e){
            return false;
        }
        utils.setInclusiveHostCount(true);
        return true;
    }


//    public boolean contains(IPV4 ip){
//        return utils.getInfo().isInRange(ip.toString());
//    }
//
//    public boolean equals(IPV4 ip){
//        return contains(ip) && ip.contains(this);
//    }

    private static String convertToCIDR(long ipAddress, int subnetMask) {
        if (ipAddress < 0 || ipAddress > 4294967295L) return "";
        StringBuilder sb = new StringBuilder();

        sb.append((ipAddress >> 24) & 255).append(".");
        sb.append((ipAddress >> 16) & 255).append(".");
        sb.append((ipAddress >> 8) & 255).append(".");
        sb.append(ipAddress & 255);
        sb.append("/").append(subnetMask);

        return sb.toString();
    }

    private static String coverToStrId(long ipAddress){
        if (ipAddress < 0 || ipAddress > 4294967295L) return "";
        return ((ipAddress >> 24) & 255) + "." +
                ((ipAddress >> 16) & 255) + "." +
                ((ipAddress >> 8) & 255) + "." +
                (ipAddress & 255);
    }

    @Override
    public String toString() {
        String st = utils.getInfo().getCidrSignature();
        if (isId){
            return Arrays.stream(st.split("/")).toList().get(0);
        }else {
            return st;
        }
    }

    public int IDtoInt(){
        assert isId;
        return utils.getInfo().asInteger(utils.getInfo().getNetworkAddress());
    }

    public IPV4 getIDOfIp(){
        assert !isId;
        return IDOf(utils.getInfo().getNetworkAddress());
    }

    public int getMaskOfIp(){
        assert  !isId;
        String[] parts = utils.getInfo().getNetmask().split("\\.");

        int netmaskLength = 0;
        for (String part : parts) {
            int octet = Integer.parseInt(part);
            while (octet > 0) {
                netmaskLength++;
                octet = octet << 1 & 0xFF;
            }
        }
        return netmaskLength;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPV4 ipv4 = (IPV4) o;
        return isId == ipv4.isId && toString().equals(ipv4.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public IPV4 clone(){
        if (isId){
            return IDOf(toString());
        }else return IPOf(toString());
    }
    private SubnetUtils utils;

    public boolean isId() {
        return isId;
    }

    public void setIsId(boolean id) {
        isId = id;
    }

    private boolean isId;
}
