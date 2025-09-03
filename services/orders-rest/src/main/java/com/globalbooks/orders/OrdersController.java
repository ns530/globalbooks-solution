package com.globalbooks.orders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Orders service.
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    // In-memory orders for demo
    private static List<Order> orders = new ArrayList<>();
    private static int orderIdCounter = 1;

    static {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Java Programming", 1, 29.99));
        orders.add(new Order(orderIdCounter++, "John Doe", items, 29.99, "Pending"));
    }

    /**
     * Create a new order.
     * @param orderRequest The order data
     * @return Created order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order orderRequest) {
        orderRequest.setId(orderIdCounter++);
        orderRequest.setStatus("Pending");
        orders.add(orderRequest);
        return ResponseEntity.ok(orderRequest);
    }

    /**
     * Get order by ID.
     * @param id Order ID
     * @return Order if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        Optional<Order> order = orders.stream().filter(o -> o.getId() == id).findFirst();
        return order.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}