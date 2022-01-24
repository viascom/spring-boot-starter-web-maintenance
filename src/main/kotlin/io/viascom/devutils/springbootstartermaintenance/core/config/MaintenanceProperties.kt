package io.viascom.devutils.springbootstartermaintenance.core.config

import java.time.Duration

data class MaintenanceProperties(
    val enabled: Boolean,
    val roles: MutableList<String>,
    val alert: Boolean,
    val clean: Boolean,
    val retryAfter: Int,
    val events: Boolean,
    val metrics: Boolean,
)