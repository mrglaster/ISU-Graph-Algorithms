import time
from graph import Graph


class HamiltonianFiller:

    def __init__(self, calculation_timeout: int = 10):
        self.__graph_instance = None
        self.__minimum_edge_count: int = -1
        self.__hamiltonian_cycle: list = []
        self.__longest_chain_length: int = -1
        self.__longest_chain: list = []
        self.__start_time: float = time.time()
        self.__calculation_timeout = calculation_timeout

    def __update_circuit_data(self, queue=None, edge_count: int = 0):
        if queue is None:
            queue = []
        if edge_count < self.__minimum_edge_count or self.__minimum_edge_count == -1:
            self.__minimum_edge_count = edge_count
            self.__hamiltonian_cycle = queue.copy()
            self.__start_time = time.time()

    def __find_longest_chain(self, current_longest_chain: list, start_pos: int = 0):
        for l in range(start_pos + 1, len(current_longest_chain)):
            if current_longest_chain[start_pos + 1] in list(
                    self.__graph_instance.nodes[current_longest_chain[start_pos]].neighbours.keys()):
                self.__find_longest_chain(current_longest_chain.copy(), start_pos + 1)
            elif self.__longest_chain_length == -1 or start_pos > self.__longest_chain_length:
                self.__longest_chain_length = start_pos
                self.__longest_chain = current_longest_chain[:start_pos]
            current_longest_chain.append(current_longest_chain.pop(start_pos + 1))

    def generate_hamiltonian_cycle(self, current_longest_chain=None, start_pos: int = 0, edge_count: int = 0):
        if current_longest_chain is None:
            current_longest_chain = []

        if time.time() - self.__start_time > self.__calculation_timeout:
            return

        if self.__minimum_edge_count != -1 and edge_count >= self.__minimum_edge_count:
            return

        if not current_longest_chain:
            current_longest_chain = list(self.__graph_instance.nodes.keys())
            self.__find_longest_chain(current_longest_chain)
            if self.__longest_chain_length != -1:
                for i in self.__longest_chain:
                    if i in current_longest_chain:
                        current_longest_chain.remove(i)
                current_longest_chain = self.__longest_chain + current_longest_chain
            start_pos = self.__longest_chain_length

        if start_pos + 1 >= len(current_longest_chain):
            if current_longest_chain[0] not in self.__graph_instance.nodes[current_longest_chain[-1]].get_neighbours():
                edge_count += 1
            self.__update_circuit_data(current_longest_chain, edge_count)
            return

        new_queue = current_longest_chain.copy()
        new_queue = new_queue[start_pos:]
        self.__longest_chain_length = -1
        self.__longest_chain = []
        self.__find_longest_chain(new_queue)
        start_pos_copy = start_pos
        if self.__longest_chain_length > 0:
            for i in self.__longest_chain:
                if i in current_longest_chain:
                    current_longest_chain.remove(i)
            current_longest_chain = self.__longest_chain + current_longest_chain
            start_pos_copy += self.__longest_chain_length - 1
            self.generate_hamiltonian_cycle(current_longest_chain.copy(), start_pos_copy, edge_count)

        if start_pos == 0:
            return

        for l in range(start_pos + 1, len(current_longest_chain)):
            if current_longest_chain[start_pos + 1] not in list(self.__graph_instance.nodes[current_longest_chain[start_pos]].neighbours.keys()):
                new_queue = current_longest_chain.copy()
                new_queue = new_queue[start_pos + 1:]
                self.__longest_chain_length = -1
                self.__longest_chain = []
                self.__find_longest_chain(new_queue)
                start_pos_copy = start_pos + 1
                if self.__longest_chain_length > 0:
                    for i in self.__longest_chain:
                        if i in current_longest_chain:
                            current_longest_chain.remove(i)
                    current_longest_chain = self.__longest_chain + current_longest_chain
                    start_pos_copy += self.__longest_chain_length - 1
                self.generate_hamiltonian_cycle(current_longest_chain.copy(), start_pos_copy, edge_count + 1)
                if start_pos + 1 == start_pos_copy:
                    return
                if time.time() - self.__start_time > self.__calculation_timeout:
                    break
            current_longest_chain.append(current_longest_chain.pop(start_pos + 1))

    def create_result_object(self, new_connection_length: int = 1):
        count = 0
        for current_node in range(len(self.__hamiltonian_cycle)):
            next_node = current_node + 1
            if next_node >= len(self.__hamiltonian_cycle):
                next_node = 0
            if self.__hamiltonian_cycle[next_node] not in self.__graph_instance.nodes[
                self.__hamiltonian_cycle[current_node]].get_neighbours():
                self.__graph_instance.add_edge(self.__hamiltonian_cycle[current_node],
                                               self.__hamiltonian_cycle[next_node], new_connection_length)
                count += 1
        print(f"Edges added: {count}")
        self.__graph_instance.hamiltonian_circuit_path = self.__hamiltonian_cycle
        return self.__graph_instance

    def complete_hamiltonian_cycle(self, graph: Graph, new_connection_length: int = 1):
        if graph.is_hamiltonian_by_ore():
            print("Your graph already has a Hamiltonian circuit according to the Ore's theorem")
            return graph

        self.__graph_instance = graph.copy()
        self.generate_hamiltonian_cycle()

        if self.__minimum_edge_count == -1:
            raise ValueError("Unable to complete the Hamiltonian graph")

        return self.create_result_object(new_connection_length)
