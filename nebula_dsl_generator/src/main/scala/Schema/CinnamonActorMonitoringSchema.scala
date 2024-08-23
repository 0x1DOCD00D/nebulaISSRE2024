package Schema

case class CinnamonActorMonitoringSchema(
    path: String,
    reporter: String,
    thresholds: ThresholdMonitoringSchema
)
