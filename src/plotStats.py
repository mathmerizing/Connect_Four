import matplotlib.pyplot as plt
import matplotlib.animation as animation
import numpy as np
from scipy.interpolate import spline
from scipy.signal import savgol_filter
import sys
import os

fig = plt.figure()
ax1 = fig.add_subplot(1,1,1)

def animate(i,fileName):
    data = ""
    with open(fileName, 'r') as file:
        for line in file:
            data += line

    L = [subList.split("\n") for subList in data.split("\n\n")]
    # convert the elements to floats
    for l in L:
        for i in range(len(l)):
            l[i] = float(l[i])

    x = list(range(1,len(L)+1))
    maxs = [l[0] for l in L]
    uppers = [l[1] for l in L]
    medians = [l[2] for l in L]
    lowers = [l[3] for l in L]
    mins = [l[4] for l in L]

    ax1.clear()
    plt.xlabel('EPOCH')
    plt.ylabel('FITNESS')
    plt.title('EVOLUTION ' + magicNumber)

    ALPHA = 0.2

    #Apply a Savitzky-Golay filter to the maximum array.
    ax1.scatter(x,maxs,c = 'green',marker = '+',s = 10, alpha = ALPHA)
    ax1.plot(x, savgol_filter(maxs, min(51,len(L)-(len(L)+1)%2), 3), color='green',label='maximum')
    if (plotAll):
        ax1.plot(x, savgol_filter(uppers, min(51,len(L)-(len(L)+1)%2), 3), color='blue',label='upper quartile',alpha = ALPHA)
        ax1.plot(x, savgol_filter(medians, min(51,len(L)-(len(L)+1)%2), 3), color='orange',label='median',alpha = ALPHA)
        ax1.plot(x, savgol_filter(lowers, min(51,len(L)-(len(L)+1)%2), 3), color='purple',label='lower quartile',alpha = ALPHA)
    ax1.scatter(x,mins,c = 'red',marker = '+',s = 10, alpha = ALPHA)
    ax1.plot(x, savgol_filter(mins, min(51,len(L)-(len(L)+1)%2), 3), color='red',label='minimum')


    plt.hlines(y=209, xmin=0, xmax=len(L), linewidth=1, color='green',linestyles='dotted',label='win')
    plt.hlines(y=176, xmin=0, xmax=len(L), linewidth=1, color='black',linestyles='dotted',label='tie')
    plt.hlines(y=101, xmin=0, xmax=len(L), linewidth=1, color='red',linestyles='dotted',label='loose')

    ax1.legend(loc=4,fancybox=True, shadow=True)
    manager = plt.get_current_fig_manager()
    manager.set_window_title("Statistics")
    manager.resize(*[int(dim/2) for dim in manager.window.maxsize()])


def showPlot(magicNumber,plotAll):
    fileName = os.path.join("saved",magicNumber,"stats.txt")
    print(fileName)

    ani = animation.FuncAnimation(fig, animate, fargs = [fileName], interval = 10000)
    plt.show()


if __name__ == "__main__":
    magicNumber = sys.argv[1]
    plotAll = False
    if ("-plotAll" in sys.argv): plotAll = True
    showPlot(magicNumber,plotAll)
