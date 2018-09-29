package io.github.mlypik.statuscheck.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import io.github.mlypik.statuscheck.api.StatuscheckService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class StatuscheckLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new StatuscheckApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new StatuscheckApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[StatuscheckService])
}

abstract class StatuscheckApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[StatuscheckService](wire[StatuscheckServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = StatuscheckSerializerRegistry

  // Register the status-check persistent entity
  persistentEntityRegistry.register(wire[StatuscheckEntity])
}
