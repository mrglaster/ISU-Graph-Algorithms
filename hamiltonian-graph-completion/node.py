import copy


class Node:

    def __init__(self, name: str):
        self.name = name
        self.neighbours = {}

    def copy(self):
        return copy.deepcopy(self.neighbours)

    def get_neighbours_count(self):
        return len(self.neighbours.keys())

    def get_neighbours(self):
        return self.neighbours

    def add_neighbour(self, name, distance):
        self.neighbours[name] = distance

    def delete_neighbour(self, name):
        self.neighbours.pop(name)

    def __str__(self):
        return f"Node(name={self.name} neighbours={self.neighbours})"
