package com.jwsystem.schedule.configuration

import javax.persistence.PrePersist
import javax.persistence.PreRemove
import javax.persistence.PreUpdate

class TenantListener {

    @PreUpdate
    @PreRemove
    @PrePersist
    fun setTenant(entity: TenantAware) {
        val tenantId = TenantContext.getTenantId()
        entity.setTenantId(tenantId)
    }

}