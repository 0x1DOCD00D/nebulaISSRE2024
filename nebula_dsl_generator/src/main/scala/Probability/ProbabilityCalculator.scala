package Probability

import breeze.stats.distributions.Poisson

class ProbabilityCalculator {}

object ProbabilityCalculator:
  def computePoissonDistribution(mean: Double): Int =
    import breeze.stats.distributions.Rand.VariableSeed.randBasis
    Poisson(mean).draw()