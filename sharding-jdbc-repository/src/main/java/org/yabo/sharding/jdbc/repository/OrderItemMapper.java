package org.yabo.sharding.jdbc.repository;

import org.apache.ibatis.annotations.Mapper;
import org.yabo.distributed.transaction.common.OrderItem;

@Mapper
public interface OrderItemMapper extends MapperApi<OrderItem>{
}
