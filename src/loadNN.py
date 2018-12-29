import matplotlib.pyplot as plt
import sys
import os
import pickle

def loadPickle(fileName):
    with open(os.path.join('saved',fileName),'rb') as file:
        ax = pickle.load(file)
        plt.show(ax)

if __name__ == "__main__":
    fileName = sys.argv[1]
    loadPickle(fileName)
