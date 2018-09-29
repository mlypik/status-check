package io.github.mlypik.statuscheckstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import io.github.mlypik.statuscheckstream.api.StatuscheckStreamService
import io.github.mlypik.statuscheck.api.StatuscheckService
import com.softwaremill.macwire._

class StatuscheckStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new StatuscheckStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new StatuscheckStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[StatuscheckStreamService])
}

abstract class StatuscheckStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[StatuscheckStreamService](wire[StatuscheckStreamServiceImpl])

  // Bind the StatuscheckService client
  lazy val statuscheckService = serviceClient.implement[StatuscheckService]
}
