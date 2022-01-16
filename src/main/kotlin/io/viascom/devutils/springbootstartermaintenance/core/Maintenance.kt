package io.viascom.devutils.springbootstartermaintenance.core

import io.viascom.devutils.springbootstartermaintenance.core.config.MaintenanceProperties
import io.viascom.devutils.springbootstartermaintenance.core.event.MaintenanceEventPublisher
import io.viascom.devutils.springbootstartermaintenance.core.event.MaintenanceState
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.LocalDateTime.now


open class Maintenance(
    private val properties: MaintenanceProperties,
    private val alerts: List<MaintenanceAlert>,
    private val cleaners: List<MaintenanceCleaner>,
    private val eventPublisher: MaintenanceEventPublisher,
    var start: LocalDateTime? = null,
    var end: LocalDateTime? = null,
    var active: Boolean = false,
    var roles: MutableList<String> = mutableListOf(),
    var events: Boolean = false,
    private var state: MaintenanceState = MaintenanceState.DISABLED
) {

    init {
        this.active = properties.enabled
        this.roles = properties.roles
        this.events = properties.events

        if (active) {
            this.state = MaintenanceState.ENABLED
            this.start = now()
        }
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @JvmOverloads
    fun start(
        startTime: LocalDateTime? = now(),
        expectedEndTime: LocalDateTime? = null,
        alert: Boolean? = false
    ) {
        this.active = true
        this.state = MaintenanceState.ENABLED

        log.info("Maintenance mode $state")

        if (startTime != null) {
            this.start = startTime
        } else {
            this.start = now()
        }

        if (expectedEndTime != null) {
            this.end = expectedEndTime
        }

        if (events) {
            eventPublisher.publishMaintenanceEvent(MaintenanceState.ENABLED)
        }

        if (alert == true || properties.alert) {
            alert()
        }
    }

    @JvmOverloads
    fun stop(clean: Boolean? = false) {
        this.active = false
        this.state = MaintenanceState.DISABLED

        log.info("Maintenance mode $state")

        this.end = now()

        if (events) {
            eventPublisher.publishMaintenanceEvent(MaintenanceState.DISABLED)
        }

        if (clean == true || properties.clean) {
            clean()
        }
    }

    fun state(): MaintenanceState {
        log.info("Maintenance mode $state")
        return state
    }

    fun clean() = cleaners.forEach { it.clean() }

    fun alert() = alerts.forEach { it.alert() }
}