/**
 * This pass will random shrink Core pass to the minimal form
 */
package org.generator.lib.generator.ospf.pass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.reducer.driver.reducer;

import java.util.List;

import static org.generator.util.diff.differ.compareJson;

public class shrinkCorePass {

    ObjectNode getJson(ConfGraph confG){
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        for(var node: confG.getNodes()){
            jsonObject.set(node.getName(), node.getJsonNode());
        }
        return jsonObject;
    }
    boolean check(List<OpCtxG> opCtxGS, ConfGraph confG, String r_name){
        var opCtxG = genCorePassOspf.mergeOpCtxgToOne(opCtxGS);
        var g = confG.copyPhyGraph();
        reducer.reduceToConfG(opCtxG, g);
        return getJson(confG).equals(getJson(g));
    }
    /**
     * This will change opCtxGs to the mininal formal
     * each OpCtxG's first op is SetOp
     * @param confG
     * @param opCtxGs
     */
    public void solve(List<OpCtxG> opCtxGs, ConfGraph confG){
        String r_name = confG.getR_name();
        if (!check(opCtxGs, confG, r_name)){
            var opCtxG = genCorePassOspf.mergeOpCtxgToOne(opCtxGs);
            var g = confG.copyPhyGraph();
            reducer.reduceToConfG(opCtxG, g);
            System.out.println(opCtxG);
            System.out.println(compareJson(confG.toJson(),g.toJson()).toPrettyString());
            assert false:"genCorePass's core is not equal to confG";
        }

        for(var opCtxg: opCtxGs){
            var ops = opCtxg.getOps();
            for(int i = 0; i < ops.size(); i++){
                if (i == 0) continue;
                var op = ops.remove(i);
                if (check(opCtxGs, confG, r_name)){
                    i--;
                }else{
                    ops.add(i, op);
                }
            }
        }
    }
    public shrinkCorePass(){}
}
