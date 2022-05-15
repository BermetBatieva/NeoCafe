package com.example.backend.repository;

import com.example.backend.entity.Additionals;
import com.example.backend.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AdditionalsRepository extends JpaRepository<Additionals,Long> {

    List<Additionals> findAllByOrderDetail(OrderDetails orderDetails);

    List<Additionals> findAllByOrderDetailId(Long orderDetailsId);

}
