package com.grits.orderservice.repository;

import com.grits.orderservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    boolean existsByName(String name);
}
