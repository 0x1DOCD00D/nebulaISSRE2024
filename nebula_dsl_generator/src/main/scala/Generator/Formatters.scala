package Generator

import Schema.{
  ActorSchema,
  ArgumentSchema,
  CaseSchema,
  CustomObjectSchema,
  ExternalEndpointSchema,
  MessageSchema,
  MethodSchema,
  OrchestratorSchema,
  VariableSchema
}
import play.api.libs.json.Json
import play.api.libs.json.OFormat

object Formatters {
  implicit val orchestratorFormat: OFormat[OrchestratorSchema] =
    Json.format[OrchestratorSchema]

  implicit val actorFormat: OFormat[ActorSchema] =
    Json.format[ActorSchema]

  implicit val argumentFormat: OFormat[ArgumentSchema] =
    Json.format[ArgumentSchema]

  implicit val variableFormat: OFormat[VariableSchema] =
    Json.format[VariableSchema]

  implicit val methodFormat: OFormat[MethodSchema] =
    Json.format[MethodSchema]

  implicit val caseFormat: OFormat[CaseSchema] =
    Json.format[CaseSchema]

  implicit val customObjectFormat: OFormat[CustomObjectSchema] =
    Json.format[CustomObjectSchema]

  implicit val externalEndpointFormat: OFormat[ExternalEndpointSchema] =
    Json.format[ExternalEndpointSchema]

  implicit val messageFormat: OFormat[MessageSchema] =
    Json.format[MessageSchema]
}
