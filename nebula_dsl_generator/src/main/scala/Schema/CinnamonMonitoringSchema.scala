package Schema

case class CinnamonMonitoringSchema(
    consoleReporter: Boolean,
    prometheusHttpServer: Boolean,
    messageType: Boolean,
    actors: Seq[CinnamonActorMonitoringSchema]
)
