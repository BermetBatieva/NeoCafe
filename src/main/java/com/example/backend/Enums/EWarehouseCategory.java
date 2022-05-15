package com.example.backend.Enums;

public enum EWarehouseCategory {
    FINISHED_GOODS(1),//готовая продукция
    STUFF(2);//сырье

    private int id;

    public int getId() {
        return this.id;
    }

    EWarehouseCategory(int id) {
        this.id = id;
    }

    public static EWarehouseCategory getProduct(int id ){
        for(EWarehouseCategory product : EWarehouseCategory.values())
            if(product.getId() == id)
                return product;
        return null;
    }
}

