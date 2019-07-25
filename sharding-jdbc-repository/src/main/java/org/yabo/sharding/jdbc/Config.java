package org.yabo.sharding.jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@PropertySource("classpath:respository.properties")
@MapperScan("org.yabo.sharding.jdbc.repository")
public class Config {
    @Bean("dataSource")
    public DataSource getShardingDataSource() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(orderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new PreciseShardingAlgorithm<String>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
                System.err.println("default -->  availableTargetNames:" + availableTargetNames + "," + shardingValue.getColumnName() + "," + shardingValue.getLogicTableName() + "," + shardingValue.getValue());
                String targetNode = String.valueOf((int) shardingValue.getValue().charAt(0) % availableTargetNames.size());
                for (String name : availableTargetNames) {
                    if (name.endsWith(targetNode)) {
                        return name;
                    }
                }
                return null;
            }
        }));
        try {
            return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @DependsOn({"dataSource", "mybatisConfig"})
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource,
                                                       org.apache.ibatis.session.Configuration configuration) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();//mybatis-plus插件类
        sqlSessionFactoryBean.setDataSource(dataSource);//数据源
        sqlSessionFactoryBean.setConfiguration(configuration);
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources("classpath*:/mapper/*.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlSessionFactoryBean;
    }

    @Bean("mybatisConfig")
    public org.apache.ibatis.session.Configuration configuration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setUseGeneratedKeys(false);
        configuration.setCacheEnabled(false);
        configuration.setLogImpl(Slf4jImpl.class);
        return configuration;
    }

    private TableRuleConfiguration orderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order", "ds${0..1}.t_order${0..1}");
        result.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new PreciseShardingAlgorithm<String>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
                System.err.println("order -->  availableTargetNames:" + availableTargetNames + "," + shardingValue.getColumnName() + "," + shardingValue.getLogicTableName() + "," + shardingValue.getValue());
                String targetNode = String.valueOf(shardingValue.getValue().codePointAt(0) % availableTargetNames.size());
                for (String name : availableTargetNames) {
                    if (name.endsWith(targetNode)) {
                        System.err.println("db select:" + name);
                        return name;
                    }
                }
                return null;
            }
        }));
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new PreciseShardingAlgorithm<String>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
                System.err.println("表分区：availableTargetNames:" + availableTargetNames + "," + shardingValue.getColumnName() + "," + shardingValue.getLogicTableName() + "," + shardingValue.getValue());
                String targetTable = String.valueOf(shardingValue.getValue().codePointAt(shardingValue.getValue().length() - 1) % availableTargetNames.size());
                for (String name : availableTargetNames) {
                    if (name.endsWith(targetTable)) {
                        System.err.println("table select :" + name);
                        return name;
                    }
                }
                return "order";
            }
        }));
        return result;
    }

    private TableRuleConfiguration orderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item", "ds${0..1}.t_order_item${0..1}");
        result.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id", new PreciseShardingAlgorithm<String>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
                System.err.println("orderItem --> availableTargetNames:" + availableTargetNames + "," + shardingValue.getColumnName() + "," + shardingValue.getLogicTableName() + "," + shardingValue.getValue());
                String targetNode = String.valueOf((int) shardingValue.getValue().charAt(0) % availableTargetNames.size());
                for (String name : availableTargetNames) {
                    if (name.endsWith(targetNode)) {
                        return name;
                    }
                }
                return null;
            }
        }));
        return result;
    }

    private Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        BasicDataSource ds0 = new BasicDataSource();
        ds0.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds0.setUrl("jdbc:mysql://192.168.115.129:3306/ds0?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT");
        ds0.setUsername("yabo");
        ds0.setPassword("123456");
        result.put("ds0", ds0);

        BasicDataSource ds1 = new BasicDataSource();
        ds1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds1.setUrl("jdbc:mysql://192.168.115.129:3306/ds1?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT");
        ds1.setUsername("yabo");
        ds1.setPassword("123456");
        result.put("ds1", ds1);
        return result;
    }
}
