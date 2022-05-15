package com.example.backend.Enums;

public enum OrderTypes {
    IN(1),
    OUT(2);

    private int id;

    private int getId() {
        return id;
    }

    OrderTypes(int id) {
        this.id = id;
    }

    public static OrderTypes getTypes(int id ){
        for(OrderTypes unit : OrderTypes.values())
            if(unit.getId() == id)
                return unit;
        return null;
    }
}
