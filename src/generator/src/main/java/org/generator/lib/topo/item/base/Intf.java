package org.generator.lib.topo.item.base;

public class Intf{
    public int area;
    public int networkId;

    public int cost;

    public int routerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id;
    public Intf(){
        id = -1;
        networkId = -1;
    }

    public Intf(int id, int routerId){
        this.id = id;
        this.routerId = routerId;
    }

    public Intf copy(){
        var intf  = new Intf(this.id, this.routerId);
        intf.cost = this.cost;
        intf.networkId = this.networkId;
        intf.area = this.area;
        return intf;
    }
}
