//
// Created by 水兵 on 2023/10/29.
//


#ifndef FUZZER_TOPO_H
#define FUZZER_TOPO_H
#include<string>
#include<memory>
#include <utility>
#include<fmt/format.h>
using namespace std;




//===============Physical===================

enum NodeType{
    Router,
    Host,
    Switch
};

class Name{
public:
    virtual string getName() const = 0;
};


class Intf;
class Node: public Name{
public:
    string name;
    unordered_map<string, unique_ptr<Intf>> intfs;

    
    NodeType getType(){
        return typ;
    }

    string getName() const override{
        return name;
    }

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
    }
private:
    int id;
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

class Intf:public Name{
public:
    bool up;
    OSPFIntf* ospf_intf;

    string getName() const override{
        return name;
    }

    Node* getNode(){
        return node;
    }

    Intf(Node* _node, int _id){
        node = _node;
        id = _id;
        name = fmt::format("{}-eth{}", node->getName(), id);
        up = true;
        ospf_intf = nullptr;
        link = nullptr;
    }

    void setLink(Link* _link){
        link = _link;
    }

    void unsetLink(){
        link == nullptr;
    }

private:
    Node* node;
    int id;
    string name;
    Link* link;
};


class Link:public Name{
public:
    static string get_link_name(Intf* intf1, Intf* intf2){
        if (intf1->getNode()->getType() > intf2->getNode()->getType()) swap(intf1, intf2);
        return fmt::format("{}->{}", intf1->getName(), intf2->getName());
    }

    Link(Intf* intf1, Intf* intf2){
        if (intf1->getNode()->getType() > intf2->getNode()->getType()) swap(intf1, intf2);
        intfl = intf1;
        intfr = intf2;
        name = get_link_name(intf1, intf2);
    }

    string getName() const override{
        return name;
    }

    Intf* getLIntf(){
        return intfl;
    }

    Intf* getRIntf(){
        return intfr;
    }

    void del_link(){
        intfl->unsetLink();
        intfr->unsetLink();
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

class OSPFIntf:public Name{
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

    string getName() const override{
        return name;
    }

private:
    int id;
    string name;
};

enum OSPF_Status{
    Up,
    Down,
    Restart,
};

class OSPF:public Name{
public:
    IP id;
    OSPF_Status status;
    unordered_map<string, unique_ptr<OSPFIntf>> ospf_intfs;
    unordered_map<string, Area*> ospf_areas;

    OSPF(RNode* rnode, IP _id){
        router = rnode;
        name = rnode->getName();
        id = _id;
        status = OSPF_Status::Up;
        ospf_intfs.clear();
        ospf_areas.clear();
    }

    string getName() const override{
        return name;
    }

private:
    string name;
    RNode* router;
};

#endif //FUZZER_TOPO_H
