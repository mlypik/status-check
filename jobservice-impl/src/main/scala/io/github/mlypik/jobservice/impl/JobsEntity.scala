package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

class JobsEntity extends PersistentEntity {
  override type Command = this.type
  override type Event = this.type
  override type State = this.type

  override def initialState: JobsEntity.this.type = ???

  override def behavior: Behavior = ???
}

object JobsSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = ???
}
