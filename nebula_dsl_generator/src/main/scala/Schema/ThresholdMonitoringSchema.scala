package Schema

case class ThresholdMonitoringSchema(
    mailboxSize: Int,
    stashSize: Int,
    mailboxTime: String,
    processingTime: String
)
