package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.topo.item.base.Router_ISIS;

import java.util.List;

public interface genAttri_ISIS {
    void generate(ConfGraph_ISIS g, List<Router_ISIS> routers);
}
