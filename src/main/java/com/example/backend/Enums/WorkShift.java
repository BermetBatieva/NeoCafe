package com.example.backend.Enums;

public enum WorkShift {
    DAY_SHIFT(1),
    NIGHT_SHIFT(2),
    DAY_OFF(3);

    private final int id;

    private int getId() {
        return id;
    }

    WorkShift(int id) {
        this.id = id;
    }

    public static WorkShift workShift(int id ){
        for(WorkShift workShift : WorkShift.values())
            if(workShift.getId() == id)
                return workShift;
        return null;
    }
}
