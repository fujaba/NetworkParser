package de.uniks.networkparser.ext.petaf;

public enum MessageTyp {
    UPDATE, // UPDATE only send basic info
    INFO, // Info send more Infos
    ATTRIBUTES,// ATTRIBUTES send all Attri9butes of Remote Proxy
    CONNECT, // CONNECT TO CONNECT NODE TO EXISTING CLOUD
}
