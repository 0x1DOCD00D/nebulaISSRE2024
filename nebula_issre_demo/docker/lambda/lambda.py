import numpy as np
from pymoo.core.problem import Problem
from pymoo.algorithms.moo.nsga2 import NSGA2
from pymoo.optimize import minimize
from pymoo.operators.mutation.bitflip import BitflipMutation
from pymoo.operators.crossover.ux import UniformCrossover
from pymoo.operators.sampling.rnd import BinaryRandomSampling
from pymoo.termination import get_termination
import firebase_admin
from firebase_admin import credentials, db
from prometheus_api_client import PrometheusConnect, MetricRangeDataFrame
from datetime import timedelta, datetime
from prometheus_api_client.utils import parse_datetime
from datetime import timedelta
from fastapi import FastAPI
from mangum import Mangum


# Fetch the service account key JSON file contents
cred = credentials.Certificate('nebula-cf706-firebase-adminsdk-cjswy-6d96607db5.json')

# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://nebula-cf706-default-rtdb.firebaseio.com/'
})

app = FastAPI()


class ActorPlacementProblem(Problem):

    def __init__(self):
        super().__init__(n_var=10,  # Number of actors
                         n_obj=3,  # Maximize CPU, Maximize RAM, Minimize Response Time
                         n_constr=0,  # No constraints in this case
                         xl=0,  # Lower bounds for the binary variables
                         xu=1,  # Upper bounds for the binary variables
                         type_var=np.bool_)  # Binary variables (actor placement)

    def _evaluate(self, x, out, *args, **kwargs):
        # Example: Each actor has some CPU and RAM demand and response time contribution
        cpu_demand, ram_demand = retrieve_data_from_firebase()
        response_time_contribution = retrieve_response_time()

        # Split the actors according to their node assignment
        node1_actors = (x == 0).astype(int)
        node2_actors = (x == 1).astype(int)

        # Calculate the CPU, RAM utilization and Response time for each node
        cpu_node1 = np.sum(cpu_demand * node1_actors, axis=1)
        cpu_node2 = np.sum(cpu_demand * node2_actors, axis=1)

        ram_node1 = np.sum(ram_demand * node1_actors, axis=1)
        ram_node2 = np.sum(ram_demand * node2_actors, axis=1)

        response_time_node1 = np.sum(response_time_contribution * node1_actors, axis=1)
        response_time_node2 = np.sum(response_time_contribution * node2_actors, axis=1)

        # Objectives: We want to maximize CPU and RAM, minimize response time
        # Since Pymoo minimizes objectives, to maximize CPU and RAM we minimize their negatives
        f1 = - np.minimum(cpu_node1, cpu_node2)  # Maximize minimum CPU utilization
        f2 = - np.minimum(ram_node1, ram_node2)  # Maximize minimum RAM utilization
        f3 = np.maximum(response_time_node1, response_time_node2)  # Minimize maximum response time

        out["F"] = np.column_stack([f1, f2, f3])


def main():
    # Define the genetic algorithm with Pymoo
    algorithm = NSGA2(
        pop_size=50,  # Population size
        sampling=BinaryRandomSampling(),
        crossover=UniformCrossover(prob=0.9),
        mutation=BitflipMutation(prob=0.2),
        eliminate_duplicates=True
    )

    # Define termination criteria (e.g., number of generations)
    termination = get_termination("n_gen", 100)

    # Run the optimization
    problem = ActorPlacementProblem()
    res = minimize(problem,
                   algorithm,
                   termination,
                   seed=1,
                   save_history=True,
                   verbose=True)

    # Print the results
    # print("Best solution found: \nX = %s\nF = %s" % (res.X, res.F))

    # serialize the result to a dictionary
    return res.X[0].tolist()  # Convert numpy array to list

def retrieve_data_from_firebase():
    ref = db.reference('demo')

    # Get the data from Firebase
    data = ref.get()

    # Extract the specific values
    cpuLoad = data['exp1']['cpu']['cpuLoad']
    systemCpuLoad = data['exp1']['cpu']['systemCpuLoad']
    freeMemory = data['exp1']['memory']['freeMemory']
    totalMemory = data['exp1']['memory']['totalMemory']
    usedMemory = data['exp1']['memory']['usedMemory']

    # Store CPU in a numpy array
    cpu_demand = np.array([cpuLoad, systemCpuLoad])

    # Store RAM in a numpy array
    ram_demand = np.array([freeMemory, totalMemory])

    cpu_demand = np.concatenate([np.zeros(8), cpu_demand])
    ram_demand = np.concatenate([np.zeros(8), ram_demand])

    print(cpu_demand, ram_demand)

    return cpu_demand, ram_demand

def retrieve_response_time():
    # Connect to Prometheus server
    prom = PrometheusConnect(url="http://prometheus:9090", disable_ssl=True)

    # Get the data for the metric
    start_time = parse_datetime("2d")
    end_time = parse_datetime("now")
    chunk_size = timedelta(days=1)
    data = prom.get_metric_range_data("akka_dispatcher_processing_time_ns_count",
                                      start_time=start_time,
                                      end_time=end_time,
                                      chunk_size=chunk_size)

    # Convert the data to a pandas DataFrame
    df = MetricRangeDataFrame(data)

    # Print the DataFrame
    print(df)

    # Get the value of the most ten recent data points
    # extract the 'Value' column of the most recent 10 data points
    values = df['value'].tail(10).values
    print(values)
    return values


# Endpoint to run the genetic algorithm
@app.get("/run_ga")
def run_ga():
    try:
        result = main()
        return {"solution": result}
    except Exception as e:
        return {"error": str(e)}

# Root endpoint
@app.get("/")
def read_root():
    return {"Welcome": "Welcome to the FastAPI on Lambda"}


# Mangum adapter to handle API Gateway requests
handler = Mangum(app)
