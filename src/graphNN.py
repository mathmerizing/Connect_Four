import networkx as nx
import matplotlib.pyplot as plt
import sys
import os
import pickle

# create a dictionary with predecessors for each node
def predecessorDict(enabled,nodes):
    dict = {}
    for node in nodes:
        dict[node] = []
    for edge in enabled:
        dict[edge[1]].append(edge[0])
    return dict


# returns the layer in which the node is positioned in ------------------------------------------------------------------------------------------------------------
def inLayer(Graph,node,predecessorDict):
    return max([inLayer(Graph,n,predecessorDict)+1 for n in predecessorDict[node]]+[0])


# helper function for neatGraph -----------------------------------------------------------------------------------------------------------------------------------
def _heightCalculator(n,k,i):
    if (n == k):
        return i-1
    else:
        return i*((n-1.0)/(k+1.0))

# make a neat graph (pun intended) --------------------------------------------------------------------------------------------------------------------------------
def neatGraph(nodes,edges,fileName,showWeights = False, saveGraph = False, showGraph = False, printConnections = False):
    #create a directed graph
    DG = nx.DiGraph()
    enabled = []
    disabled = []
    for e in edges:
        FROM, TO, WEIGHT = e[0]
        DG.add_weighted_edges_from([(FROM,TO,round(WEIGHT,2))])
        if e[1] == True:
            enabled.append((FROM,TO))
        else:
            disabled.append((FROM,TO))

    #test for cycles in graph:
    assert nx.number_of_selfloops(DG) == 0

    if(printConnections):
        print(f"enabled connnections = {enabled}")
        print(f"disabled connnections = {disabled}")
        print(f"edges: \n {DG.edges()}")

    # sort nodes by type
    inNodes, hiddenNodes, outNodes = [], [], []
    for tupel in nodes:
        if tupel[0] == 'input':
            inNodes.append(tupel[1])
        elif tupel[0] == 'hidden':
            hiddenNodes.append(tupel[1])
        else:
            outNodes.append(tupel[1])

    nodeDict = {}
    for inNode in inNodes:
        nodeDict[inNode] = 0
    for hidden in hiddenNodes:
        nodeDict[hidden] = inLayer(DG,hidden,predecessorDict(enabled,inNodes+hiddenNodes+outNodes))
    outputDepth = max(nodeDict.values()) + 1
    for outNode in outNodes:
        nodeDict[outNode] = outputDepth

    #calculate positions of nodes
    layerDict = {}
    for depth in set(nodeDict.values()):
        layerDict[depth] = sum(1 for x in nodeDict.values() if x == depth)
    counterDict = {}
    maxNodes = max(list(layerDict.values()))
    for n in DG.nodes():
        depth = nodeDict[n]
        k = layerDict[depth]
        if (counterDict.get(depth) == None):
            DG.node[n]['pos'] = (depth,_heightCalculator(maxNodes,k,1))
            counterDict[depth] = 2
        else:
            DG.node[n]['pos'] = (depth,_heightCalculator(maxNodes,k,counterDict[depth]))
            counterDict[depth] += 1

    pos = nx.get_node_attributes(DG,'pos')
    weights = nx.get_edge_attributes(DG,'weight')

    #color the nodes
    colorMap = []
    for node in DG.nodes():
        if (node in inNodes):
            colorMap.append('red')
        elif (node in outNodes):
            colorMap.append('green')
        else:
            colorMap.append('blue')

    #draw nodes, edges, labels
    ax = plt.subplot(111)
    nx.draw_networkx_nodes(DG, pos, node_color = colorMap)
    nx.draw_networkx_labels(DG, pos)
    nx.draw_networkx_edges(DG, pos, edgelist = enabled, alpha = 0.1)
    nx.draw_networkx_edges(DG, pos, edge_color = 'r', edgelist = disabled,  alpha = 0.3)
    if (showWeights): nx.draw_networkx_edge_labels(DG, pos, edge_labels = weights, font_size = 6)
    plt.axis('off')
    plt.title(fileName)
    if (saveGraph):
        with open(os.path.join('saved',fileName.replace('.txt','.pickle')),'wb') as file:
            pickle.dump(ax, file)
    if (showGraph): plt.show()



# ----------------------------------------------------------------------------------------------------------------------------------------------------------------
def strToInfo(fileName):
    #print(fileName)
    #print(os.listdir('saved'))
    data = ""
    with open(os.path.join('saved',fileName), 'r') as file:
        for line in file:
            data += line

    points,connections = data.split("\n\n")[1:3] # only nodes and edges are important
    nodes = []
    for p in points.split("\n"):
        type, number = p.split("_")
        nodes.append((type,int(number)))
    edges = []

    for c in connections.split("\n"):
        try:
            if (c != ""):
                fromNode, toNode, weight, isExpressed, _ = c.split("_")
                edges.append([(int(fromNode),int(toNode),float(weight)),(True if isExpressed == "true" else False)])
        except Exception as e:
            print(f"'{c}' is not a valid connection", file=sys.stderr)


    return (nodes,edges)

# MAIN -------------------------------------------------------------------------------------------------------------------------------------------------------------
def main(fileName,showW,save,showG,printC):
    # create the graph
    neatGraph(*strToInfo(fileName),fileName,showWeights = showW,saveGraph = save,showGraph = showG,printConnections = printC)
    pass

# an example to test some functions ------------------------------------------------------------------------------------------------------------------------------
def test():
    edges = [ [(1,2,0.5),True], [(3,4,0.4),True], [(3,2,0.7),True], [(2,4,1.0),True], [(5,6,-0.5),True], [(6,4,0.6698),True], [(7,4,0.22),False] ]
    nodes = [ ('input',1), ('input',3), ('input',5), ('input',7), ('hidden',6), ('hidden',2), ('output',4) ]
    neatGraph(nodes,edges,'test',showWeights = True, showGraph = True)

# execaution of the code -----------------------------------------------------------------------------------------------------------------------------------------
if __name__ == "__main__":
    #test() // this is only for test purposes
    fileName = sys.argv[1]
    print("python3: \t" + fileName)
    showWeights, save, showGraph, printConnections = False, False, False, False
    if ("-showWeights" in sys.argv): showWeights = True
    if ("-saveGraph" in sys.argv): save = True
    if ("-showGraph" in sys.argv): showGraph = True
    if ("-printConnections" in sys.argv): printConnections = True
    main(fileName,showWeights,save,showGraph,printConnections)
