from graph import Graph
from hamiltonian_filler import HamiltonianFiller

graph = Graph('data/1000_forlabs.txt', ',')
filler = HamiltonianFiller()
res = filler.complete_hamiltonian_cycle(graph)
print(res.hamiltonian_circuit_path)