package riemann

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import functions.Noise
import riemann.model.{Event, MyRiemannClient}

import scala.concurrent.duration.DurationInt

object EventSender {
  import GraphDSL.Implicits._

  def main(args: Array[String]) {
    implicit val system = ActorSystem("riemann-events")
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()

    val riemannClient = createRiemannClient()

    val constant = ScheduleItem(_ => eventWithMetric(0.0), 20.seconds)
    val sinusoid = ScheduleItem(t => eventWithMetric(Math.sin(t / 4000.0) + 3 + Noise.white / 4.0), 60.seconds)
    val scheduler = new EventGenerator(Seq(sinusoid, constant), 1000.milliseconds, system)

    val source = Source.actorRef[Event](0, OverflowStrategy.dropTail)
    val sink = Sink.ignore

    val sendEvent = Flow.fromFunction[Event, Unit](riemannClient.sendEvent)

    val g = RunnableGraph.fromGraph(GraphDSL.create(source) { implicit b =>
      src =>
        src ~> sendEvent ~> sink
        ClosedShape
    })

    val actor = g.run()
    scheduler.start(actor)
  }

  def eventWithMetric(metric: Double): Event =
    Event(Seq(Event.Host("TEST HOST"), Event.Metric(metric)))

  def createRiemannClient(): MyRiemannClient = {
    val conf = ConfigFactory.load("riemann-event-generator.conf")
    val host = conf.getString("riemann.host")
    val port = conf.getInt("riemann.port")
    MyRiemannClient(host, port)
  }

  def event(metricFunc: () => Double) = new Event(Seq(
    Event.Host("TEST HOST"),
    Event.Service("test"),
    Event.Metric(metricFunc())
  ))

}