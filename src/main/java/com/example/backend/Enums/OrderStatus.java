package com.example.backend.Enums;

public enum OrderStatus {
    PENDING(1),// ожидает принятия
    IP(2),// в процессе
    READY(3), // готовый
    CLOSED(4), // закрытый
    CANCELED(5);//отменен

    private final int id;

    private int getId() {
        return id;
    }

    OrderStatus(int id) {
        this.id = id;
    }

    public static OrderStatus getStatus(int id ){
        for(OrderStatus unit : OrderStatus.values())
            if(unit.getId() == id)
                return unit;
        return null;
    }
}
