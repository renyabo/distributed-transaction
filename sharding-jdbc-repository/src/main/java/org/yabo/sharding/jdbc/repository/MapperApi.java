package org.yabo.sharding.jdbc.repository;

import org.apache.ibatis.annotations.Param;
import org.yabo.distributed.transaction.common.Order;

import java.util.List;

public interface MapperApi<T> {

    /**
     * Create table if not exist.
     */
    void createTableIfNotExists();

    /**
     * Drop table.
     */
    void dropTable();

    /**
     * Truncate table.
     */
    void truncateTable();

    /**
     * insert one entity.
     *
     * @param entity entity
     * @return count or primary key
     */
    int insert(T entity);

    /**
     * Do delete.
     *
     * @param key key
     */
    int delete(@Param("id") String id);

    /**
     * select all.
     *
     * @return list of entity
     */
    List<T> selectAll();

    List<T> selectRange();

    int updateByIds(@Param("ids") List<String> ids);

    int insertBatch(List<Order> orders);
}
