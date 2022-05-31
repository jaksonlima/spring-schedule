package com.jwsystem.schedule.configuration

import org.springframework.jdbc.datasource.ConnectionProxy
import org.springframework.jdbc.datasource.DelegatingDataSource
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection
import javax.sql.DataSource

class TenantAwareDataSource(targetDataSource: DataSource) : DelegatingDataSource(targetDataSource) {

    override fun getConnection(): Connection {
        val connection = targetDataSource!!.connection
        setTenantId(connection)
        return getTenantAwareConnectionProxy(connection);
    }

    override fun getConnection(username: String, password: String): Connection {
        val connection = targetDataSource!!.getConnection(username, password)
        setTenantId(connection)
        return getTenantAwareConnectionProxy(connection)
    }

    private fun setTenantId(connection: Connection) {
        val sql = connection.createStatement()
        val tenantId = TenantContext.getTenantId()
        sql.execute("SET app.tenant_id TO $tenantId")
    }

    private fun getTenantAwareConnectionProxy(connection: Connection): Connection {
        return Proxy.newProxyInstance(
            ConnectionProxy::class.java.classLoader,
            arrayOf(ConnectionProxy::class.java),
            TenantAwareInvocationHandler(connection)
        ) as Connection
    }

    class TenantAwareInvocationHandler(private val connection: Connection) : InvocationHandler {

        private fun methodInvoke(method: Method?, args: Array<out Any>?): Any? {
            return if (args == null) method?.invoke(connection) else method?.invoke(connection, args)
        }

        private fun isArgsProxy(proxy: Any?, args: Array<out Any>?): Boolean {
            return ((args?.get(0) as Class<*>).isInstance(proxy));
        }

        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
            val result = when (method?.name) {
                "equals" -> proxy == args?.get(0)
                "hashCode" -> System.identityHashCode(proxy)
                "getTargetConnection" -> connection
                "toString" -> "Tenant-aware proxy for target Connection $connection"
                "close" -> connection.createStatement().execute("RESET app.tenant_id")
                "unwrap" -> if (isArgsProxy(proxy, args)) true else methodInvoke(method, args)
                "isWrapperFor" -> if (isArgsProxy(proxy, args)) true else methodInvoke(method, args)
                else -> methodInvoke(method, args)
            }

            return result
        }
    }

}