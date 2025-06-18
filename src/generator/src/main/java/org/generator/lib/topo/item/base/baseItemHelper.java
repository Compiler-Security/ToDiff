package org.generator.lib.topo.item.base;

import java.util.ArrayList;
import java.util.List;

public class baseItemHelper {
    public static List<Router> copyFrom(List<Router> routers){
        List<Router> r = new ArrayList<Router>();
        routers.forEach(ro -> r.add(ro.copy()));
        return r;
    }
}
