package com.example.backend.Enums;

public enum Status {
    ACTIVATE(1),
    DELETED(2);

    private final int id;

    public int getId() {
        return id;
    }

    Status(int id) {
        this.id = id;
    }

    public static Status getStatus(int id ){
        for(Status status : Status.values())
            if(status.getId() == id)
                return status;
        return null;
    }
}
