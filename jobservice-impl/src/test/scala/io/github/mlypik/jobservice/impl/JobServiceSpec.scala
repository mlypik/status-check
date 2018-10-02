package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import io.github.mlypik.jobservice.api.{JobDefinition, JobService}
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

  "job service" should {
    "respond with job id" in {
      client.submit().invoke(JobDefinition(List("192.168.1.1"))).map { answer =>
        answer shouldBe a[String]
      }
    }
  }
}
