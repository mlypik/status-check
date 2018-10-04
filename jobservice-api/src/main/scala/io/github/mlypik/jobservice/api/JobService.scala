package io.github.mlypik.jobservice.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object JobService {
  val TOPIC_NAME = "jobs"
}

trait JobService extends Service {

  //submit job definition, get jod id for later lookup
  def submit(): ServiceCall[JobDefinition, JobId]

  def getJobStatus(id: JobId): ServiceCall[NotUsed, JobStatus]

  def jobsTopic(): Topic[JobSubmitted]

  override final def descriptor: Descriptor = {
    import Service._
    implicit val jobIdPathParamSerializer: PathParamSerializer[JobId] = PathParamSerializer.required[JobId]("JobId")(JobId(_))(_.id)
    named("job")
      .withCalls(
        pathCall("/nsc/submit", submit _),
        pathCall("/nsc/:id/status", getJobStatus _)
      )
      .withTopics(
        topic(JobService.TOPIC_NAME, jobsTopic _)
      )
      .withAutoAcl(true)
  }

}


case class JobId(id: String)

object JobId {
  implicit val format: Format[JobId] = Json.format[JobId]
}

case class JobStatus(status: String)

object JobStatus {
  implicit val format: Format[JobStatus] = Json.format[JobStatus]
}

case class JobDefinition(addresses: List[String])

object JobDefinition {
  implicit val format: Format[JobDefinition] = Json.format[JobDefinition]
}

case class JobSubmitted(id: JobId, definition: JobDefinition)

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format[JobSubmitted]
}
