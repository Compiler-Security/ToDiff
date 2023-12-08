package org.generator.util.net;


import org.generator.util.exception.Unimplemented;
import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;

public class IPV4 {
    public IPV4(){}
    public IPV4(@NotNull String ip_st){
        if (!ip_st.contains("/")){
            ip_st = ip_st + "/32";
        }
        try {
             utils = new SubnetUtils(ip_st);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        utils.setInclusiveHostCount(true);
    }


    public boolean contains(IPV4 ip){
        return utils.getInfo().isInRange(ip.toString());
    }

    public boolean equals(IPV4 ip){
        return contains(ip) && ip.contains(this);
    }

    private static String convertToCIDR(int ipAddress, int subnetMask) {
        StringBuilder sb = new StringBuilder();

        sb.append((ipAddress >> 24) & 255).append(".");
        sb.append((ipAddress >> 16) & 255).append(".");
        sb.append((ipAddress >> 8) & 255).append(".");
        sb.append(ipAddress & 255);
        sb.append("/").append(subnetMask);

        return sb.toString();
    }
    static public IPV4 Of(int num){
        return new IPV4(convertToCIDR(num, 32));
    }
    @Override
    public String toString() {
        return utils.getInfo().getCidrSignature();
    }

    public int toInt(){
        return utils.getInfo().asInteger(utils.getInfo().getNetworkAddress());
    }

    @Override
    public IPV4 clone(){
        return new IPV4(toString());
    }
    private SubnetUtils utils;
}
