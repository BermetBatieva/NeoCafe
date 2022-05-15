package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.Branches;
import com.example.backend.entity.PushNotificationRequest;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.firebase.FCMService;
import com.example.backend.jwt.JwtUtils;
import com.example.backend.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TableService tableService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserDetailsServiceImpl userServiceImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BranchesService branchesService;

    @Autowired
    private ImageService imageService;

    @ApiOperation(value = "Авторизация админа")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody UserDto userDto) throws Exception {
        if (userServiceImpl.auth(userDto.getPhoneNumber())) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userDto.getPhoneNumber(),
                                userDto.getCode())
                );
            } catch (BadCredentialsException e) {
                throw new Exception("Неверный код!", e);
            }
            final UserDetails userDetails = userServiceImpl.loadUserByUsername(userDto.getPhoneNumber());
            return new ResponseEntity<>(jwtUtils.generateToken(userDetails), HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Добавление меню")
    @PostMapping("/menu/add")
    public ResponseEntity<MenuDTO> create(@RequestBody MenuDTO menuDTO) throws Exception {
        return new ResponseEntity<>(menuService.add(menuDTO), HttpStatus.OK);
    }

    @ApiOperation(value = "Добавление фотки к Меню")
    @PostMapping("/menu/image/{idMenu}")
    public void setPhoto(@RequestParam MultipartFile multipartFile,
                                           @PathVariable Long idMenu) throws IOException {
        imageService.setImage(multipartFile,idMenu);
    }

    @ApiOperation("Удаление меню")
    @DeleteMapping("/menu/delete/{menuId}")
    public DeletedDTO deleteMenuById(@PathVariable Long menuId) throws Exception {
        return menuService.deleteById(menuId);
    }

    @ApiOperation("Обновление меню")
    @PutMapping("/menu/update")
    public ResponseEntity<MenuForAdminDTO> updateById(@RequestBody MenuUpdateDTO menuDeleteDTO) throws Exception {
        return new ResponseEntity<>(menuService.update(menuDeleteDTO), HttpStatus.OK);
    }

    @ApiOperation("Весь меню")
    @GetMapping("/menu/{idCategory}")
    public List<MenuForAdminDTO> getAllMenu(@PathVariable Long idCategory) throws Exception {
        return menuService.allMenuForAdmin(idCategory);
    }

    @ApiOperation("Добавление новых продуктов (склад)")
    @PostMapping("/warehouse/add")
    public ResponseEntity<Product> addNewProduct(@RequestBody Product product) throws AlreadyExistsException {
        return new ResponseEntity<>(warehouseService.addProduct(product), HttpStatus.OK);
    }

    @ApiOperation("изменение данных о продукте")
    @PutMapping("/warehouse/update")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        return new ResponseEntity<>(warehouseService.update(product), HttpStatus.OK);
    }

    @ApiOperation("удаление продукта по id продукта")
    @DeleteMapping("/warehouse/delete/{productId}")
    public DeletedDTO deleteProductById(@PathVariable Long productId) {
        return warehouseService.removeById(productId);
    }

    @ApiOperation(value = "все продукты на складе")
    @GetMapping("/warehouse/{id}")
    public List<Product> getAllProductsWithWarehouse(@PathVariable int id) {
        return warehouseService.getAllForAdmin(id);
    }

    @ApiOperation(value = "заканчивающиеся продукты")
    @GetMapping("/warehouse/ending")
    public List<Product> getAllRunningOutProducts() {
        return warehouseService.getAllEndingProducts();
    }

    @ApiOperation("Добавление филилов")
    @PostMapping("/branch/add")
    public ResponseEntity<Branches> addNewBranch(@RequestBody BranchDto branch) throws Exception {
        return new ResponseEntity<>(branchesService.addBranch(branch), HttpStatus.OK);
    }

    @ApiOperation("Удаление филилов")
    @DeleteMapping("/branch/delete/{branchId}")
    public DeletedDTO branchDelete(@PathVariable Long branchId) throws Exception {
        return branchesService.deleted(branchId);
    }

    @ApiOperation("Обновление филиалов")
    @PutMapping("/branch/update")
    public ResponseEntity<Branches> branchUpdate(@RequestBody BranchDto branch) throws Exception {
        return new ResponseEntity<>(branchesService.update(branch), HttpStatus.OK);
    }

    @ApiOperation("Все филиалы")
    @GetMapping("/branch/all")
    public List<Branches> getAllBranches(){
        return branchesService.getAll();
    }

    @ApiOperation("Добавление сотрудников 2-Waiter, 3-Barista")
    @PostMapping("/employee/add")
    public ResponseEntity<EmployeeDto> addNewEmployee(@RequestBody EmployeeDto employeeDto) throws AlreadyExistsException {
        return new ResponseEntity<>(employeeService.registrationEmployee(employeeDto), HttpStatus.OK);
    }

    @ApiOperation("Добавление столов Waiter'у")
    @PutMapping("/employee/waiter/add-tables")
    public void addWaiterTables(@RequestBody WaiterTableDto waiterTableDto) {
        employeeService.addWaiterTables(waiterTableDto);
    }

    @ApiOperation("Удаление сотрдуника")
    @DeleteMapping("/employee/delete/{employeeId}")
    public DeletedDTO deletedEmployee(@PathVariable Long employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @ApiOperation("получение всех сотрудников по филиалу")
    @GetMapping("/employee/get-all/{branchId}")
    public List<EmployeeForAdmin> getAllEmployee(@PathVariable Long branchId) {
        return employeeService.getAllEmployee(branchId);
    }

    @ApiOperation("обновление даннных сотрудников")
    @PutMapping("/employee/update")
    public ResponseEntity<EmployeeForAdmin> updateEmployee(@RequestBody EmployeeUpdateDTO employeeDto) {
        return new ResponseEntity<>(employeeService.updateEmployee(employeeDto), HttpStatus.OK);
    }

    @ApiOperation(value = "добавление стола")
    @PostMapping("/table/add")
    public ResponseEntity<TableDTO> create(@RequestBody TableDTO tableDTO) throws AlreadyExistsException {
        return new ResponseEntity<>(tableService.add(tableDTO),HttpStatus.OK);
    }

    @ApiOperation(value = "полчуение всех столов")
    @GetMapping("/table/all")
    public List<TableDtoForAdmin> getAllTables(){
        return tableService.getAll();
    }

    @ApiOperation(value = "Удаление столов")
    @DeleteMapping("/table/delete/{tableId}")
    public DeletedDTO deleteTable(@PathVariable Long tableId){
        return tableService.delete(tableId);
    }

}