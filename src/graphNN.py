import networkx as nx
import matplotlib.pyplot as plt
import sys

# returns the layer in which the node is positioned in ------------------------------------------------------------------------------------------------------------
def inLayer(Graph,node):
    return max([inLayer(Graph,n)+1 for n in Graph.predecessors(node)]+[0])

# returns a dictionary whose value are the number of nodes in the {key}.th layer ----------------------------------------------------------------------------------
def nodesInLayers(Graph):
    layerDict = {}
    for n in Graph.nodes():
        depth = inLayer(Graph,n)
        if (layerDict.get(depth) == None):
            layerDict[depth] = 1
        else:
            layerDict[depth] += 1
    return layerDict

# helper function for neatGraph -----------------------------------------------------------------------------------------------------------------------------------
def _heightCalculator(n,k,i):
    if (n == k):
        return i-1
    else:
        return i*((n-1.0)/(k+1.0))

# make a neat graph (pun intended) --------------------------------------------------------------------------------------------------------------------------------
def neatGraph(edges,title):
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


    #calculate positions of nodes
    layerDict = nodesInLayers(DG)
    counterDict = {}
    maxNodes = max(list(layerDict.values()))
    for n in DG.nodes():
        depth = inLayer(DG,n)
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
    maxDepth = max([inLayer(DG,n) for n in DG.nodes()])
    for node in DG.nodes():
        depth = inLayer(DG,node)
        if (depth == 0):
            colorMap.append('red')
        elif (depth == maxDepth):
            colorMap.append('green')
        else:
            colorMap.append('blue')

    #draw nodes, edges, labels
    nx.draw_networkx_nodes(DG, pos, node_color = colorMap)
    nx.draw_networkx_labels(DG, pos)
    nx.draw_networkx_edges(DG, pos, edgelist = enabled)
    nx.draw_networkx_edges(DG, pos, edgelist = disabled,  alpha = 0.2)
    nx.draw_networkx_edge_labels(DG, pos, edge_labels = weights, font_size = 6)
    plt.axis('off')
    plt.title(title)
    plt.show()


#TO DO / works ?! ------------------------------------------------------------------------------------------------------------------------------------------------
def strToEdges(filePath):
    with open('filePath', 'r') as NNfile:
        data = NNfile.read().replace('\n','')
    return data

#TO DO -----------------------------------------------------------------------------------------------------------------------------------------------------------
def main(NNstring,title):
    # convert NNstring to edges
    edges = []
    # create the graph
    neatGraph(edges,title)
    pass

# an example to test some functions ------------------------------------------------------------------------------------------------------------------------------
def test():
    edges = [ [(1,2,0.5),True], [(3,4,0.4),True], [(3,2,0.7),True], [(2,4,1.0),True], [(5,6,-0.5),True], [(6,4,0.6698),True], [(7,4,0.22),False] ]
    neatGraph(edges,'test')

# execaution of the code -----------------------------------------------------------------------------------------------------------------------------------------
if __name__ == "__main__":
    test()
    filePath = sys.argv[1]
    main(strToEdges(filePath),filePath)
