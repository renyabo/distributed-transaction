package org.yabo.distributed.transaction.xa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yabo.sharding.jdbc.EnableShardingRepository;

@SpringBootApplication
@EnableShardingRepository
public class XaTransactionApplication {
    public static void main(String[] args) {
        SpringApplication.run(XaTransactionApplication.class);
    }
}
