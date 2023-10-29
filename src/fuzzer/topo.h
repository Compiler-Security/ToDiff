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


struct OSPFIntf;

enum NodeType{
    Router,
    Host,
    Switch
};

class Name{
public:
    virtual string getName() const = 0;
};

//class NodeName:Name{
//public:
//    int id;
//    NodeType typ;
//    NodeName(){}
//    NodeName(int _id, NodeType _typ){
//        id = _id;
//        typ = _typ;
//    }
//
//    string getName() const override{
//        switch (typ){
//            case Router:
//                return fmt::format("r{}", id);
//            case Switch:
//                return fmt::format("s{}", id);
//            default:
//                assert(false && "name type error");
//        }
//    }
//
//    NodeType getType(){
//        return typ;
//    }
//
//    bool operator==(const NodeName& other) const{
//        return getName() == other.getName();
//    }
//};



//class IntfName:Name{
//public:
//    int id;
//    NodeName nodename;
//    IntfName(){}
//    IntfName(NodeName _nodename, int _id){
//        nodename = std::move(_nodename);
//        id = _id;
//    }
//
//    string getName() const override{
//        return fmt::format("{}-eth{}", nodename.getName(), id);
//    }
//
//    bool operator==(const IntfName& other) const{
//        return getName() == other.getName();
//    }
//};

struct Intf;
struct Node:Name{

    string name;
    unordered_map<string, Intf> intfs;

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

struct Link;
struct Intf:Name{
    bool up;
    unique_ptr<OSPFIntf> ospf;

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
        link = nullptr;
    }

    void setLink(shared_ptr<Link> _link){
        link = std::move(_link);
    }

    void unsetLink(){
        link.reset();
    }

private:
    Node* node;
    int id;
    string name;
    shared_ptr<Link> link;
};


//class LinkName:Name{
//public:
//    string intf1_name, intf2_name;
//    LinkName(){}
//    LinkName(string _intf1_name, string _intf2_name){
//        intf1_name = _intf1_name;
//        intf2_name = _intf2_name;
//    }
//
//    string getName() const override{
//        return fmt::format("{}->{}", intf1_name, intf2_name);
//    }
//    bool operator==(const LinkName& other) const{
//        return getName() == other.getName();
//    }
//};

struct Link:Name{
    static shared_ptr<Link> new_link(Intf* intf1, Intf* intf2){
        auto l = make_shared<Link>(intf1, intf2);
        intf1->setLink(l);
        intf2->setLink(l);
        return l;
    }

    static void del_link(shared_ptr<Link> l){
        l->intfl->unsetLink();
        l->intfr->unsetLink();
    }

    Link(Intf* intf1, Intf* intf2){
        if (intf1->getNode()->getType() > intf2->getNode()->getType()) swap(intf1, intf2);
        intfl = intf1;
        intfr = intf2;
        name = fmt::format("{}->{}", intf1->getName(), intf2->getName());
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

    ~Link(){
        //printf("link deconstruct");
    }

private:
    Intf* intfl, *intfr;
    string name;
};

enum AType{

};

struct Area{
    int id;
    AType typ;
};

struct OSPFIntf{
    Intf* intf;

    int vrf;
    Area* area;
    int cost;

    OSPFIntf(){}
};

struct RNode;
struct OSPF{
    RNode* router;
};

//struct Link{
//
//    weak_ptr<RNode>
//};




struct OSPF;
struct RNode:Node{
    unique_ptr<OSPF> ospf;

    RNode(int _id): Node(_id, Router){}
};

struct SNode:Node{
    SNode(int _id):Node(_id, Switch){}
};



#endif //FUZZER_TOPO_H
