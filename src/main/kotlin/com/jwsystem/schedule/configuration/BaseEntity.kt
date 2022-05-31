package com.jwsystem.schedule.configuration

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(TenantListener::class)
abstract class BaseEntity : TenantAware, Serializable {

    @Column(name = "TENANT_ID")
    open var tenantID: String? = null

    override fun getTenantId(): String? {
        return this.tenantID
    }

    override fun setTenantId(tenantId: String?) {
        this.tenantID = tenantId
    }

}