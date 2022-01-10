/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.dbCore;


import com.zaxxer.hikari.HikariDataSource;
import org.palading.clivia.config.api.CliviaApiConfigService;
import org.palading.clivia.config.api.CliviaBlacklistConfigService;
import org.palading.clivia.config.api.CliviaClientSecurityConfigService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * @author palading_cr
 * @title CliviaDbConfig
 * @project clivia
 */
@Conditional(CliviaConfigCondition.class)
@EnableJpaRepositories(basePackages = {"org.palading.clivia.dbCore.repository"})
@EntityScan("org.palading.clivia.dbCore.domain")
@Configuration
public class CliviaDbAutoConfigiguration {

    @Conditional(CliviaConfigCondition.class)
    @ConfigurationProperties(prefix = "clivia.datasource")
    @Bean
    public CliviaDataSourceProperties cliviaDataSourceProperties() {
        return new CliviaDataSourceProperties();
    }

    /**
     * org.palading.clivia.config.api config service
     *
     * @author palading_cr
     *
     */
    @Conditional(CliviaConfigCondition.class)
    @Bean
    public CliviaApiConfigService cliviaApiConfigService() {
        return new CliviaApiConfigServiceImpl();
    }

    /**
     * blacklist config service
     *
     * @author palading_cr
     *
     */
    @Conditional(CliviaConfigCondition.class)
    @Bean
    public CliviaBlacklistConfigService cliviaBlackListConfigService() {
        return new CliviaBlackListConfigServiceImpl();
    }

    /**
     * client switcher service
     *
     * @author palading_cr
     *
     */
    @Conditional(CliviaConfigCondition.class)
    @Bean
    public CliviaClientSecurityConfigService cliviaClientSwitcherConfigService() {
        return new CliviaClientSecurityConfigServiceImpl();
    }

    /**
     * @author palading_cr
     *
     */
    @Conditional(CliviaConfigCondition.class)
    @Bean
    public CliviaDbCommandLineRunner cliviaDbCommandLineRunner(ObjectProvider<CliviaApiConfigService> cliviaApiConfigServices,
                                                                                         ObjectProvider<CliviaBlacklistConfigService> cliviaBlacklistConfigServices) {
        return new CliviaDbCommandLineRunner(cliviaApiConfigServices.getIfAvailable(),
            cliviaBlacklistConfigServices.getIfAvailable());
    }

    @Conditional(CliviaConfigCondition.class)
    @Bean
    public DataSource dataSource(ObjectProvider<CliviaDataSourceProperties> cliviaDataSourceProperties) {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setDriverClassName(cliviaDataSourceProperties.getIfAvailable().getDriverClassName());
        datasource.setJdbcUrl(cliviaDataSourceProperties.getIfAvailable().getJdbcUrl());
        datasource.setUsername(cliviaDataSourceProperties.getIfAvailable().getUsername());
        datasource.setPassword(cliviaDataSourceProperties.getIfAvailable().getPassword());
        datasource.setMaximumPoolSize(cliviaDataSourceProperties.getIfAvailable().getMaximumPoolSize());
        datasource.setConnectionTimeout(cliviaDataSourceProperties.getIfAvailable().getConnectionTimeout());
        datasource.setPoolName(cliviaDataSourceProperties.getIfAvailable().getPoolName());
        return datasource;
    }

}
