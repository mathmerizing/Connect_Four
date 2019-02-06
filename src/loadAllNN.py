import matplotlib.pyplot as plt
from matplotlib import animation
import sys
import os
import pickle
import graphNN


def visualize(directory,pickleFiles):
    fileNum = len(pickleFiles)
    global fileBeginning
    fileBeginning = pickleFiles[0].replace(".pickle","").rsplit("_",1)[0]
    fileBeginning = os.path.join(directory,fileBeginning + "_")
    plt.ion()
    for i in range(fileNum):
        with open(fileBeginning + str(i+1) + ".pickle",'rb') as file:
            ax = pickle.load(file)
            manager = plt.get_current_fig_manager()
            manager.resize(*manager.window.maxsize())
            plt.pause(1)

if __name__ == "__main__":
    directory = os.path.join("saved",sys.argv[1])
    #print(os.listdir(directory))
    txtFiles = []
    pickleFiles = []
    for f in os.listdir(directory):
        if f.endswith(".txt") and f != "stats.txt" and f != "performance.txt":
            txtFiles.append(f)
        elif f.endswith(".pickle"):
            pickleFiles.append(f)
    if len(pickleFiles) == 0:
        # create .pckle files from the .txt files
        for f in txtFiles:
            fileName = os.path.join(sys.argv[1],f)
            print(fileName)
            graphNN.main(fileName,False,True,False,False)
        pickleFiles = [f for f in os.listdir(directory) if f.endswith(".pickle")]
    assert len(txtFiles) == len(pickleFiles)

    visualize(directory,pickleFiles)
