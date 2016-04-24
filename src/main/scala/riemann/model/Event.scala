package riemann.model

object Event {
  sealed trait Config
  case class Host(host: String) extends Config
  case class Service(service: String) extends Config
  case class State(state: String) extends Config
  case class Time(time: Long) extends Config
  case class Description(description: String) extends Config
  case class Tag(tag: String) extends Config
  case class Metric(metric: Double) extends Config
  case class Ttl(metric: Float) extends Config
}

case class Event(configs: Seq[Event.Config])
