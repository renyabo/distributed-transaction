package org.yabo.sharding.jdbc.repository;

import org.apache.ibatis.annotations.Mapper;
import org.yabo.distributed.transaction.common.Order;

@Mapper
public interface OrderMapper extends MapperApi<Order> {
}
