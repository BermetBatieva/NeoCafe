package com.example.backend.Enums;

public enum TypeGeneralAdditional {
    CIRCLE(1),
    SQUARE(2);

    private final int id;

    private int getId() {
        return id;
    }

    TypeGeneralAdditional(int id) {
        this.id = id;
    }

    public static TypeGeneralAdditional getTypes(int id ){
        for(TypeGeneralAdditional unit : TypeGeneralAdditional.values())
            if(unit.getId() == id)
                return unit;
        return null;
    }
}
