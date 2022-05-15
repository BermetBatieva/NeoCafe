package com.example.backend.Enums;

public enum TableStatus {
    OCCUPIED(1),//занято
    FREE(2);

    private final int id;

    private int getId() {
        return id;
    }

    TableStatus(int id) {
        this.id = id;
    }

    public static TableStatus getStatus(int id ){
        for(TableStatus s : TableStatus.values())
            if(s.getId() == id)
                return s;
        return null;
    }
}
