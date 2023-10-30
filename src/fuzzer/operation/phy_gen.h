//
// Created by 水兵 on 2023/10/30.
//

#ifndef FUZZER_PHY_GEN_H
#define FUZZER_PHY_GEN_H

#include "ops.h"
#include <unordered_set>
//template <typename Key, typename T>
//T getOrDefault(const std::unordered_map<Key, T>& map, const Key& key, const T& defaultValue) {
//    auto it = map.find(key);
//    if (it != map.end()) {
//        return it->second;
//    } else {
//        return defaultValue;
//    }
//}


//template <typename KeyType, typename ValueType>
//std::unordered_set<KeyType> extractKeys(const std::unordered_map<KeyType, ValueType>& inputMap) {
//    std::unordered_set<KeyType> keys;
//    for (const auto& pair : inputMap) {
//        keys.insert(pair.first);
//    }
//    return keys;
//}
//
//class NameSet{
//        public:
//        vector<string> lleft, rleft;
//        NameSet(){}
//    NameSet(unordered_set<string> l, unordered_set<string> r){
//        for(auto &elem: l){
//            if (r.find(elem) == r.end() ){
//                lleft.push_back(elem);
//            }
//        }
//        for (auto &elem: r){
//            if (l.find(elem) == l.end()){
//                rleft.push_back(elem);
//            }
//        }
//    }
//
//    NameSet(unordered_set<string> r){
//        for (auto &elem: r){
//                rleft.push_back(elem);
//        }
//    }
//};
class PhyEncoder{
public:
    unordered_map<Target*, OpertaionGroup> opgs;
    OpertaionGroup phyops;
    Topo* pre_topo, *cur_topo;

    PhyEncoder(Topo* _pre_topo, Topo* _cur_topo){
        pre_topo = _pre_topo;
        cur_topo = _cur_topo;
    }
    void encode_intf(Intf* pre, Intf* cur){
        if (pre == nullptr){
            //add intf(link intf1 intf2 up)
            assert(cur->has_pair() && "intf should has pair");
            auto l = Link(cur, cur->pair());
            if (l.getLIntf() == cur) {
                phyops.add_op(fmt::format("link {} {} up", l.getLIntf()->getName(), l.getRIntf()->getName()));
            }
        }else if (cur == nullptr){
            //del intf(link intf1 intf2 remove)
            auto pair_node = pre->pair()->getNode();
            assert(pre->has_pair() && !cur_topo->nodes.get(pair_node->getId())->intfs.contains(pre->pair()->getId())&&"pre intf should be deleted too");
            auto l = Link(pre, pre->pair());
            if (l.getLIntf() == pre) {
                phyops.add_op(fmt::format("link {} {} remove", l.getLIntf()->getName(), l.getRIntf()->getName()));
            }
        }else{
            if (cur->up && !pre->up){
                phyops.add_op(fmt::format("intf {} up", cur->getName()));
            }
            if (!cur->up && pre->up){
                phyops.add_op(fmt::format("intf {} down", cur->getName()));
            }
        }
    }
//
    void encode_intfs(Node* pre, Node* cur){
        if (pre == nullptr){
            for (auto pair: cur->intfs.value()){
                encode_intf(nullptr, pair);
            }
        }else{
            for(auto node: cur->intfs.sub(pre->intfs).value()){
                encode_intf(nullptr, node);
            }
            for(auto node: pre->intfs.sub(cur->intfs).value()){
                encode_intf(node, nullptr);
            }
            for(auto node: pre->intfs.join(cur->intfs).key()){
                encode_intf(pre->intfs.get(node), cur->intfs.get(node));
            }
        }
    }

    void encode_node(Node* pre, Node* cur){
        //node add/del
        if (pre == nullptr){
            phyops.add_op(fmt::format("node {} add", cur->getName()));
        }else if (cur == nullptr){
            phyops.add_op(fmt::format("node {} del", cur->getName()));
        }
        //router set ospf TODO

        //intf
    }

    void encode_intfs_persudo(Topo* pre, Topo* cur){
        if (pre == nullptr){
            for (auto node: cur->nodes.value()){
                encode_intfs(nullptr, node);
            }
        }else{
            for(auto node: cur->nodes.sub(pre->nodes).value()){
                encode_intfs(nullptr, node);
            }
            for(auto node: pre->nodes.sub(cur->nodes).value()){
                encode_intfs(node, nullptr);
            }
            for(auto node: pre->nodes.join(cur->nodes).key()){
                encode_intfs(pre->nodes.get(node), cur->nodes.get(node));
            }

        }
    }
    void encode_nodes(Topo* pre, Topo* cur){
        if (pre == nullptr){
            for (auto node: cur->nodes.value()){
                encode_node(nullptr, node);
            }
        }else{
            for(auto node: cur->nodes.sub(pre->nodes).value()){
                encode_node(nullptr, node);
            }
            for(auto node: pre->nodes.sub(cur->nodes).value()){
                encode_node(node, nullptr);
            }
            for(auto node: pre->nodes.join(cur->nodes).key()){
                encode_node(pre->nodes.get(node), cur->nodes.get(node));
            }

        }
    }

    void encode(){
        encode_nodes(pre_topo, cur_topo);
        encode_intfs_persudo(pre_topo, cur_topo);
    }
};

#endif //FUZZER_PHY_GEN_H
