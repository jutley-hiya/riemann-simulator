package riemann.model

import com.aphyr.riemann.client.{EventDSL, RiemannClient => AphyrRiemannClient}

case class RiemannClient(host: String = "localhost", port: Int = 5555) {

  val riemannClient: AphyrRiemannClient = AphyrRiemannClient.tcp(host, port)
  riemannClient.connect()

  def sendEvent(event: Event): Unit = {
    val aphyrEvent = event.configs.foldLeft(new EventDSL(riemannClient)) {
      case (tempAphyrEvent, config) => applyEventConfig(tempAphyrEvent, config)
    }
    riemannClient.sendEvent(aphyrEvent.build())
  }

  def applyEventConfig(eventDSL: EventDSL, config: Event.Config): EventDSL = config match {
    case Event.Host(host)               => eventDSL.host(host)
    case Event.Service(service)         => eventDSL.service(service)
    case Event.State(state)             => eventDSL.state(state)
    case Event.Time(time)               => eventDSL.time(time)
    case Event.Description(description) => eventDSL.description(description)
    case Event.Tag(tag)                 => eventDSL.tag(tag)
    case Event.Metric(metric)           => eventDSL.metric(metric)
    case Event.Ttl(ttl)                 => eventDSL.metric(ttl)
  }
}
