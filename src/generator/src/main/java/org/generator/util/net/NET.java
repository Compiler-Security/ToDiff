// Java
package org.generator.util.net;

import java.util.Objects;

public class NET {
    private String area;       // 区域部分，例如 "ab.cdef"
    private String systemId;   // 系统 ID，例如 "1234.5678.9abc"
    private String selector;   // 选择器，通常为 "00"

    public static NET of(String netStr) {
        var net = new NET();
        if (net.fromStr(netStr, false)) {
            return net;
        } else return null;
    }

    protected boolean fromStr(String netStr, boolean strict) {
        netStr = netStr.trim();
        String[] parts = netStr.split("\\.");
        if (parts.length != 6) {
            if (strict) {
                throw new IllegalArgumentException("无效的 NET 地址格式");
            }
            return false;
        }
        this.area = parts[0] + "." + parts[1];                   // ab.cdef
        this.systemId = parts[2] + "." + parts[3] + "." + parts[4]; // 1234.5678.9abc
        this.selector = parts[5];                                // 00
        return true;
    }

    public String getArea() {
        return area;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSelector() {
        return selector;
    }

    @Override
    public String toString() {
        return area + "." + systemId + "." + selector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NET net = (NET) o;
        return this.toString().equals(net.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public NET copy() {
        return NET.of(this.toString());
    }
}