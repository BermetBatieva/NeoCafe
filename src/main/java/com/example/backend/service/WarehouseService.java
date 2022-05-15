package com.example.backend.service;

import com.example.backend.Enums.Status;
import com.example.backend.dto.DeletedDTO;
import com.example.backend.dto.Product;
import com.example.backend.Enums.EUnit;
import com.example.backend.Enums.EWarehouseCategory;
import com.example.backend.entity.Warehouse;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BranchesRepository;
import com.example.backend.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private BranchesRepository branchesRepository;

    public DeletedDTO removeById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Продукт с такой id не найдено! id = ", id));
        warehouse.setProductName("deleted name: "+warehouse.getProductName());
        warehouse.setStatus(Status.DELETED);
        warehouseRepository.save(warehouse);
        return new DeletedDTO(id);
    }

    public List<Product> getAllForAdmin(int id) {
        List<Warehouse> warehouseList = warehouseRepository.findAllByStatusAndWarehouseCategory(Status.ACTIVATE,
                EWarehouseCategory.getProduct(id));
        return setterProduct(warehouseList);
    }

    public List<Product> getAllEndingProducts(){
        List<Warehouse> warehouseList = warehouseRepository.findAllEndingProducts();
        return setterProduct(warehouseList);
    }

    private List<Product> setterProduct(List<Warehouse> warehouseList){
        List<Product> productList = new ArrayList<>();
        for (Warehouse wProduct : warehouseList) {
            Product product = new Product();
            product.setId(wProduct.getProductId());
            product.setWarehouseCategoryId(wProduct.getWarehouseCategory().getId());
            product.setUnit(wProduct.getUnit().getId());
            product.setProductName(wProduct.getProductName());
            product.setQuantity(wProduct.getQuantity());
            product.setMinNumber(wProduct.getMinNumber());
            product.setArrivalDate(wProduct.getArrivalDate());
            product.setExpirationDate(wProduct.getExpirationDate());
            product.setBranchId(wProduct.getBranches().getId());
            productList.add(product);
        }
        return productList;
    }

    public Product addProduct(Product product) throws AlreadyExistsException {
        if (warehouseRepository.existsByProductName(product.getProductName())) {
            throw new AlreadyExistsException("Продукт с таким именем уже существует!");
        } else {
            Warehouse newWarehouse = new Warehouse();
            return saverWarehouse(newWarehouse, product);
        }
    }

    public Product update(Product product) {
        Warehouse warehouse = warehouseRepository.findById(product.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Продукт с такой id не существует! id = ", product.getId()));
        saverWarehouse(warehouse, product);
        return product;
    }

    @Transactional
    Product saverWarehouse(Warehouse warehouse, Product product) {
        warehouse.setBranches(branchesRepository.findById(product.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Филиал с такой id не найден! id = ", product.getBranchId())));
        warehouse.setProductName(product.getProductName());
        warehouse.setArrivalDate(product.getArrivalDate());
        warehouse.setExpirationDate(product.getExpirationDate());
        warehouse.setMinNumber(product.getMinNumber());
        warehouse.setUnit(EUnit.getUnit(product.getUnit()));
        warehouse.setQuantity(product.getQuantity());
        warehouse.setWarehouseCategory(EWarehouseCategory.getProduct(product.getWarehouseCategoryId()));
        warehouse.setStatus(Status.ACTIVATE);
        warehouseRepository.save(warehouse);
        return product;
    }

}
