//
// Created by 水兵 on 2023/10/30.
//

#ifndef FUZZER_UTIL_H
#define FUZZER_UTIL_H

#endif //FUZZER_UTIL_H

#include<vector>
#include <utility>
using namespace std;
template<class T>
class NumSet{
public:
    NumSet(){
        reset();
    }

    NumSet(int _maxNum){
        reset(_maxNum);
    }

    shared_ptr<T> del(int id){
        set_exist(id, false);
        auto p = _data[id];
        _data[id].reset();
        return move(p);
    }

    bool full(){
        for(int i = 0; i < exist.size(); i++){
            if (!contains(i)) return true;
        }
        return false;
    }

    int add(shared_ptr<T> p, int idx = -1){
        if (idx == -1){
            for(int i = 0; i < exist.size(); i++){
                if (!contains(i)){
                    idx = i;
                    break;
                }
            }
        }
        if (idx == -1){
            set_double();
            return add(p);
        }
        set_exist(idx, true);
        _data[idx] = p;
        return idx;
    }

    bool contains(int id){
        if(id >= exist.size()) return false;
        return exist[id];
    }

    shared_ptr<T> get_shared(int id){
        if (contains(id)){
            return _data[id];
        }else{
            return nullptr;
        }
    }

    T* get(int id){
        if (contains(id)){
            return _data[id].get();
        }else{
            return nullptr;
        }
    }

    void reset(int _maxNum = 10){
        exist.clear();
        _data.clear();
        exist.resize(_maxNum);
        _data.resize(_maxNum);
        for(int i = 0; i < exist.size(); i++){
            exist[i] = false;
        }
        //_data.clear();
    }

    vector<int> key(){
        vector<int> keys;
        for(int i = 0; i < exist.size(); i++){
            if (exist[i]) keys.push_back(i);
        }
        return keys;
    }
    vector<T*> value(){
        vector<T*> values;
        for(int i = 0; i < exist.size(); i++){
            if (exist[i]) values.push_back(_data[i].get());
        }
        return values;
    }
    vector<pair<int, shared_ptr<T>>> item(){}
    NumSet sub(NumSet& n, bool keep_idx=false){
        NumSet p(_data.size());
        for(int i = 0; i < _data.size(); i++){
            if (contains(i) && !n.contains(i)){
                if (!keep_idx){
                    p.add(get_shared(i), -1);
                }else{
                    p.add(get_shared(i), i);
                }
            }
        }
        return p;
    }
    NumSet join(NumSet &n, bool keep_idx=false){
        NumSet p(_data.size());
        for(int i = 0; i < _data.size(); i++){
            if (contains(i) && n.contains(i)){
                if (!keep_idx){
                    p.add(get_shared(i), -1);
                }else{
                    p.add(get_shared(i), i);
                }
            }
        }
        return p;
    }
private:
    void set_exist(int id, bool _exist = true){
        exist[id] = _exist;
    }

    void set_double(){
        _data.resize(_data.size() * 2);
        exist.resize(_data.size() * 2);
        for(int i = exist.size() / 2; i < exist.size(); i++){
            exist[i] = 0;
        }
    }
private:
    vector<bool> exist;
    vector<shared_ptr<T>> _data;
};