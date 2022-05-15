package com.example.backend.Enums;

public enum EUnit {
    гр(1),
    мл(2),
    л(3),
    шт(4);//штук

    private int id;

    public int getId() {
        return id;
    }

    EUnit(int id) {
        this.id = id;
    }

    public static EUnit getUnit(int id ){
        for(EUnit unit : EUnit.values())
            if(unit.getId() == id)
                return unit;
        return null;
    }


}
