package functions

import scala.util.Random

object Noise {
  def white = Random.nextDouble() * 2.0 - 1.0
}
