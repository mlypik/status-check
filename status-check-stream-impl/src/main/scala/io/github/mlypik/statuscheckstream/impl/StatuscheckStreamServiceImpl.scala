package io.github.mlypik.statuscheckstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import io.github.mlypik.statuscheckstream.api.StatuscheckStreamService
import io.github.mlypik.statuscheck.api.StatuscheckService

import scala.concurrent.Future

/**
  * Implementation of the StatuscheckStreamService.
  */
class StatuscheckStreamServiceImpl(statuscheckService: StatuscheckService) extends StatuscheckStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(statuscheckService.hello(_).invoke()))
  }
}
