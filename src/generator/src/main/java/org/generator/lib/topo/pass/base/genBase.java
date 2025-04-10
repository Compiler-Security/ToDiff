package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Router;

import java.util.List;

public interface genBase {
    List<Router> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio);
}
