package io.github.mlypik.jobservice.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object JobService {
  val TOPIC_NAME = "pendingjobs"
}

trait JobService extends Service {

  //submit job definition, get jod id for later lookup
  def submit(): ServiceCall[JobDefinition, JobId]

  def jobSubmittedTopic(): Topic[JobSubmitted]

  override final def descriptor: Descriptor = {
    import Service._
    named("job")
      .withCalls(
        pathCall("/nsc/submit", submit _)
      )
      .withTopics(
        topic(JobService.TOPIC_NAME, jobSubmittedTopic _)
      )
      .withAutoAcl(true)
  }

}


case class JobId(id: String)

object JobId {
  implicit val format: Format[JobId] = Json.format[JobId]
}

case class JobDefinition(addresses: List[String])

object JobDefinition {
  implicit val format: Format[JobDefinition] = Json.format[JobDefinition]
}

case class JobSubmitted(id: JobId, definition: JobDefinition)

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format[JobSubmitted]
}
