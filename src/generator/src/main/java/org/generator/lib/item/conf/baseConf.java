package org.generator.lib.item.conf;

import org.generator.lib.item.opg.BaseOpG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface baseConf<T, U extends BaseOpG<T>> {
    U getConfInOneGroup();
    List<U> getConfInGroups();
}
