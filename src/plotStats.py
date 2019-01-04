import matplotlib.pyplot as plt
import sys
import os

def showPlot(magicNumber,plotAll):
    fileName = os.path.join("saved",magicNumber,"stats.txt")
    print(fileName)

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

    plt.plot(x, maxs, color='green',label='maximum')
    if (plotAll):
        plt.plot(x, uppers, color='blue',label='upper quartile')
        plt.plot(x, medians, color='orange',label='median')
        plt.plot(x, lowers, color='purple',label='lower quartile')
    plt.plot(x, mins, color='red',label='minimum')

    plt.xlabel('EPOCH')
    plt.ylabel('FITNESS')
    plt.title('EVOLUTION ' + magicNumber)
    plt.hlines(y=209, xmin=0, xmax=len(L), linewidth=1, color='green',linestyles='dotted',label='win')
    plt.hlines(y=176, xmin=0, xmax=len(L), linewidth=1, color='black',linestyles='dotted',label='tie')
    plt.hlines(y=101, xmin=0, xmax=len(L), linewidth=1, color='red',linestyles='dotted',label='loose')

    plt.legend(loc=4,fancybox=True, shadow=True)
    plt.show()




if __name__ == "__main__":
    magicNumber = sys.argv[1]
    plotAll = False
    if ("-plotAll" in sys.argv): plotAll = True
    showPlot(magicNumber,plotAll)
