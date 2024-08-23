package Schema

case class CaseSchema(
    className: String,
    executionCode: String,
    endpointSchema: Option[ExternalEndpointSchema],
    transitions: Seq[String]
)
