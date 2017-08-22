package de.uniks.networkparser.ext.petaf.network;

public enum NodeProxyType {
    IN(0x1),
    OUT(0x2),
    INOUT(0x3);

    private int value;

    NodeProxyType(int value) {
        this.value = value;
    }

    public static boolean isInput(NodeProxyType value) {
        if (value == null) return false;
        return (value.value & 1) != 0;
    }

    public static boolean isOutput(NodeProxyType value) {
        if (value == null) return false;
        return (value.value & 2) != 0;
    }

    public boolean isInput() {
        return (value & 1) != 0;
    }

    public boolean isOutput() {
        return (value & 2) != 0;
    }
}
