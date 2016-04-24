package riemann

import akka.actor.{ActorRef, ActorSystem}
import riemann.model.Event

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

case class ScheduleItem(generator: Long => Event, duration: FiniteDuration)

class EventGenerator(schedule: Seq[ScheduleItem], tickRate: FiniteDuration, system: ActorSystem)(implicit ec: ExecutionContext) {

  def start(actor: ActorRef): Unit = {
    schedule.foldLeft(0.seconds){ case (delay, item) =>
      initializeScheduleItem(item, delay, actor)
      delay + item.duration
    }
  }

  def initializeScheduleItem(item: ScheduleItem, delay: FiniteDuration, actor: ActorRef): Unit = {
    val scheduleCanceller = system.scheduler.schedule(delay, tickRate, new Runnable {
      override def run(): Unit = actor ! item.generator(System.currentTimeMillis())
    })
    system.scheduler.scheduleOnce(delay + item.duration)(scheduleCanceller.cancel())
  }

}
