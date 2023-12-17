package org.generator;

import org.generator.gen.RandomGen;

public class Main {
    public static void main(String[] args) {
        var gen = new RandomGen();
        var ls = gen.genRandom(50, 0.5, 0.3, 3, 0, 0.9, "r1");
        //System.out.println(ls);
    }
}