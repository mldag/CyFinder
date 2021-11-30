name = "GBM"

header = ""
edges = set()
with open(name + ".txt") as f:
    header = f.readline()
    lines = f.readlines()

    for line in lines:
        fields = line.split("\t")
        edge = []
        edge.append(fields[0])
        edge.append(fields[1])
        edge.sort()

        edges.add(edge[0] + "\t" + edge[1] + "\t" + fields[2])

edges = list(edges)
edges.sort()

with open (name + " new.txt", "w") as f:
    f.write(header)

    for edge in edges:
        f.write(edge)
