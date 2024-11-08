package org.generator.util.graph;

public abstract class AbstractEdge<N> implements Edge<N>{
    protected final N src;
    protected final N dst;
    protected AbstractEdge(N src, N dst){
        this.src = src;
        this.dst = dst;
    }

    public N getSrc(){return this.src;}
    public N getDst(){return this.dst;}
}
