package org.yabo.distributed.transaction.xa;

import com.google.common.collect.Lists;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yabo.distributed.transaction.common.Order;
import org.yabo.sharding.jdbc.repository.OrderMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class XaController implements ApplicationContextAware {

    @Autowired
    OrderMapper orderMapper;

    @RequestMapping("/insert")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
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
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
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

    @RequestMapping("/insertOK")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    public String insertOK() {
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            order.setId(String.valueOf(i));
            order.setUserId("yabo");
            order.setStatus("normal");
            orderMapper.insert(order);
        }
        return "OK";
    }

    @RequestMapping("/insertFail")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    public String insertFail() {
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            order.setId(String.valueOf(i));
            order.setUserId("yabo");
            order.setStatus("normal");
            orderMapper.insert(order);
            Assert.isTrue(i < 100, "throw exception...");
        }
        return "OK";
    }

    @RequestMapping("/delete")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    public String delete(@RequestParam("ids") String ids) {
        System.out.println(ids);
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")) {
                orderMapper.delete(id);
            }
        }
        return "OK";
    }

    @RequestMapping("/insertBatch")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
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
    @ShardingTransactionType(TransactionType.XA)
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
    @ShardingTransactionType(TransactionType.XA)
    public String update() {
        int i = orderMapper.updateByIds(Lists.newArrayList("2", "0efdf21b-05a1-4e96-a420-54cbbc15596e", "5b388ebe-aed9-41ff-826e-6dde3e706acd", "7"));
        return "OK=" + i;
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
