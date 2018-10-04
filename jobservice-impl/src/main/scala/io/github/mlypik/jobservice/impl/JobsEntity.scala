package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import io.github.mlypik.jobservice.api.{JobDefinition, JobId, JobStatus}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable

class JobsEntity extends PersistentEntity {
  override type Command = JobCommand[_]
  override type Event = JobEvent
  override type State = JobServiceState

  override def initialState: State = JobServiceState(JobStatus("Does not exist"), JobDefinition(List()), "")

  override def behavior: Behavior = {
    case JobServiceState(jobProgress, _, jobResult) => Actions().onCommand[CreateJob, JobId] {
      case (CreateJob(jobid, jobDefinition), ctx, state) => {
        ctx.thenPersist(
          JobSubmitted(jobid, jobDefinition)
        ) { _ =>
          ctx.reply(jobid)
        }
      }
    }.onReadOnlyCommand[GetJobStatus, JobStatus] {
      case (GetJobStatus(), ctx, state) => {
        ctx.reply(jobProgress)
      }
    }.onEvent {
      case (JobSubmitted(jobid, jobDefinition), state) => {
        JobServiceState(JobStatus("Submitted"), jobDefinition, "")
      }
    }
  }
}


case class JobDetails(details: String)

object JobDetails {
  implicit val format: Format[JobDetails] = Json.format
}

sealed trait JobEvent extends AggregateEvent[JobEvent] {
  override def aggregateTag: AggregateEventTagger[JobEvent] = JobEvent.Tag
}

object JobEvent {
  val Tag: AggregateEventTag[JobEvent] = AggregateEventTag[JobEvent]
}

sealed trait JobCommand[R] extends ReplyType[R]

case class CreateJob(jobid: JobId, jobDefinition: JobDefinition) extends JobCommand[JobId]

case class GetJobStatus() extends JobCommand[JobStatus]


case class JobSubmitted(jobId: JobId, jobDetails: JobDefinition) extends JobEvent

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format
}

case class JobServiceState(state: JobStatus, jobDefinition: JobDefinition, result: String)

object JobServiceState {
  implicit val format: Format[JobServiceState] = Json.format
}

object JobsSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[JobServiceState],
    JsonSerializer[JobId],
    JsonSerializer[JobDetails],
    JsonSerializer[JobSubmitted]
  )
}


