package com.example.backend.service;

import com.example.backend.Enums.*;
import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.exception.*;
import com.example.backend.firebase.FCMService;
import com.example.backend.repository.*;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private BranchesRepository branchesRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private GeneralAdditionalRepository generalAdditionalRepository;

    @Autowired
    private AdditionalsRepository additionalsRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Transactional
    public OrderDto addOrderForClient(OrderDto orderDto) throws Exception {

        Orders orders = new Orders();
        orders.setClient(userDetailsService.getCurrentUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        orders.setOrderTime(convertLocalDateTimeToDateUsingInstant(localDateTime));
        orders.setOrderType(orderDto.getOrderType());
        orders.setTable(tableRepository.findById(orderDto.getTableId()).orElseThrow(
                () -> new ResourceNotFoundException("стол с таким id не существует! id = ",
                        orderDto.getTableId())));
        if (!tableService.isFree(orderDto.getTableId())) {
            throw new TableIsOccupiedException();
        }
        if (orderDto.getOrderType() == OrderTypes.IN && orderDto.getTableId() != null) {
            tableService.reserve(orderDto.getTableId());
        }
        orders.setBranch(branchesRepository.findById(orderDto.getBranchId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("филиал с таким id не существует! id = ", orderDto.getBranchId())));
        orders.setEmployee(tableRepository.getById(orderDto.getTableId()).getUser());
        orders.setOrderStatus(OrderStatus.PENDING);


        long orderId = ordersRepository.save(orders).getId();

        double orderTotalPrice = 0;

        for (OrderDetailsDto orderDetails : orderDto.getListOrderDetailsDto()) {
            orderTotalPrice += addOrderDetails(orderDetails, orderId);
        }

        ordersRepository.getById(orderId).setTotalPrice(orderTotalPrice);

        //отправка уведомления
        String bodyMessage = "";
        if (orders.getOrderType() == OrderTypes.IN)
            bodyMessage = "(В заведении)";
        else
            bodyMessage = "(На вынос)";
        /*PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
        pushNotificationRequest.setToken(userRepository.getByBranchesAndRole(orders.getTable().getBranches(),
                ERole.ROLE_BARISTA).getFirebaseToken());
        pushNotificationRequest.setTitle("Новый заказ! стол №" + orders.getTable().getId());*/


        for (OrderDetails o : orderDetailsRepository.findAllByOrders(orders)) {
            bodyMessage += o.getMenu().getName() + " x" + o.getQuantity() + ", ";
        }
       /* pushNotificationRequest.setMessage(bodyMessage);*/

        Notifications notifications = new Notifications();
        notifications.setStatus(Status.ACTIVATE);
        notifications.setTitle("Новый заказ! стол №" + orders.getTable().getId());
        notifications.setMessage(bodyMessage);
        notifications.setTime(new Date());
        notifications.setUser(userRepository.getByBranchesAndRole(orders.getTable().getBranches(),
                ERole.ROLE_BARISTA));
        notificationsRepository.save(notifications);

        return orderDto;
    }

    private Date convertLocalDateTimeToDateUsingInstant(LocalDateTime dateToConvert) {
        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Transactional //добавление заказа для официанта
    public OrderDto addOrderForWaiter(OrderDto orderDto) throws Exception {

        Orders orders = new Orders();

        orders.setEmployee(userDetailsService.getCurrentUser());
        orders.setOrderStatus(OrderStatus.PENDING);
        LocalDateTime localDateTime = LocalDateTime.now();
        orders.setOrderTime(convertLocalDateTimeToDateUsingInstant(localDateTime));
        orders.setOrderType(orderDto.getOrderType());
        if (orderDto.getOrderType() == OrderTypes.IN) {
            orders.setTable(tableRepository.findByIdAndUserAndTableStatus(orderDto.getTableId(),
                    userDetailsService.getCurrentUser(), TableStatus.FREE));
            Tables tables = tableRepository.findByUser_IdAndId(userDetailsService.getCurrentUser().getId(),
                    orders.getTable().getId());
            tables.setTableStatus(TableStatus.OCCUPIED);
        } else
            orders.setTable(null);


        orders.setBranch(userDetailsService.getCurrentUser().getBranches());
        long orderId = ordersRepository.save(orders).getId();

        double orderTotalPrice = 0;

        for (OrderDetailsDto orderDetails : orderDto.getListOrderDetailsDto()) {
            orderTotalPrice += addOrderDetails(orderDetails, orderId);
        }

        ordersRepository.findById(orderId).orElseThrow(() ->
                new ResourceNotFoundException("заказ с таким id не существует! id = "
                        , orderId)).setTotalPrice(orderTotalPrice);
        return orderDto;
    }

    //добавление сверху заказа нового блюда(добавить блюдо в заказ со статусом "новый")
    @Transactional
    public UpdateOrderDetailsDTO updateOrderDetails(UpdateOrderDetailsDTO orderDetailsDTO) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrders(ordersRepository.findById(orderDetailsDTO.getOrderId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("заказ с таким id не существует! id = ",
                                orderDetailsDTO.getOrderId())));
        orderDetails.setMenu(menuRepository.findById(orderDetailsDTO.getOrderDetailsDto().getStockId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("меню с таким id не существует! id = ",
                                orderDetailsDTO.getOrderId())));
        orderDetails.setQuantity(orderDetailsDTO.getOrderDetailsDto().getQuantity());
        long detailId = orderDetailsRepository.save(orderDetails).getId();
        double totalPrice = (addAdditionals(orderDetailsDTO.getOrderDetailsDto().getGeneralAdditionalId(), detailId) + menuRepository.getById(orderDetailsDTO.getOrderDetailsDto().getStockId()).getPrice()) * orderDetailsDTO.getOrderDetailsDto().getQuantity(); //---->> сохраняет additionals и считает сумму всех additionals

        OrderDetails orderDetailsUpdate = orderDetailsRepository.findById(detailId).orElseThrow(
                () -> new ResourceNotFoundException("order detail с таким id не существует! id = ",
                        detailId));
        orderDetailsUpdate.setCalcTotal(totalPrice);
        orderDetailsRepository.save(orderDetailsUpdate);
        return orderDetailsDTO;
    }

    private double addOrderDetails(OrderDetailsDto detailsDto, Long orderId) throws Exception {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setMenu(menuRepository.findById(detailsDto.getStockId()).orElseThrow(
                () -> new Exception("меню detail с таким id не существует! id = "+
                        detailsDto.getStockId())));
        long count = menuRepository.findById(detailsDto.getStockId()).orElseThrow(
                () -> new Exception("menu с таким id не существует! id = "+
                        detailsDto.getStockId())).getCounter() + detailsDto.getQuantity();

        menuRepository.findById(detailsDto.getStockId()).orElseThrow(
                () -> new Exception("menu  с таким id не существует! id = "+
                        detailsDto.getStockId())).setCounter(count);

        orderDetails.setOrders(ordersRepository.findById(orderId).orElseThrow(
                () -> new Exception("order  с таким id не существует! id = "+ orderId)));

        orderDetails.setQuantity(detailsDto.getQuantity());
        long detailId = orderDetailsRepository.save(orderDetails).getId();

        double totalPrice = (addAdditionals(detailsDto.getGeneralAdditionalId(), detailId) +
                menuRepository.findById(detailsDto.getStockId()).orElseThrow(
                        () -> new ResourceNotFoundException("menu  с таким id не существует! id = ",
                                detailsDto.getStockId())
                ).getPrice()) * detailsDto.getQuantity(); //---->> сохраняет additionals и считает сумму всех additionals

        OrderDetails orderDetailsUpdate = orderDetailsRepository.findById(detailId).orElseThrow(
                () -> new ResourceNotFoundException("order detail  с таким id не существует! id = ",
                        detailId));
        orderDetailsUpdate.setCalcTotal(totalPrice);
        orderDetailsRepository.save(orderDetailsUpdate);
        return totalPrice;
    }

    private double addAdditionals(List<Long> GeneralAdditionalId, Long detailId) {

        OrderDetails currentOrderDetails = orderDetailsRepository.getById(detailId);

        List<GeneralAdditional> generalAdditionalList = generalAdditionalRepository
                .findAllById(GeneralAdditionalId);

        List<Additionals> additionalsList = new ArrayList<>();

        double totalPriceAdditionals = 0;
        for (GeneralAdditional general : generalAdditionalList) {
            Additionals additionals = new Additionals();
            additionals.setGeneralAdditional(general);
            additionals.setOrderDetail(currentOrderDetails);
            additionalsList.add(additionals);
            totalPriceAdditionals += general.getPrice();
        }
        additionalsRepository.saveAll(additionalsList);
        return totalPriceAdditionals;
    }

    private void subtract(Long orderId) throws Exception {
        List<OrderDetails> detailsList = orderDetailsRepository.findAllByOrders(ordersRepository.getById(orderId));

        for (OrderDetails d : detailsList) {
            List<Composition> compositions = compositionRepository.findAllByMenu(d.getMenu());

            for (Composition c : compositions) {
                minus(c.getProductId().getProductId(), c.getQuantity() * d.getQuantity());
            }

            List<Additionals> additionalsList = additionalsRepository.findAllByOrderDetail(d);

            for (Additionals a : additionalsList) {
                minus(a.getGeneralAdditional().getWarehouse().getProductId(),
                        a.getGeneralAdditional().getQuantity() * d.getQuantity());
            }
        }
    }

    private void minus(Long productId, double quantity) throws Exception {
        Warehouse product = warehouseRepository.getById(productId);

        if ((product.getQuantity() - quantity) < 0) {
            throw new Exception("На складе недостаточно " + product.getProductName() + "!");
        } else {
            product.setQuantity(product.getQuantity() - quantity);
            warehouseRepository.save(product);
        }
    }

    //it closes the order after client pays and adds new bonuses for specific client who made a customization
    public Orders closeOrder(Long orderId) throws Exception {
        //TODO: add exceptions in case order doesn't exist
        Optional<Orders> orders = ordersRepository.findById(orderId);
        if (orders.isEmpty()) throw new OrderNotFoundException();

        User client = orders.orElse(null).getClient();
        User employeeW = orders.get().getEmployee();

        //checking if the order status is valid to close it
        if (orders.get().getOrderStatus() != OrderStatus.READY) {
            throw new BadRequestException("You can close only READY orders. This order is "
                    + orders.get().getOrderStatus());
        }

        return ordersRepository.findById(orderId)
                .map(changeStatus -> {
                    if (orders.get().getOrderStatus() != OrderStatus.CLOSED) {
                        changeStatus.setOrderStatus(OrderStatus.CLOSED);
                        //if a client has no app and waiter made an order for client, bonuses will not be counted
                        // here bonuses are counted and set up for client
                        if (client != null) {
                            long updatedBonuses = client.getBonus() + (long) (orders.get().getTotalPrice() / 100);
                            client.setBonus(updatedBonuses);
                            userRepository.save(client);
                        }
                    }
                    //count how much money has waiter brought to cashier in total and set it up to rating column
                    if (employeeW != null && employeeW.getRole() == ERole.ROLE_WAITER) {
                        employeeW.setRating((employeeW.getRating() == null ? 0 : employeeW.getRating())
                                + (long) orders.get().getTotalPrice());
                        userRepository.save(employeeW);
                    }
                    // after an order with type IN is closed the reserved table will have a status free
                    try {
                        if (changeStatus.getOrderType() == OrderTypes.IN
                                && !tableService.isFree(changeStatus.getTable().getId())) {
                            try {
                                tableService.freeUpTable(changeStatus.getTable().getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return ordersRepository.save(changeStatus);
                }).orElseThrow(Exception::new);
    }

    //!!! look at this getCurrUser is used
    public List<OrdersDtoForWaiter> allOrdersByUser() {
        User user = userDetailsService.getCurrentUser();
        List<Orders> list = ordersRepository.findAllByEmployee_Id(user.getId());
        return getOrdersDtoForWaiters(list);
    }

    public Long getBonuses() {
        User user = userDetailsService.getCurrentUser();
        return user.getBonus();
    }

    public Long subtractBonuses(Long amount) {
        User user = userDetailsService.getCurrentUser();
        long leftBonuses = user.getBonus() - amount;
        user.setBonus(leftBonuses);
        userRepository.save(user);
        return leftBonuses;
    }

    public List<OrdersDtoForWaiter> allOrdersByUserAndStatus(int statusId) {
        User user = userDetailsService.getCurrentUser();
        List<Orders> ordersList = ordersRepository.
                findAllByEmployeeAndOrderStatus(user, OrderStatus.getStatus(statusId));
        return getOrdersDtoForWaiters(ordersList);
    }

    private List<OrdersDtoForWaiter> getOrdersDtoForWaiters(List<Orders> ordersList) {

        List<OrdersDtoForWaiter> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrdersDtoForWaiter model = new OrdersDtoForWaiter();
            model.setId(orders.getId());
            model.setOrderTime(orders.getOrderTime());
            model.setOrderStatus(orders.getOrderStatus());
            model.setOrderTypes(orders.getOrderType());
            if (orders.getOrderType() == OrderTypes.IN) {
                model.setTableId(orders.getTable().getId());
            } else
                model.setTableId(null);
            result.add(model);
        }
        return result;
    }

    @Transactional
    public List<OrdersDtoForBarista> getOrdersByTypeAndStatus(OrderStatus orderStatus, OrderTypes orderTypes) {
        User user = userDetailsService.getCurrentUser();
        List<Orders> list = ordersRepository.
                findAllByBranchAndOrderStatusAndOrderType(user.getBranches(), orderStatus, orderTypes);
        List<OrdersDtoForBarista> result = new ArrayList<>();
        for (Orders orders : list) {
            OrdersDtoForBarista model = new OrdersDtoForBarista();
            if (orders.getClient() != null)
                model.setClientName(orders.getClient().getFirstName());
            model.setId(orders.getId());

            List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrders(
                    ordersRepository.getById(orders.getId()));

            List<DetailsDTOForOrders> detailsDTOForOrders = new ArrayList<>();

            for (OrderDetails orderDetail : orderDetails) {
                DetailsDTOForOrders detailsDTOForOrders1 = new DetailsDTOForOrders();
                detailsDTOForOrders1.setName("x" + orderDetail.getQuantity() + " " + orderDetail.getMenu().getName());
                detailsDTOForOrders1.setPrice(orderDetail.getCalcTotal());
                detailsDTOForOrders1.setUrlImage(orderDetail.getMenu().getImagesUrl());
                detailsDTOForOrders.add(detailsDTOForOrders1);
            }
            model.setListOrderDetailsDto(detailsDTOForOrders);

            result.add(model);
        }
        return result;
    }

    private OrdersDtoForBarista convertToOrdersDtoForBarista(Orders orders) {
        OrdersDtoForBarista model = new OrdersDtoForBarista();
        model.setId(orders.getId());

        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrders(
                ordersRepository.getById(orders.getId()));

        List<DetailsDTOForOrders> detailsDTOForOrders = new ArrayList<>();

        for (OrderDetails orderDetail : orderDetails) {
            DetailsDTOForOrders detailsDTOForOrders1 = new DetailsDTOForOrders();
            detailsDTOForOrders1.setName("x" + orderDetail.getQuantity() + " " + orderDetail.getMenu().getName());
            detailsDTOForOrders1.setPrice(orderDetail.getCalcTotal());
            detailsDTOForOrders1.setUrlImage(orderDetail.getMenu().getImagesUrl());
            detailsDTOForOrders.add(detailsDTOForOrders1);
        }
        model.setListOrderDetailsDto(detailsDTOForOrders);
        return model;
    }


    @Transactional //добавление заказа для официанта
    public OrdersDtoForBarista addOrderForBarista(OrderDtoBarista orderDto) throws Exception {

        Orders orders = new Orders();

        orders.setEmployee(userDetailsService.getCurrentUser());
        orders.setOrderStatus(OrderStatus.PENDING);
        LocalDateTime localDateTime = LocalDateTime.now();
        orders.setOrderTime(convertLocalDateTimeToDateUsingInstant(localDateTime));
        orders.setOrderType(orderDto.getOrderType());
        orders.setBranch(userDetailsService.getCurrentUser().getBranches());

        long orderId = ordersRepository.save(orders).getId();

        double orderTotalPrice = 0;
        for (OrderDetailsDto orderDetails : orderDto.getListOrderDetailsDto()) {
            orderTotalPrice += addOrderDetails(orderDetails, orderId);
        }

        Orders orders1 = ordersRepository.getById(orderId);
        orders1.setTotalPrice(orderTotalPrice);
        ordersRepository.save(orders1);

        String bodyMessage = "";
        if (orders.getOrderType() == OrderTypes.IN)
            bodyMessage = "(В заведении)";
        else
            bodyMessage = "(На вынос)";
        /*PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
        pushNotificationRequest.setToken(userDetailsService.getCurrentUser().getFirebaseToken());
        pushNotificationRequest.setTitle("Новый заказ!");*/


        for (OrderDetails o : orderDetailsRepository.findAllByOrders(orders)) {
            bodyMessage += " "+ o.getMenu().getName() + " x" + o.getQuantity() + ", ";
        }
        /*pushNotificationRequest.setMessage(bodyMessage);*/

        Notifications notifications = new Notifications();
        notifications.setStatus(Status.ACTIVATE);
        notifications.setTitle("Новый заказ!");
        notifications.setMessage(bodyMessage);
        notifications.setTime(new Date());
        notifications.setUser(userDetailsService.getCurrentUser());
        notificationsRepository.save(notifications);
        return convertToOrdersDtoForBarista(orders1);
    }


    public List<OrderCurrAndCompletedForClientDto> getCurrentOrderClean() {
        User user = userDetailsService.getCurrentUser();
        List<Orders> orders = ordersRepository.findAllByClientIdAndOrderStatusOrOrderStatusOrOrderStatus
                (user.getId(), OrderStatus.PENDING, OrderStatus.IP, OrderStatus.READY);
        List<OrderCurrAndCompletedForClientDto> result = new ArrayList<>();
        for (Orders order : orders) {
            OrderCurrAndCompletedForClientDto dto = new OrderCurrAndCompletedForClientDto();
            dto.setOrderId(order.getId());
            dto.setOrderTime(order.getOrderTime());
            dto.setOrderType(order.getOrderType());
            dto.setStatus(order.getOrderStatus());
            if (order.getTable() != null)
                dto.setTableId(order.getTable().getId());
            dto.setListOrderDetailsDto(convertOrderDetailsForClient(order));
            dto.setTotalPrice(order.getTotalPrice());
            dto.setBranchId(order.getBranch().getId());
            dto.setBranchName(order.getBranch().getName());
            dto.setBranchAddress(order.getBranch().getAddress());
            dto.setBranchPhoneNumber(order.getBranch().getPhoneNumber());
            dto.setBranchLink2gis(order.getBranch().getLink2gis());
            dto.setBranchWorkingTime(order.getBranch().getWorkingTime().toString());
            result.add(dto);
        }
        return result;
    }

    public List<OrderCurrAndCompletedForClientDto> getCompletedOrdersClean() {
        User user = userDetailsService.getCurrentUser();
        List<Orders> orders = ordersRepository.findAllByClientIdAndOrderStatusOrderByOrderTimeDesc
                (user.getId(), OrderStatus.CLOSED);
        List<OrderCurrAndCompletedForClientDto> result = new ArrayList<>();
        for (Orders order : orders) {
            OrderCurrAndCompletedForClientDto dto = new OrderCurrAndCompletedForClientDto();
            dto.setOrderId(order.getId());
            dto.setOrderTime(order.getOrderTime());
            dto.setOrderType(order.getOrderType());
            dto.setStatus(order.getOrderStatus());
            dto.setTableId(order.getTable().getId());
            dto.setListOrderDetailsDto(convertOrderDetailsForClient(order));
            dto.setTotalPrice(order.getTotalPrice());
            dto.setBranchId(order.getBranch().getId());
            dto.setBranchName(order.getBranch().getName());
            dto.setBranchAddress(order.getBranch().getAddress());
            dto.setBranchPhoneNumber(order.getBranch().getPhoneNumber());
            dto.setBranchLink2gis(order.getBranch().getLink2gis());
            dto.setBranchWorkingTime(order.getBranch().getWorkingTime().toString());
            result.add(dto);
        }
        return result;
    }

    public List<OrderDetailsFullDtoForClient> convertOrderDetailsForClient(Orders order) {
        List<OrderDetailsFullDtoForClient> result = new ArrayList<>();
        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrders(order);
        for (OrderDetails curr : orderDetails) {
            OrderDetailsFullDtoForClient dto = new OrderDetailsFullDtoForClient();
            dto.setId(curr.getId());
            dto.setName(curr.getMenu().getName());
            dto.setPrice(curr.getMenu().getPrice());

            dto.setQuantity(curr.getQuantity());
            dto.setChosenGeneralAdditional(convertAdditionalForClientToDto(additionalsRepository.findAllByOrderDetailId(curr.getId())));
            dto.setCalcTotal(curr.getCalcTotal());
            result.add(dto);
        }
        return result;
    }

    public List<AdditionalForClientHistoryDto> convertAdditionalForClientToDto(List<Additionals> additionalsList) {
        List<AdditionalForClientHistoryDto> result = new ArrayList<>();
        for (Additionals additional : additionalsList) {
            AdditionalForClientHistoryDto dto = new AdditionalForClientHistoryDto();
            dto.setGeneralAdditionalId(additional.getAdditionalId());
            Optional<Warehouse> warehouseItem = warehouseRepository.findByProductId(dto.getGeneralAdditionalId());
            warehouseItem.ifPresent(warehouse -> dto.setGeneralAdditionalName(warehouse.getProductName()));
            result.add(dto);
        }
        return result;
    }


    public ChangeStatusDto setStatusInProgress(Long orderId) throws Exception {
        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Нету такого заказа!")
        );
        if (orders.getOrderStatus() == OrderStatus.PENDING) {
            orders.setOrderStatus(OrderStatus.IP);
            subtract(orderId);
            ordersRepository.save(orders);

            //отправка уведомлений
            if (orders.getOrderType()==OrderTypes.IN) {
                /*PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
                pushNotificationRequest.setToken(orders.getEmployee().getFirebaseToken());
                pushNotificationRequest.setTitle("Заказ готов! стол №" + orders.getTable().getId());
                pushNotificationRequest.setMessage("Заказ стола №"+ orders.getTable().getId()+" готов!");*/
                Notifications notifications = new Notifications();
                notifications.setStatus(Status.ACTIVATE);
                notifications.setTitle("Заказ готов! стол №" + orders.getTable().getId());
                notifications.setMessage("Заказ стола №"+ orders.getTable().getId()+" готов!");
                notifications.setTime(new Date());
                notifications.setUser(orders.getEmployee());
                notificationsRepository.save(notifications);
            }

            return new ChangeStatusDto(orderId, OrderStatus.IP);
        } else
            throw new BadRequestException("You can start preparing only PENDING orders. This order is "
                    + orders.getOrderStatus());
    }

    public ChangeStatusDto setStatusReady(Long orderId) throws Exception {
        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Нету такого заказа!")
        );
        if (orders.getOrderStatus() == OrderStatus.IP) {
            orders.setOrderStatus(OrderStatus.READY);

            /*if (orders.getEmployee().getRole() == ERole.ROLE_WAITER) {
                PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
                pushNotificationRequest.setToken(orders.getEmployee().getFirebaseToken());
                pushNotificationRequest.setTitle("Заказ стола №" + orders.getTable().getId() + " готов");

                String bodyMessage = "";
                for (OrderDetails o : orderDetailsRepository.findAllByOrders(orders)) {
                    bodyMessage += o.getMenu().getName() + " x" + o.getQuantity() + ", ";
                }
                pushNotificationRequest.setMessage(bodyMessage);

                fcmService.sendMessageToToken(pushNotificationRequest);
            }*/
            subtract(orderId);
            ordersRepository.save(orders);
            return new ChangeStatusDto(orderId, OrderStatus.READY);
        } else
            throw new BadRequestException("You can set order as READY only if it is IP. This order is "
                    + orders.getOrderStatus());
    }

    public ChangeStatusDto setStatusCancel(Long orderId) throws Exception {
        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Нету такого заказа!")
        );
        if (orders.getOrderStatus() == OrderStatus.PENDING) {
            orders.setOrderStatus(OrderStatus.CANCELED);
            subtract(orderId);
            ordersRepository.save(orders);

            //отправка уведомлений
/*            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
            pushNotificationRequest.setToken(userRepository.getByBranchesAndRole(orders.getTable().getBranches(),
                    ERole.ROLE_BARISTA).getFirebaseToken());
            pushNotificationRequest.setTitle("Заказ отменен! стол №" + orders.getTable().getId());
            pushNotificationRequest.setMessage("Клиент отменил заказ.");*/

            return new ChangeStatusDto(orderId, OrderStatus.CANCELED);
        } else
            throw new BadRequestException("You are late to cancel. This order is "
                    + orders.getOrderStatus());
    }

}



