package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.Orders;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.service.OrdersService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("Orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @ApiOperation(value = "Добавление заказов для клиента")
    @PostMapping("add")
    public ResponseEntity<?> addNewOrder(@RequestBody OrderDto orderDto) throws Exception {
        ordersService.addOrderForClient(orderDto);
        return new ResponseEntity<>("Успешно добавлено!", HttpStatus.OK);
    }

    @ApiOperation(value = "отменить заказ")
    @GetMapping("/change-status-cancel/{orderId}")
    public ResponseEntity<ChangeStatusDto> changeOrderCancel(
            @PathVariable Long orderId) throws Exception {
        return new ResponseEntity<>(ordersService.setStatusCancel(orderId), HttpStatus.OK);
    }

    @ApiOperation(value = "начать выполнение заказа")
    @GetMapping("/change-status-in-progress/{orderId}")
    public ResponseEntity<ChangeStatusDto> changeOrderIP(
            @PathVariable Long orderId) throws Exception {
        return new ResponseEntity<>(ordersService.setStatusInProgress(orderId), HttpStatus.OK);
    }

    @ApiOperation(value = "отметить заказ как готовое")
    @GetMapping("/change-status-ready/{orderId}")
    public ResponseEntity<ChangeStatusDto> changeOrderReady(
            @PathVariable Long orderId) throws Exception {
        return new ResponseEntity<>(ordersService.setStatusReady(orderId), HttpStatus.OK);
    }

    //changes the status of order to CLOSED and adds bonuses to client
    @ApiOperation(value = "закрыть счет (заказ)")
    @GetMapping("/change-status-close/{id}")
    public Orders closeOrder(@PathVariable Long id) throws Exception {
        return ordersService.closeOrder(id);
    }

    @ApiOperation(value = "получение заказов текущего юзера(официанта)")
    @GetMapping("get-all-orders-currentUser")
    public List<OrdersDtoForWaiter> getAllOrdersByUser(){
        return  ordersService.allOrdersByUser();
    }

    @ApiOperation(value = "получение заказов текущего юзера(официанта) по статусу")
    @GetMapping("get-all-orders-byCurrentUserAndStatus/{statusId}")
    public List<OrdersDtoForWaiter> getAllOrdersByUserAndStatus(@PathVariable Integer statusId){
        return ordersService.allOrdersByUserAndStatus(statusId);
    }

    @ApiOperation(value = "Добавление заказов для официанта")
    @PostMapping("add-for-waiter")
    public ResponseEntity<OrderDto> addNewOrderForWaiters(@RequestBody OrderDto orderDto) throws Exception {
        return new ResponseEntity<>(ordersService.addOrderForWaiter(orderDto), HttpStatus.OK);
    }

    @ApiOperation(value = "добавление сверху заказа нового блюда(добавить" +
            " блюдо в заказ со статусом \"новый\")")
    @PutMapping("update-for-waiter-and-barista")
    public ResponseEntity<UpdateOrderDetailsDTO> updateNewOrderForWaiters(@RequestBody UpdateOrderDetailsDTO updateOrderDetailsDTO) {

            return new ResponseEntity<>(ordersService.updateOrderDetails(updateOrderDetailsDTO),HttpStatus.OK);
    }

    @ApiOperation(value = "получить текущий заказ current клиента")
    @GetMapping("/orders/get-curr-order")
    public List<OrderCurrAndCompletedForClientDto> getCurrentOrder(){
        return ordersService.getCurrentOrderClean();
    }

    @ApiOperation(value = "получить все завершенные заказы current клиента")
    @GetMapping("/orders/get-completed-orders")
    public List<OrderCurrAndCompletedForClientDto> getCompletedOrders(){
        return ordersService.getCompletedOrdersClean();
    }
}


