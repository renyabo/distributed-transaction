package org.yabo.distributed.transaction.local;

import com.google.common.collect.Lists;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yabo.distributed.transaction.common.Order;
import org.yabo.distributed.transaction.common.OrderItem;
import org.yabo.distributed.transaction.common.Response;
import org.yabo.sharding.jdbc.repository.OrderItemMapper;
import org.yabo.sharding.jdbc.repository.OrderMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class LocalController implements ApplicationContextAware {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;


    @RequestMapping("/create")
    @ShardingTransactionType(TransactionType.LOCAL)
    public String createTable() {
        orderMapper.createTableIfNotExists();
        orderItemMapper.createTableIfNotExists();
        return "OK";
    }

    @RequestMapping("/trunc")
    @ShardingTransactionType(TransactionType.LOCAL)
    public String truncTable() {
        orderMapper.truncateTable();
        orderItemMapper.truncateTable();
        return "OK";
    }

    @RequestMapping("/insertOK")
    @Transactional
    @ShardingTransactionType(TransactionType.LOCAL)
    public String insertOK() {
        doInsert();
        return "OK";
    }

    @RequestMapping("/insertFail")
    @Transactional
    @ShardingTransactionType(TransactionType.LOCAL)
    public String insertFail() {
        doInsert();
        throw new IllegalArgumentException("yabo exception");
    }

    private void doInsert() {
        List<String> orderIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            String orderId = String.valueOf(order.hashCode());
            order.setId(orderId);
            orderIds.add(orderId);
            order.setUserId("yabo");
            order.setStatus("normal");
            orderMapper.insert(order);
        }
        orderIds.forEach(id -> {
            OrderItem item = new OrderItem();
            item.setId(String.valueOf(item.hashCode()));
            item.setOrderId(id);
            item.setStatus("yes");
            item.setUserId("userId");
            orderItemMapper.insert(item);
        });
    }

    @RequestMapping("/list")
    @Transactional
    @ShardingTransactionType(TransactionType.LOCAL)
    public Response list() {
        List list = new ArrayList();
        list.add(orderMapper.selectAll());
        list.add(orderItemMapper.selectAll());
        return new Response(list);
    }


    @RequestMapping("/update")
    @Transactional
    @ShardingTransactionType(TransactionType.LOCAL)
    public String update() {
        int i = orderMapper.updateByIds(Lists.newArrayList("122", "345", "567", "789"));
        int item = orderItemMapper.updateByIds(Lists.newArrayList("122", "345", "567", "789"));
        return "OK=" + i + "," + item;
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
