package org.yabo.distributed.transaction.local;

import com.google.common.collect.Lists;
import io.shardingsphere.transaction.annotation.ShardingTransactionType;
import io.shardingsphere.transaction.api.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yabo.distributed.transaction.common.Order;
import org.yabo.sharding.jdbc.repository.OrderMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@ResponseBody
public class LocalController {

    @Autowired
    OrderMapper orderMapper;

    @RequestMapping("/insert")
    @Transactional
    public String insert() {
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            order.setId(String.valueOf(i));
            order.setUserId("yabo");
            order.setStatus("normal");
            orderMapper.insert(order);
        }
        return "OK";
    }


    @RequestMapping("/insert1")
    @ShardingTransactionType(TransactionType.LOCAL)
    @Transactional
    public String insert1(@RequestParam("ids") String ids) {
        System.out.println(ids);
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")) {
                Order order = new Order();
                order.setId(id);
                order.setUserId("yabo");
                order.setStatus("normal");
                orderMapper.insert(order);
            }
        }
        return "OK";
    }

    @RequestMapping("/insertBatch")
    @ShardingTransactionType(TransactionType.LOCAL)
    @Transactional
    public String insertBatch(@RequestParam("ids") String ids) {
        System.out.println(ids);
        List<Order> orders = new ArrayList<>();
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")) {
                Order order = new Order();
                order.setId(id);
                order.setUserId("yabo");
                order.setStatus("normal");
                orders.add(order);
            }
        }
        orderMapper.insertBatch(orders);
        return "OK";
    }

    @RequestMapping("/truncateTable")
    public String truncateTable() {
        orderMapper.truncateTable();
        return "OK";
    }

    @RequestMapping("/trunc")
    public String createTable() {
        orderMapper.createTableIfNotExists();
        return "OK";
    }

    @RequestMapping("/update")
    public String update() {
        int i = orderMapper.updateByIds(Lists.newArrayList("2", "0efdf21b-05a1-4e96-a420-54cbbc15596e", "5b388ebe-aed9-41ff-826e-6dde3e706acd", "7"));
        return "OK=" + i;
    }
}
