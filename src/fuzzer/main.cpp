#include <iostream>
#include "topo.h"
#include "operation/phy_gen.h"

shared_ptr<Topo> get_topo_1(){
    auto t = make_shared<Topo>();
    auto r0 = make_shared<RNode>(0);
    auto r1 = make_shared<SNode>(0);
    t->nodes.add(r0);
    t->nodes.add(r1);
    auto r0_intf = make_shared<Intf>(r0.get(), 0);
    auto r1_intf = make_shared<Intf>(r1.get(), 0);
    t->nodes.get(0)->intfs.add(r0_intf);
    t->nodes.get(1)->intfs.add(r1_intf);
    r0_intf->pair_intf = r1_intf.get();
    r1_intf->pair_intf = r0_intf.get();
    return t;
}

shared_ptr<Topo> get_topo_2(){
    auto t = make_shared<Topo>();
    auto r0 = make_shared<RNode>(0);
    auto r1 = make_shared<SNode>(0);
    t->nodes.add(r0);
    t->nodes.add(r1);
    auto r0_intf = make_shared<Intf>(r0.get(), 0);
    auto r1_intf = make_shared<Intf>(r1.get(), 0);
    t->nodes.get(0)->intfs.add(r0_intf);
    t->nodes.get(1)->intfs.add(r1_intf);
    r0_intf->pair_intf = r1_intf.get();
    r1_intf->pair_intf = r0_intf.get();
    r0_intf->up = false;
    return t;
}

int main() {
    auto t1 = get_topo_1();
    auto t2 = get_topo_2();
    auto p = PhyEncoder(nullptr, t1.get());
    p.encode();
    cout << p.phyops.str() << endl;
    auto p1 = PhyEncoder(t1.get(), t2.get());
    p1.encode();
    cout << "=======" << endl;
    cout << p1.phyops.str() << endl;
}
