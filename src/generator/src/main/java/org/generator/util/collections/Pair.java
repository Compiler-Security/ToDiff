package org.generator.util.collections;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
