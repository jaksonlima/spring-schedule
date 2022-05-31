package com.jwsystem.schedule.configuration

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource


@Configuration
@EnableConfigurationProperties
class DataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("multitenancy.tenant.datasource")
    fun tenantDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties("multitenancy.tenant.datasource.hikari")
    fun tenantDataSource(): DataSource {
        val dataSource = tenantDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
        dataSource.poolName = "tenantDataSource"
        return TenantAwareDataSource(dataSource)
    }

}