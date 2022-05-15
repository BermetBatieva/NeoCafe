package com.example.backend.controller;

import com.example.backend.dto.OrderById;
import com.example.backend.dto.OrderDetailsForWaiterDto;

import com.example.backend.dto.OrderTypesDto;
import com.example.backend.service.OrderDetailsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("Order-details")
@RestController
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

//    @GetMapping("getAll")
//    public ResponseEntity<List<OrderDetails>> getAllProducts(){
//        List<OrderDetails> list = service.getAll();
//        return ResponseEntity.ok(list);
//    }

//    @PutMapping("update")
//    public ResponseEntity<String> updateProduct(@RequestBody OrderDetailsDto orderDetailsDto){
//        try {
//           orderDetailsService.updateOrderDetails(orderDetailsDto);
//            return ResponseEntity.ok("successfully updated");
//        } catch (ResourceNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    @ApiOperation(value = "получение деталей одного заказа по типу заказа(на вынос или в) для официанта и баристы")
    @PostMapping("get-all-byOrder/{orderId}")
    public List<OrderDetailsForWaiterDto> getAllDetailsByOrder(@PathVariable Integer orderId, @RequestBody OrderTypesDto orderTypes){
        return orderDetailsService.allOrderDetailsByOrders(orderId,orderTypes.getOrderTypes());
    }


    @ApiOperation(value = "получение иннфы о заказе по айдишке стола")
    @GetMapping("get-all-details-by-table/{tableId}")
    public OrderById getAllOrderDetailsByOrderId(@PathVariable Long tableId){
        return orderDetailsService.getAllDetailsByTable(tableId);
    }
}
