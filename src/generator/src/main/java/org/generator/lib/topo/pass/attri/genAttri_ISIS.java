package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.item.base.Router_ISIS;

import java.util.List;

public interface genAttri_ISIS {
    void generate(ConfGraph g, List<Router_ISIS> routers);
}
