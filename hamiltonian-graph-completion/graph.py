import copy

from node import Node


class Graph:

    def __init__(self, input_file_name: str, file_separator: str):
        self.nodes = {}
        self.added_edges = 0
        self.hamiltonian_circuit_path = []
        if input_file_name != "":
            self.read_file(input_file_name, file_separator)

    def copy(self):
        return copy.deepcopy(self)

    def read_file(self, file_name: str, separator: str):
        with open(file_name, 'r', encoding='utf-8') as file:
            for i in file.readlines():
                i = i.split(separator)
                if len(i) >= 3:
                    self.add_edge(i[0], i[1], int(i[2]))

    def add_node(self, node_name):
        if node_name not in self.nodes:
            self.nodes[node_name] = Node(node_name)

    def add_edge(self, source_name, destination_name, distance):
        if source_name not in self.nodes:
            self.add_node(source_name)

        if destination_name not in self.nodes:
            self.add_node(destination_name)
        self.nodes[source_name].add_neighbour(destination_name, distance)

    def remove_edge(self, source_name, destination_name):
        if source_name not in self.nodes or destination_name not in self.nodes or source_name == destination_name:
            return
        self.nodes[source_name].delete_neighbour(destination_name)

    def is_hamiltonian_by_ore(self):
        for i in self.nodes.values():
            neighbours = i.get_neighbours()
            other_nodes = list(self.nodes.keys())
            other_nodes.remove(i.name)
            for j in neighbours:
                if j in other_nodes:
                    other_nodes.remove(j)

            for j in other_nodes:
                if i.name in list(self.nodes[j].get_neighbours().keys()):
                    continue
                if self.nodes[j].get_neighbours_count() + len(neighbours) >= len(self.nodes.keys()):
                    return True
        return False

    def write_to_file(self, output_file: str = "result.txt"):
        res = ""
        for node in self.nodes:
            for neighbour in self.nodes[node].get_neighbours():
                substring = f"{node},{neighbour},{self.nodes[node].neighbours[neighbour]}\n"
                res += substring
                print(substring, end='')
        with open(output_file, 'w') as file:
            file.write(res)

    def __str__(self):
        return f"Nodes: {[str(i) for i in self.nodes.values()]}"
