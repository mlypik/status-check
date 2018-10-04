package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import io.github.mlypik.jobservice.api.{JobDefinition, JobId, JobService, JobStatus}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class JobServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new JobsApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[JobService]

  override protected def afterAll(): Unit = server.stop()

  "job service submit" should {
    "respond with PRETTY job id" in {
      client.submit().invoke(JobDefinition(List("192.168.1.1"))).map {
        case JobId(jobId) => jobId should fullyMatch regex """[A-Z]{4}-[0-9]{5}-[A-Z]{4}-[0-9]{5}"""
      }
    }
  }

  "job service get status" should {
    "return 'does not exist' for nonexistent job id" in {
      client.getJobStatus(JobId("AAAA-00000-AAAA-01007")).invoke().map{
        case JobStatus(status) => status shouldBe "Does not exist"
      }
    }
  }


}
