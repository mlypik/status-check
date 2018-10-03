package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.softwaremill.id.pretty.PrettyIdGenerator
import io.github.mlypik.jobservice.api
import io.github.mlypik.jobservice.api.{JobDefinition, JobService}

case class JobServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends JobService {
  val idGenerator: PrettyIdGenerator = PrettyIdGenerator.singleNode

  override def submit(): ServiceCall[JobDefinition, api.JobId] = ServiceCall { jobDefinition =>
    val id = idGenerator.nextId()
    val ref = persistentEntityRegistry.refFor[JobsEntity](id)

    ref.ask(CreateJob(JobId(id), jobDefinition))
  }

  override def jobSubmittedTopic(): Topic[api.JobSubmitted] =
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry.eventStream(JobEvent.Tag, fromOffset)
        .map(ev => (convertEvent(ev), ev.offset)
        )
    }

  private def convertEvent(jobEvent: EventStreamElement[JobEvent]): api.JobSubmitted = {
    jobEvent.event match {
      case JobSubmitted(jobId, jobDetails) => api.JobSubmitted(api.JobId(jobId.id), jobDetails)
    }
  }
}
