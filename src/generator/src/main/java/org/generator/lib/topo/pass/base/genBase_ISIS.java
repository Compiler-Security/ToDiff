package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Router_ISIS;

import java.util.List;

public interface genBase_ISIS {
    List<Router_ISIS> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio);
}
