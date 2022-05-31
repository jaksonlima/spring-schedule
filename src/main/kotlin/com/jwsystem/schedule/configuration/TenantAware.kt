package com.jwsystem.schedule.configuration

interface TenantAware {

    fun getTenantId(): String?

    fun setTenantId(tenantId: String?)

}