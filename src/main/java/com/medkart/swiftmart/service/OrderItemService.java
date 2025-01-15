package com.medkart.swiftmart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.medkart.swiftmart.dto.OrderItemDto;
import com.medkart.swiftmart.dto.OrderRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Order;
import com.medkart.swiftmart.entity.OrderItem;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.enums.OrderStatus;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.OrderItemRepo;
import com.medkart.swiftmart.repository.OrderRepo;
import com.medkart.swiftmart.repository.ProductRepo;
import com.medkart.swiftmart.specification.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final UserService userService;

    public Response placeOrder(OrderRequest orderRequest) {
        User user = userService.getLoginUser();
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(orderItemRequest -> {
                    Product product = productRepo.findById(orderItemRequest.getProductId())
                            .orElseThrow(() -> new NotFoundException("Product not found with id: " + orderItemRequest.getProductId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(orderItemRequest.getQuantity());

                    // Check if requested quantity exceeds the product stock
                    if (orderItemRequest.getQuantity() > product.getQty()) {
                        orderItem.setStatus(OrderStatus.CANCELLED); // Mark as rejected
                        orderItem.setPrice(BigDecimal.ZERO); // Set price to zero
                    } else {
                        orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())));
                        orderItem.setStatus(OrderStatus.PENDING);
                    }

                    orderItem.setUser(user);
                    return orderItem;
                }).toList();

        BigDecimal totalPrice = orderRequest.getTotalPrice() != null && orderRequest.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
                ? orderRequest.getTotalPrice()
                : orderItems.stream().filter(orderItem -> orderItem.getStatus() != OrderStatus.CANCELLED)
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderItemList(orderItems);
        order.setTotalPrice(totalPrice);

        // Setting order reference in each OrderItem
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepo.save(order);

        return Response.builder()
                .status(200)
                .message("Order Placed Successfully")
                .build();
    }

    public Response updateOrderItemStatus(Long orderItemId , String status) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with id: " + orderItemId));

        orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderItemRepo.save(orderItem);

        return Response.builder()
                .status(200)
                .message("Order Item Updated Successfully")
                .build();
    }

    public Response filterOrderItems(OrderStatus status , LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        Specification<OrderItem> spec = Specification.where(OrderItemSpecification.hasStatus(status))
                .and(OrderItemSpecification.createdBetween(startDate, endDate))
                .and(OrderItemSpecification.hasItemId(itemId));
        Page<OrderItem> orderItemPage = orderItemRepo.findAll(spec, pageable);

        if(orderItemPage.isEmpty()) {
            return Response.builder()
                    .status(200)
                    .message("No Order Items Found")
                    .build();
        }

        List<OrderItem> orderItemList = orderItemPage.getContent();
        List<OrderItemDto> orderItemDtoList = orderItemList.stream().map(entityDtoMapper::mapOrderItemToDtoBasic).toList();

        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtoList)
                .build();
    }

}
