package functions

import scala.util.Random

object Functions {
  def jitter(scaleFactor: Double = 1.0): Double =
    (Random.nextDouble() - 0.5) * 2.0 * scaleFactor
}
