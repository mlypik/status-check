package io.github.mlypik.statuscheck.impl

import io.github.mlypik.statuscheck.api
import io.github.mlypik.statuscheck.api.StatuscheckService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the StatuscheckService.
  */
class StatuscheckServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends StatuscheckService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the status-check entity for the given ID.
    val ref = persistentEntityRegistry.refFor[StatuscheckEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the status-check entity for the given ID.
    val ref = persistentEntityRegistry.refFor[StatuscheckEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(StatuscheckEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[StatuscheckEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
