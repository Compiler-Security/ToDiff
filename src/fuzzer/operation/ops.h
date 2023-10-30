//
// Created by 水兵 on 2023/10/29.
//

#ifndef FUZZER_OPS_H
#define FUZZER_OPS_H

#endif //FUZZER_OPS_H

#include "../topo.h"

enum TargetType{
    physic,
    ospf
};

enum PHYOp{

};

enum OSPFOp{

};

class Target{
public:
    NameObj* target;
    TargetType target_typ;
    template<class T>
    T* get_target(){
        return dynamic_pointer_cast<T>(target);
    }
};


class Attri{

};

class Cmd{
public:
    Cmd(){}
    Cmd(vector<string> _cmd, Attri _attri){
        cmds = _cmd;
        attri = _attri;
    }
    vector<string> cmds;
    Attri attri;

    string str(){
        string o = "";
        for (auto & v: cmds){
                o += v;
        }
        return o;
    }
};


class BaseOperation{
public:
    static int id_count;
    Target* target;
    Cmd cmds;
    int id;
    explicit BaseOperation(){
        id = id_count++;
        target = nullptr;
    }

    string str() {
        return cmds.str();
    }
};

class OpertaionGroup{
public:
//    Target* target;
//    vector<unique_ptr<BaseOperation>> ops;
//    string str(){}
//    void add_op(unique_ptr<BaseOperation> op){
//        ops.push_back(std::move(op));
//    }
    vector<string> ops;
    void add_op(string op){
        ops.push_back(op);
    }

    string str(){
        string st;
        for(auto &op: ops){
            st = st + op + "\n";
        }
        return st;
    }
};