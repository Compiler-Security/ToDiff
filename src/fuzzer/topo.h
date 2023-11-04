//
// Created by 水兵 on 2023/10/29.
//


#ifndef FUZZER_TOPO_H
#define FUZZER_TOPO_H
#include<string>
#include<memory>
#include <utility>
#include<fmt/format.h>
#include"util.h"
using namespace std;





//===============Physical===================

enum NodeType{
    Router,
    Host,
    Switch
};

class NameObj{
public:
    virtual string getName() const{
        return name;
    }

    bool is_exist(){
        return exist;
    }

    void add(){
        exist = true;
    }

    void del(){
        exist = false;
    }

protected:
    bool exist;
    int id;
    string name;
};


class Intf;
class Node: public NameObj{
public:
    NumSet<Intf> intfs;
    NodeType getType(){
        return typ;
    }
    Node(){}
    Node(int _id, NodeType _typ){
        id = _id;
        typ = _typ;
        //calc name
        switch (typ){
            case Router:
                name = fmt::format("r{}", id);
                break;
            case Switch:
                name = fmt::format("s{}", id);
                break;
            default:
                assert(false && "name type error");
        }
        add();
    }

    int getId(){
        return id;
    }

private:
    NodeType typ;
};

class OSPF;

class RNode:public Node{
public:
    unique_ptr<OSPF> ospf;

    explicit RNode(int _id): Node(_id, Router){
        ospf.reset();
    }
};

class SNode:public Node{
public:
    explicit SNode(int _id):Node(_id, Switch){}
};

class OSPFIntf;
class Link;

class Intf:public NameObj{
public:
    bool up;
    OSPFIntf* ospf_intf;

    Node* getNode(){
        return node;
    }
    Intf(){}
    Intf(Node* _node, int _id){
        node = _node;
        id = _id;
        name = fmt::format("{}-eth{}", node->getName(), id);
        add();
        up = true;
        ospf_intf = nullptr;
        pair_intf = nullptr;
    }

    void reset(){
        up = false;
        ospf_intf = nullptr;
        pair_intf = nullptr;
    }

    bool has_pair(){
        return pair_intf != nullptr;
    }

    Intf* pair(){
        return pair_intf;
    }

    int getId(){
        return id;
    }

//    void setLink(shared_ptr<Link> _link){
//        unsetLink();
//        link = std::move(_link);
//    }
//
//    void unsetLink(){
//        link.reset();
//    }
    Intf* pair_intf;
private:
    Node* node;

//    shared_ptr<Link> link;
};


class Link{
public:
    static string get_link_name(Intf* intf1, Intf* intf2){
        if (intf1->getNode()->getType() > intf2->getNode()->getType()) swap(intf1, intf2);
        return fmt::format("{}->{}", intf1->getName(), intf2->getName());
    }

    Link(Intf* intf1, Intf* intf2){
        if (intf1->getNode()->getType() > intf2->getNode()->getType()) swap(intf1, intf2);
        intfl = intf1;
        intfr = intf2;
        name = get_link_name(intfl, intfr);
    }

    string getName() const{
        return name;
    }

    Intf* getLIntf(){
        return intfl;
    }

    Intf* getRIntf(){
        return intfr;
    }

private:
    Intf* intfl, *intfr;
    string name;
};


//===============OSPF=====================

enum AType{

};

class IP{

};
class Area{
    IP id;
    AType typ;
};

class OSPFIntf:public NameObj{
public:
    Intf* intf;

    int vrf;
    Area* area;
    int cost;

    OSPFIntf(Node* _node, int _id){
        id = _id;
        name = fmt::format("{}-eth{}", _node->getName(), id);
        intf = nullptr;
        vrf = 0;
        area = nullptr;
        cost = 0;
    }

};

enum OSPF_Status{
    Up,
    Down,
    Restart,
};

class OSPF:public NameObj{
public:
    IP ospf_id;
    OSPF_Status status;
    unordered_map<string, unique_ptr<OSPFIntf>> ospf_intfs;
    unordered_map<string, shared_ptr<Area>> ospf_areas;

    OSPF(RNode* rnode, IP _id){
        router = rnode;
        name = rnode->getName();
        ospf_id = _id;
        status = OSPF_Status::Up;
        ospf_intfs.clear();
        ospf_areas.clear();
    }

private:
    RNode* router;
};

//===========Graph===========
class Topo{
public:
    NumSet<Node> nodes;
};


#endif //FUZZER_TOPO_H
