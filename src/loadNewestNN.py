import matplotlib.pyplot as plt
import sys
import os
import pickle
import glob

def loadNewestPickle(directory,pauseTime):
     plt.ion()
     while True:
         newestPickle = max(glob.iglob(os.path.join('saved',directory,'*.pickle')), key=os.path.getctime)
         print(newestPickle)
         with open(newestPickle,'rb') as file:
             ax = pickle.load(file)
             manager = plt.get_current_fig_manager()
             manager.resize(*[int(dim/2) for dim in manager.window.maxsize()])
             manager.set_window_title("Newest Best Genome")
             plt.pause(pauseTime)
             plt.close()

if __name__ == "__main__":
    directory = sys.argv[1]
    pauseTime = 1
    if (len(sys.argv) == 3): pauseTime = int(sys.argv[2])
    loadNewestPickle(directory,pauseTime)
