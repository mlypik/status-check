package io.github.mlypik.jobservice.impl

import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import com.softwaremill.macwire.wire
import io.github.mlypik.jobservice.api.JobService
import play.api.libs.ws.ahc.AhcWSComponents


class JobServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = new JobsApplication(context) {
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new JobsApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[JobService])
}

abstract class JobsApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[JobService](wire[JobServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = JobsSerializerRegistry

  // Register the status-check persistent entity
  persistentEntityRegistry.register(wire[JobsEntity])
}