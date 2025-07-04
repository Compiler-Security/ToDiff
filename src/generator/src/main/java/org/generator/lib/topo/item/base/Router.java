package org.generator.lib.topo.item.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Router {
    public int id;
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

    public List<Intf> getIntfsOfNetwork(int networkId){
        return intfs.stream().filter(intf -> intf.networkId == networkId).collect(Collectors.toList());
    }

    public List<Intf> getUnconnectedIntfsOfArea(int area){
        return getIntfsOfArea(area).stream().filter(intf-> intf.networkId == -1).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Router router = (Router) o;
        return id == router.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Router copy(){
        Router router = new Router(id);
        intfs.forEach(intf -> router.intfs.add(intf.copy()));
        router.abr = abr;
        return router;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("r%d[".formatted(id));
        for(Intf intf : intfs){
            sb.append("(i:%d a:%d n:%d c:%d),".formatted(intf.id, intf.area,intf.networkId, intf.cost));
        }
        sb.append("]");
        return sb.toString();
    }
}