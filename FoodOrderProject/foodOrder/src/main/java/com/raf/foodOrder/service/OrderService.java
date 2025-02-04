package com.raf.foodOrder.service;

import com.raf.foodOrder.exceptions.*;
import com.raf.foodOrder.model.*;
import com.raf.foodOrder.repository.DishRepository;
import com.raf.foodOrder.repository.ErrorMessageRepository;
import com.raf.foodOrder.repository.OrderRepository;
import com.raf.foodOrder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private DishRepository dishRepository;
    private ErrorMessageRepository errorMessageRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, DishRepository dishRepository, ErrorMessageRepository errorMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.errorMessageRepository = errorMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<Dish> findAllDishes(){

        List<Dish> dishes = dishRepository.findAll();
        return dishes;
    }

    public List<Order> findAll(){

        List<Order> orders = orderRepository.findAllByOrderByIdDesc();
        return orders;
    }

    public List<Order> ordersForUser(int userId){

        List<Order> userOrders = orderRepository.findByUserIdOrderByIdDesc(userId);
        return userOrders;
    }

    public List<Order> ordersByStatus(SearchByStatusRequest listOfStatus){

        String []s = listOfStatus.getListOfStatus().split(",");
        List<OrderStatus> orderStatuses = new ArrayList<>();
        for(String status : s){

            if(OrderStatus.valueOf(status).equals(OrderStatus.ORDERED) ||
                    OrderStatus.valueOf(status).equals(OrderStatus.PREPARING) ||
                    OrderStatus.valueOf(status).equals(OrderStatus.IN_DELIVERY) ||
                    OrderStatus.valueOf(status).equals(OrderStatus.CANCELED) ||
                    OrderStatus.valueOf(status).equals(OrderStatus.DELIVERED)){
                orderStatuses.add(OrderStatus.valueOf(status));
            }
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        String userRole = userDetails.getRole();
        List<Order> ordersByStatus;
        if(userRole.equals("ADMIN")){

            ordersByStatus = orderRepository.findByOrderStatusIn(orderStatuses);
        }
        else{

            ordersByStatus = orderRepository.findByOrderStatusInAndUserId(orderStatuses, userId);
        }

        return ordersByStatus;
    }

    public List<Order> filterOrdersBetweenDates(LocalDate startDate, LocalDate endDate){

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        String userRole = userDetails.getRole();
        List<Order> orders;
        if(userRole.equals("ADMIN")){

            orders = orderRepository.findAllByOrderByIdDesc();
        }
        else{

            orders = orderRepository.findByUserIdOrderByIdDesc(userId);
        }

        return orders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getCreatedDate().toLocalDate();
                    return (orderDate.isEqual(startDate) || orderDate.isAfter(startDate)) &&
                            (orderDate.isEqual(endDate) || orderDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
    }

    public List<Order> ordersByUserId(String userId){

        int id = Integer.parseInt(userId);
        if(id == 0){
            return findAll();
        }
        return ordersForUser(id);
    }


    public Order scheduleOrder(ScheduleOrderRequest scheduleOrderRequest) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime scheduledTime = LocalDateTime.parse(scheduleOrderRequest.getScheduledTime(), formatter);

        Order order = new Order();
        String items = scheduleOrderRequest.getDishes();
        String []i = items.split(",");
        for(String dish : i){

            Dish d = dishRepository.findByName(dish);

            if(d != null){
                if(order.getItems() != null && !order.getItems().contains(d)){

                    order.getItems().add(d);
                }
            }
        }

        order.setOrderStatus(OrderStatus.SCHEDULED);
        order.setScheduledTime(scheduledTime);

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        Optional<User> user = userRepository.findById(userId);
        User u;
        if(user.isPresent()){
            u = user.get();
            order.setUser(u);
            u.getOrders().add(order);
        }

        return orderRepository.save(order);
    }

    @Scheduled(fixedRate = 30000)
    public void processScheduledOrders() {

        LocalDateTime now = LocalDateTime.now();

        List<Order> orders = orderRepository.findAllByOrderStatusAndScheduledTimeBefore(OrderStatus.SCHEDULED, now);

        long activeOrders = orderRepository.countActiveOrders(List.of(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY));

        for (Order order : orders) {
            processOrder(order,activeOrders);
        }
    }

    @Transactional
    public void processOrder(Order order, long activeOrders) {
        if (activeOrders >= 3) {
            saveErrorMessage(order, "Maximum number of concurrent orders exceeded.");
        } else {
            createOrder(order);
        }
    }

    private void saveErrorMessage(Order order, String message) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setOperations("CREATE_ORDER");
        errorMessage.setMessage(message);
        errorMessage.setDate(LocalDateTime.now());
        errorMessage.setUser(order.getUser());
        errorMessage.setOrder(order);

        order.setOrderStatus(OrderStatus.CANCELED);

        orderRepository.save(order);

        errorMessageRepository.save(errorMessage);

        messagingTemplate.convertAndSend("/topic/messages", order);
    }

    private void createOrder(Order order) {
        order.setOrderStatus(OrderStatus.ORDERED);
        messagingTemplate.convertAndSend("/topic/messages", order);
        orderRepository.save(order);
    }


    public Order createOrder(CreateOrderRequest createOrderRequest){

        long activeOrders = orderRepository.countActiveOrders(List.of(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY));
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        Optional<User> user = userRepository.findById(userId);
        User u;
        if(user.isPresent()){
            u = user.get();
        }
        else{
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        if (activeOrders >= 3) {

            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setOperations("CREATE_ORDER");
            errorMessage.setMessage("Maximum number of concurrent orders exceeded.");
            errorMessage.setDate(LocalDateTime.now());
            errorMessage.setUser(u);

            errorMessageRepository.save(errorMessage);

            throw new OrderLimitExceededException("Maximum number of concurrent orders exceeded.");
        }

        Order order = new Order();
        order.setUser(u);
        String items = createOrderRequest.getOrder();
        String []i = items.split(",");
        for(String dish : i){

            Dish d = dishRepository.findByName(dish);

            if(d != null){
                if(order.getItems() != null && !order.getItems().contains(d)){

                    order.getItems().add(d);
                }
            }
        }
        u.getOrders().add(order);
        orderRepository.save(order);
        return order;
    }


    public Order cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        if (!order.getOrderStatus().equals(OrderStatus.ORDERED)) {

            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            int userId = userDetails.getId();
            Optional<User> user = userRepository.findById(userId);
            User u = null;
            if(user.isPresent()){
                u = user.get();
            }
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setDate(LocalDateTime.now());
            errorMessage.setOrder(order);
            errorMessage.setOperations("CANCEL_ORDER");
            errorMessage.setMessage("Order cannot be canceled as it is not in ORDERED status");
            errorMessage.setUser(u);
            errorMessageRepository.save(errorMessage);

            throw new OrderNotCancellableException("Order cannot be canceled as it is not in ORDERED status");
        }

        order.setActive(false);
        order.setOrderStatus(OrderStatus.CANCELED);
        messagingTemplate.convertAndSend("/topic/messages", order);

        try {
            orderRepository.save(order);
        } catch (OptimisticLockException e) {

            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            int userId = userDetails.getId();
            Optional<User> user = userRepository.findById(userId);
            User u = null;
            if(user.isPresent()){
                u = user.get();
            }
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setDate(LocalDateTime.now());
            errorMessage.setOrder(order);
            errorMessage.setOperations("CANCEL_ORDER");
            errorMessage.setMessage("Order update failed due to version conflict. Order has already been canceled by another user.");
            errorMessage.setUser(u);
            errorMessageRepository.save(errorMessage);
            throw new MyOptimisticLockException("Order update failed due to version conflict. Order has already been canceled by another user.");
        }
        return order;
    }


    @Scheduled(fixedRate = 5000)
    public void updateOrdersToPreparing() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> orders = orderRepository.findAllByOrderStatus(OrderStatus.ORDERED);

        for (Order order : orders) {
            Random random = new Random();
            int deviation = random.nextInt(3);
            if (order.getCreatedDate().plusSeconds(10 + deviation).isBefore(now)) {
                order.setOrderStatus(OrderStatus.PREPARING);
                order.setPreparingTime(now);
                orderRepository.save(order);

                messagingTemplate.convertAndSend("/topic/messages", order);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void updateOrdersToInDelivery() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> orders = orderRepository.findAllByOrderStatus(OrderStatus.PREPARING);

        for (Order order : orders) {
            Random random = new Random();
            int deviation = random.nextInt(3);
            if (order.getPreparingTime() != null && order.getPreparingTime().plusSeconds(15 + deviation).isBefore(now)) {
                order.setOrderStatus(OrderStatus.IN_DELIVERY);
                order.setDeliveryTime(now);
                orderRepository.save(order);

                messagingTemplate.convertAndSend("/topic/messages", order);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void updateOrdersToDelivered() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> orders = orderRepository.findAllByOrderStatus(OrderStatus.IN_DELIVERY);

        for (Order order : orders) {
            Random random = new Random();
            int deviation = random.nextInt(3);
            if (order.getDeliveryTime() != null && order.getDeliveryTime().plusSeconds(20 + deviation).isBefore(now)) {
                order.setOrderStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);

                messagingTemplate.convertAndSend("/topic/messages", order);
            }
        }
    }

    public Page<ErrorMessage> getAllErrorMessages(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return errorMessageRepository.findAll(pageRequest);
    }


    public Page<ErrorMessage> getErrorMessagesByUserId(int userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return errorMessageRepository.findByUserId(userId, pageRequest);
    }
}
