package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.repository.GeneralAdditionalRepository;
import com.example.backend.service.MenuService;
import com.example.backend.service.NotificationsService;
import com.example.backend.service.OrdersService;
import com.example.backend.service.UserDetailsServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/barista")
public class BaristaController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private NotificationsService notificationsService;

    @ApiOperation(value = "получение всех блюд по категории")
    @GetMapping("/menu/category/{categoryId}")
    public List<MenuForBarista> getAllByCategoryIdForBarista(@PathVariable long categoryId) throws Exception {
        return menuService.allMenuByCategoryForBarista(categoryId);
    }

    @SneakyThrows
    @ApiOperation(value ="получение информации о блюде")
    @GetMapping("/menu/{id}")
    public MenuByID getMenuByIdForBarista(@PathVariable Long id) throws Exception {
        return menuService.getInfoById(id);
    }

    @ApiOperation(value ="получение состава меню")
    @GetMapping("/menu/general/{id}")
    public List<GeneralDTO> getGeneralAdditonal(@PathVariable Long id) throws Exception {
        return  menuService.getGeneral(id);
    }

    @ApiOperation("список заказов для баристы по типу заказа(в заведении или на вынос)")
    @PostMapping("/orders/all")
    public List<OrdersDtoForBarista> getOrdersByTypeAndStatus(@RequestBody TypeAndStatusForBaristaOrderDto dto){
        return ordersService.getOrdersByTypeAndStatus(dto.getOrderStatus(),dto.getOrderTypes());
    }

    @ApiOperation(value = "Добавление заказа для баристы")
    @PostMapping("/orders/add")
    public ResponseEntity<OrdersDtoForBarista> addNewOrderForBarista(
            @RequestBody OrderDtoBarista orderDtoBarista) throws Exception {
        return new ResponseEntity<>(ordersService.addOrderForBarista(orderDtoBarista), HttpStatus.OK);
    }

    @ApiOperation(value = "принятие заказа")
    @PutMapping("/change-status-in-progress/{orderId}")
    public ResponseEntity<ChangeStatusDto> setStatusInProgress(
            @PathVariable Long orderId) throws Exception {
        return new ResponseEntity<>(ordersService.setStatusInProgress(orderId), HttpStatus.OK);
    }

    @ApiOperation(value = "поменять статус на готовый")
    @PutMapping("/change-status-ready/{orderId}")
    public ResponseEntity<ChangeStatusDto> setStatusReady(
            @PathVariable Long orderId) throws Exception {
        return new ResponseEntity<>(ordersService.setStatusReady(orderId), HttpStatus.OK);
    }

    @ApiOperation(value = "получение личных данных")
    @GetMapping("/get-personal-data")
    public ResponseEntity<EmployeePersonalDataDTO> getPersonalData(){
        return new ResponseEntity<>(userService.getPersonalDate(),HttpStatus.OK);
    }

    @ApiOperation("страница после первой авторизации сотрудников")
    @PutMapping("/update-personal-data")
    public DataDto updateEmployee(@RequestBody DataDto model) throws Exception {
        return userService.updateData(model);
    }

}
