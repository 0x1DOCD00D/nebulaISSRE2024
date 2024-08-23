from pymoo.gradient.automatic import AutomaticDifferentiation
from pymoo.problems.multi import ZDT1
import numpy as np

problem = ZDT1()

problem = AutomaticDifferentiation(problem)

X = np.random.random((100, problem.n_var))

F, dF = problem.evaluate(X, return_values_of=["F", "dF"])

print()
