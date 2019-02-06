import matplotlib.pyplot as plt
import matplotlib.animation as animation
import numpy as np
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
    wins = [l[0] for l in L]
    ties = [l[1] for l in L]
    losses = [l[2] for l in L]

    ax1.clear()
    plt.xlabel('EPOCH')
    plt.ylabel('PERFORMANCE')
    plt.title('EVOLUTION ' + magicNumber)

    width = 0.35  # the width of the bars

    p1 = ax1.bar(x, losses, width, color='red',label='loss %')
    p2 = ax1.bar(x, ties, width, color='orange',bottom=losses,label='tie %')
    p3 = ax1.bar(x, wins, width, color='green',bottom=[ties[i] + losses[i] for i in range(len(ties))],label='tie %')

    ax1.set_yticks(np.arange(0, 101, 25))
    ax1.legend(loc=4,fancybox=True, shadow=True)
    manager = plt.get_current_fig_manager()
    manager.set_window_title("Performance")
    manager.resize(*[int(dim/2) for dim in manager.window.maxsize()])


def showPlot(magicNumber):
    fileName = os.path.join("saved",magicNumber,"performance.txt")
    print(fileName)

    ani = animation.FuncAnimation(fig, animate, fargs = [fileName], interval = 10000)
    plt.show()


if __name__ == "__main__":
    magicNumber = sys.argv[1]
    showPlot(magicNumber)
