package org.yabo.distributed.transaction.local;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yabo.sharding.jdbc.EnableShardingRepository;

@SpringBootApplication
@EnableShardingRepository
public class LocalTransactionApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalTransactionApplication.class);
    }
}
