package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import io.github.mlypik.jobservice.api.{JobDefinition, JobId, JobService, JobSubmitted}

case class JobServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends JobService{
  override def submit(): ServiceCall[JobDefinition, JobId] = ???

  override def jobSubmittedTopic(): Topic[JobSubmitted] = ???
}
