package util;

import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.junit.Test;

import java.util.Objects;

public class netTest {
    @Test
    public void idTest(){
        var id1 = ID.of("1.1.1.1");
        var id2 = ID.of("1.1.1.1");
        var ip1 = IP.of("1.1.1.1/22");
        System.out.printf("%s %s %s", id1, id2, id1.equals(ip1));
    }

    @Test
    public void ipTest(){
        var ip1 = IP.of(2, 22);
        var ip2 = IP.of("1.1.1.2/22");
        assert ip1.getMask() == 22;
        assert ip1.getAddress().equals("0.0.0.2");
        assert ip1.toString().equals("0.0.0.2/22");
        assert !ip1.equals(ip2);
    }

    @Test
    public void ipRangeTest(){
        var ipRange = IPRange.of("1.1.1.1/22");
        var ipRange2 = IPRange.of("1.0.1.2/22");
        var ip1 = IP.of("1.1.1.2/22");
        assert ipRange.contains(ip1);
        assert ipRange.getMask() == 22;
        assert ipRange.toRangeString().equals("1.1.0.0/22");
        assert ipRange.toString().equals("1.1.1.1/22");
        assert !ipRange.equals(ipRange2);
    }
}
