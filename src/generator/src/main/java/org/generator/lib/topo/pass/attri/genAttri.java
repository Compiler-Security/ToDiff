package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.item.base.Router;

import java.util.List;

public interface genAttri {
    void generate(ConfGraph g, List<Router> routers);
}
