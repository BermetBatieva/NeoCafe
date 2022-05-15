package com.example.backend.repository;

import com.example.backend.Enums.OrderTypes;
import com.example.backend.entity.Menu;
import com.example.backend.entity.OrderDetails;

import com.example.backend.entity.Orders;
import com.example.backend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    List<OrderDetails> findAllByOrders(Orders orders);

    List<OrderDetails> findAllByOrdersAndOrders_OrderType(Orders orders, OrderTypes orderTypes);

    OrderDetails findByOrders_IdAndMenu_Id(Long orderId,Long menuId);

    List<OrderDetails> findAllByMenu_Id(Long menuId);

    List<OrderDetails> findAllByOrders_Id(Long id);
}
