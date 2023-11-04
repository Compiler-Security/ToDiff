package org.generator.util.collections;

public record Pair<T1, T2>(T1 first, T2 second) {
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return "<" + this.first + ", " + this.second + ">";
    }

    public T1 first() {
        return this.first;
    }

    public T2 second() {
        return this.second;
    }
}
