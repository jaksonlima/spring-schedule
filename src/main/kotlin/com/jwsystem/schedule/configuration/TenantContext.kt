package com.jwsystem.schedule.configuration

class TenantContext {

    companion object {
        private val CURRENT_TENANT = InheritableThreadLocal<String>()

        fun getTenantId(): String {
            return CURRENT_TENANT.get() ?: "default"
        }

        fun setTenantId(tenantId: String) {
            CURRENT_TENANT.set(tenantId)
        }

        fun clear() {
            CURRENT_TENANT.remove()
        }
    }

}