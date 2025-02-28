package org.generator.util.exec;

public enum ExecStat {
    SUCC,
    FAIL,
    MISS;

    public ExecStat join(ExecStat b){
        if (this == FAIL || b == FAIL) return FAIL;
        else if (this == MISS || b == MISS) return MISS;
        else return SUCC;
    }
}
