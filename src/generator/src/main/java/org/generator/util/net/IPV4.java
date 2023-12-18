package org.generator.util.net;


import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class IPV4 {
    public boolean isWrong() {
        return wrong;
    }

    public void setWrong(boolean wrong) {
        this.wrong = wrong;
    }

    public IPV4(){
        wrong = false;
    }

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


    public boolean containsId(IPV4 id){
        assert id.isId : "should contain id not subnet";
        return utils.getInfo().isInRange(id.toString());
    }

    public boolean containsIp(IPV4 ip){
        assert !isId() : "should contain ip";
        //FIXME we don't check subnet(mask)
        return utils.getInfo().isInRange(ip.getAddressOfIp().toString());
    }
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
        if (!wrong) {
            String st = utils.getInfo().getCidrSignature();
            if (isId) {
                return Arrays.stream(st.split("/")).toList().get(0);
            } else {
                return st;
            }
        }else{
            var ran = new Random();
                StringBuilder b = new StringBuilder();
                switch (ran.nextInt(3)){
                    case 0,1 -> {
                        b.append(ran.nextInt(-10000, 10000));
                        for(int i = 1; i <= 3; i++){
                            b.append("/");
                            b.append(ran.nextInt(-10000, 10000));
                        }
                    }
                    case 2 -> {
                        for(int i = 0; i < ran.nextInt(10); i++){
                            b.append(ran.nextInt(-10000, 10000));
                            b.append("/");
                        }
                    }
                }
                if (isId && ran.nextInt(10) < 8){
                    b.append("/");
                    b.append(ran.nextInt(-32, 32));
                }
            return b.toString();
        }
    }

    public String toNetString(){
        if (isId){
            return toString();
        }else{
            return String.format("%s/%d", getNetAddressOfIp(), getMaskOfIp());
        }
    }

    public int IDtoInt(){
        assert isId;
        return utils.getInfo().asInteger(utils.getInfo().getNetworkAddress());
    }

    public IPV4 getAddressOfIp(){
        assert !isId;
        return IDOf(utils.getInfo().getAddress());
    }

    public IPV4 getNetAddressOfIp(){
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
        if (isId != ipv4.isId) return false;
        return toNetString().equals(ipv4.toNetString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toNetString());
    }

    @Override
    public IPV4 clone(){
        if (isId){
            return IDOf(toString());
        }else return IPOf(toString());
    }

    private boolean wrong;
    private SubnetUtils utils;

    public boolean isId() {
        return isId;
    }

    public void setIsId(boolean id) {
        isId = id;
    }

    private boolean isId;
}
