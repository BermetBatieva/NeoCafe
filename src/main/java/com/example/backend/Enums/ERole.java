package com.example.backend.Enums;


import org.springframework.security.core.GrantedAuthority;


public enum ERole implements GrantedAuthority {
    ROLE_ADMIN("ADMIN",1),//1
    ROLE_WAITER("WAITER",2),//2
    ROLE_BARISTA("BARISTA", 3),
    ROLE_CLIENT("CLIENT",4);

    public final String name;

    private int getId() {
        return id;
    }

    private int id;

    ERole(String name, int id){
        this.name = name;
        this.id = id;
    }



    public static ERole getRole(int id ){
        for(ERole role : ERole.values())
            if(role.getId() == id)
                return role;
        return null;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }
}