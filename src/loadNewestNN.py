import matplotlib.pyplot as plt
import sys
import os
import pickle
import glob

def loadNewestPickle(directory):
     plt.ion()
     while True:
         newestPickle = max(glob.iglob(os.path.join('saved',directory,'*.pickle')), key=os.path.getctime)
         print(newestPickle)
         with open(newestPickle,'rb') as file:
             ax = pickle.load(file)
             plt.pause(1)
             plt.close()

if __name__ == "__main__":
    directory = sys.argv[1]
    loadNewestPickle(directory)
