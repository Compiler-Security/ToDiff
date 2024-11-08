package org.generator.lib.topo.item.base;

import org.generator.lib.topo.pass.base.ranBaseGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Router {
    int id;
    public List<Intf> intfs;
    public boolean abr;
    public Router(int id){
        intfs = new ArrayList<>();
        this.id = id;
    }

    public Set<Integer> getAreas(){
        return intfs.stream().map(intf -> intf.area).collect(Collectors.toSet());
    }

    public List<Intf> getIntfsOfArea(int area){
        return intfs.stream().filter(intf -> intf.area == area).collect(Collectors.toList());
    }

    public List<Intf> getUnconnectedIntfsOfArea(int area){
        return getIntfsOfArea(area).stream().filter(intf-> intf.networkId == -1).collect(Collectors.toList());
    }
}