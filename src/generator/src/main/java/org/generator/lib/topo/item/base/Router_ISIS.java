package org.generator.lib.topo.item.base;

import org.generator.lib.topo.pass.base.isisRanBaseGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Router_ISIS {
    int id;
    public List<Intf_ISIS> intfs;
    // 0: L1, 1: L2, 2: L1L2
    public int level;
    public int area;
    //public boolean abr;
    public Router_ISIS(int id, int level, int area){
        intfs = new ArrayList<>();
        this.id = id;
        this.level = level;
        this.area = area;
    }


    public List<Intf_ISIS> getUnconnectedIntfs(){
        return intfs.stream().filter(intf -> intf.networkId == -1).collect(Collectors.toList());
    }

    public List<Intf_ISIS> getConnectedIntfs(){
        return intfs.stream().filter(intf -> intf.networkId != -1).collect(Collectors.toList());
    }
    public Integer getArea(){
        return area;
    }

    public Integer getLevel(){
        return level;
    }
}