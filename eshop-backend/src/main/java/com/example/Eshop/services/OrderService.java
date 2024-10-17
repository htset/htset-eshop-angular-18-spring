package com.example.Eshop.services;

import com.example.Eshop.dtos.OrderDTO;
import com.example.Eshop.dtos.OrderDetailDTO;
import com.example.Eshop.models.Address;
import com.example.Eshop.models.Item;
import com.example.Eshop.models.Order;
import com.example.Eshop.models.OrderDetail;
import com.example.Eshop.repositories.AddressRepository;
import com.example.Eshop.repositories.ItemRepository;
import com.example.Eshop.repositories.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final AddressRepository addressRepository;
  private final ItemRepository itemRepository;

  public OrderService(OrderRepository orderRepository,
                      AddressRepository addressRepository,
                      ItemRepository itemRepository) {
    this.orderRepository = orderRepository;
    this.addressRepository = addressRepository;
    this.itemRepository = itemRepository;
  }

  @Transactional
  public OrderDTO createOrder(OrderDTO dto) {
    Order newOrder = new Order();
    newOrder.setUserId(dto.getUserId());
    newOrder.setOrderDate(LocalDateTime.now());

    Address tempAddr = addressRepository.findById(dto.getDeliveryAddressId())
        .orElseThrow(() -> new RuntimeException("Address not found"));
    newOrder.setFirstName(tempAddr.getFirstName());
    newOrder.setLastName(tempAddr.getLastName());
    newOrder.setStreet(tempAddr.getStreet());
    newOrder.setZip(tempAddr.getZip());
    newOrder.setCity(tempAddr.getCity());
    newOrder.setCountry(tempAddr.getCountry());

    BigDecimal totalPrice = BigDecimal.ZERO;

    for (OrderDetailDTO detail : dto.getOrderDetails()) {
      Item tempItem = itemRepository.findById(detail.getItemId())
          .orElseThrow(() -> new RuntimeException("Item not found"));
      OrderDetail newOrderDetail = new OrderDetail();
      newOrderDetail.setItemId(detail.getItemId());
      newOrderDetail.setItemName(tempItem.getName());
      newOrderDetail.setItemUnitPrice(tempItem.getPrice());
      newOrderDetail.setQuantity(detail.getQuantity());
      newOrderDetail.setTotalPrice(tempItem.getPrice()
          .multiply(detail.getQuantity()));

      newOrderDetail.setOrder(newOrder);
      newOrder.getOrderDetails().add(newOrderDetail);
      totalPrice = totalPrice.add(newOrderDetail.getTotalPrice());
    }

    newOrder.setTotalPrice(totalPrice);
    orderRepository.save(newOrder);
    return createDTOFromOrder(newOrder);
  }

  private OrderDTO createDTOFromOrder(Order order) {
    OrderDTO dto = new OrderDTO();
    dto.setId(order.getId());
    dto.setUserId(order.getUserId());
    dto.setOrderDate(order.getOrderDate());

    dto.setTotalPrice(order.getTotalPrice());

    for (OrderDetail detail : order.getOrderDetails()) {
      OrderDetailDTO dtoDetail = new OrderDetailDTO();
      dtoDetail.setId(detail.getId());
      dtoDetail.setOrderId(detail.getOrder().getId());
      dtoDetail.setItemId(detail.getItemId());
      dtoDetail.setItemName(detail.getItemName());
      dtoDetail.setItemUnitPrice(detail.getItemUnitPrice());
      dtoDetail.setQuantity(detail.getQuantity());
      dtoDetail.setTotalPrice(detail.getTotalPrice());
      dto.getOrderDetails().add(dtoDetail);
    }

    return dto;
  }

  public List<Order> findPaginatedOrders(int page, int pageSize, String search) {
    Pageable pageable = PageRequest.of(page, pageSize);
    if(search == null || search.isEmpty())
      return orderRepository.findAll(pageable).getContent();
    else
      return orderRepository.findAllBySearchCriteria(search, pageable);
  }

  public int getTotalOrderCount(String search) {
    if(search == null || search.isEmpty())
      return orderRepository.countAll();
    else
      return orderRepository.countBySearchCriteria(search);
  }

  public Optional<OrderDTO> getOrderById(Long id) {
    return orderRepository.findById(id).map(this::createDTOFromOrder);
  }
}
