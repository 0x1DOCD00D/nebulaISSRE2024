package Schema

case class ActorSchema(
    actorName: String,
    actorArgs: Seq[ArgumentSchema],
    actorVars: Seq[VariableSchema],
    methods: Seq[MethodSchema]
)
