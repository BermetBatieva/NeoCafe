package com.example.backend.repository;

import com.example.backend.Enums.OrderTypes;
import com.example.backend.Enums.OrderStatus;
import com.example.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findOrderById(Long id);

    List<Orders> findOrdersByClient(User user);

    List<Orders> findAllByBranchAndOrderTypeAndOrderStatus(Branches branches, OrderTypes orderTypes,
                                                           OrderStatus orderStatus);

    List<Orders> findAllByEmployee_Id(Long Id);

    List<Orders> findAllByEmployeeAndOrderStatus(User user, OrderStatus orderStatus);

    @Query("SELECT e FROM Orders e WHERE e.client.id =:idClient ORDER BY e.orderTime DESC")
    List<Orders> orderHistory(Long idClient);

    Long findClientIdById(Long orderId);

    List<Orders> findAllByEmployeeAndOrderStatusAndOrderType(User user,OrderStatus orderStatus,
                                                             OrderTypes orderTypes);

    List<Orders> findAllByBranchAndOrderStatusAndOrderType(Branches branches,
                                                           OrderStatus orderStatus,
                                                           OrderTypes orderTypes);

    List<Orders> findAllByClientIdAndOrderStatusOrOrderStatusOrOrderStatus(Long clientId,
                                                 OrderStatus status1, OrderStatus status2, OrderStatus status3);
    List<Orders> findAllByClientIdAndOrderStatusOrderByOrderTimeDesc(Long clientId, OrderStatus status1);


    Orders findByIdAndEmployee(Long idOrder,User empId);

    Orders findByTable_IdAndEmployee_Id(long tableId, long emp_id);

    Orders findByBranch_Id(Long Id);

}
