package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Router;

import java.util.List;

public class ripRanBaseGen implements genBase{
    @Override
    //we don't care about areaCount and abrRatio
    public List<Router> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio) {
        // FIXME
        // current we just use OSPF base generate, set abrRatio=0, areaCount=1
        var ospf_base_gen = new ospfRanBaseGen();
        var res =  ospf_base_gen.generate(totalRouter, 1, mxDegree, 0);
        networkId = ospf_base_gen.networkId;
        return res;
    }

    public int networkId;
}
