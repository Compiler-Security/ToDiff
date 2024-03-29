package org.generator.util.net;


import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class IPBase {
    public boolean isWrong() {
        return wrong;
    }

    public void setWrong(boolean wrong) {
        this.wrong = wrong;
    }

    public IPBase(){
        wrong = false;
    }

    public static IPBase IDOf(String id_st){
        var ip = new IPBase();
        if (ip.fromStr(id_st, true)){
            ip.setIsId(true);
            return ip;
        }else return null;
    }

    public static IPBase IPOf(String ip_st){
        var ip = new IPBase();
        if (ip.fromStr(ip_st, false)){
            ip.setIsId(false);
            return ip;
        }else return null;
    }


    protected boolean fromStr(@NotNull String ip_st, boolean id){
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


    public boolean containsId(IPBase id){
        assert id.isId : "should contain id not subnet";
        return utils.getInfo().isInRange(id.toString());
    }

    public boolean containsIp(IPBase ip){
        assert !isId() : "should contain ip";
        //FIXME we don't check subnet(mask)
        return utils.getInfo().isInRange(ip.getAddressOfIp().toString());
    }
//
//    public boolean equals(IPV4 ip){
//        return contains(ip) && ip.contains(this);
//    }

    protected static String convertToCIDR(long ipAddress, int subnetMask) {
        if (ipAddress < 0 || ipAddress > 4294967295L) return "";
        StringBuilder sb = new StringBuilder();

        sb.append((ipAddress >> 24) & 255).append(".");
        sb.append((ipAddress >> 16) & 255).append(".");
        sb.append((ipAddress >> 8) & 255).append(".");
        sb.append(ipAddress & 255);
        sb.append("/").append(subnetMask);

        return sb.toString();
    }

    protected static String coverToStrId(long ipAddress){
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

    String netString;
    public String toNetString(){
        if (netString != null) return netString;
        if (isId){
            netString = toString();
        }else{
            netString = String.format("%s/%d", getNetAddressOfIp(), getMaskOfIp());
        }
        return netString;
    }

    private static long ipToLong(String ipAddress) {
        String[] addrArray = ipAddress.split("\\.");
        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += (long) ((Integer.parseInt(addrArray[i]) % 256) * Math.pow(256, power));
        }
        return num;
    }

    public Long IDtoLong(){
        assert isId;
        return ipToLong(utils.getInfo().getNetworkAddress());
        //return utils.getInfo().asInteger(utils.getInfo().getNetworkAddress());
    }

    public IPBase getAddressOfIp(){
        assert !isId;
        return IDOf(utils.getInfo().getAddress());
    }

    public IPBase getNetAddressOfIp(){
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
        IPBase IPBase = (IPBase) o;
        if (isId != IPBase.isId) return false;
        return toNetString().equals(IPBase.toNetString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toNetString());
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

    public Long longNum;
}
