import matplotlib.pyplot as plot
from mpl_toolkits.axes_grid1 import make_axes_locatable
import numpy as np
import csv
import matplotlib.ticker as ticker

with open ("C:/Users/miisiekkk/PycharmProjects/mes_results/test1.csv", newline="") as plikCSV:
    dtau = 120

    reader = csv.reader(plikCSV, delimiter=",")
    cycleCounter = 0
    rowCounter = 0
    numbers = []
    numbers.append([])
    for row in reader:
        tempNumbers = numbers[cycleCounter]
        if row[0] == '\n':
            cycleCounter += 1
            numbers.append([])
            rowCounter = 0
        else:
            tempRow = row[0].split(', ')
            for i in range(len(tempRow)):
                tempRow[i] = float(tempRow[i])

            rowCounter += 1
            tempNumbers.append(tempRow)

    numOfRows = len(numbers) - 1
    numOfChosen = int(numOfRows/12)
    chosen = [i * numOfChosen for i in range(12)]

    fig, axes = plot.subplots(nrows=3, ncols=4)
    numbersIter = 0
    for ax in axes.flat:
        im = ax.imshow(numbers[chosen[numbersIter]])
        ax.set_xlabel("CM")
        ax.set_ylabel("CM")

        ax.xaxis.set_major_locator(ticker.FixedLocator((np.arange(0, 50, 10))))
        ax.yaxis.set_major_locator(ticker.FixedLocator((np.arange(0, 50, 10))))

        ax.set_title(str(chosen[numbersIter] * dtau + 120) + "s")
        numbersIter += 1

        divider = make_axes_locatable(ax)
        cax = divider.append_axes("right", size="5%", pad=0.1)
        fig.colorbar(im, cax=cax)

    plot.show()