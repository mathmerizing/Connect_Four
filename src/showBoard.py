import pygame
import sys
import time

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
YELLOW = (255, 255, 0)
RED = (255, 0, 0)

# WIDTH and HEIGHT of each grid cell
WIDTH = 20
HEIGHT = 20

# margin between each cell
MARGIN = 5

grid = [[0 for j in range(7)] for i in range(6)]

# -----------------------------------------------------------------------

def addTilesToBoard(redTiles,yellowTiles):
    print(redTiles)
    print(yellowTiles)
    redStones = []
    for s in redTiles.split("_"):
        a,b = s.split(",")
        redStones.append([int(a),int(b)])

    yellowStones = []
    if (yellowTiles != ""):
        for s in yellowTiles.split("_"):
            a,b = s.split(",")
            yellowStones.append([int(a),int(b)])

    for l in redStones:
        grid[l[0]][l[1]] = -1

    for l in yellowStones:
        grid[l[0]][l[1]] = 1

    print(grid)
    for g in grid: print(*g)
# ----------------------------------------------------------------------

if __name__ == "__main__":
    pygame.init() # Initialize pygame

    addTilesToBoard(sys.argv[1],sys.argv[2] if sys.argv[2] != "-timer" else "")
    timer = False
    if ("-timer" in sys.argv): timer = True

    screen = pygame.display.set_mode([180, 155]) # set the screen size
    pygame.display.set_caption("Board")
    done = False
    clock = pygame.time.Clock()
    startTime = time.time()
    print(f"start time: {startTime}")

    while not done:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                done = True

        screen.fill(BLACK)

        # Draw the grid
        for row in range(6):
            for column in range(7):
                color = WHITE
                if grid[row][column] == 1:
                    color = YELLOW
                elif grid[row][column] == -1:
                    color = RED
                pygame.draw.rect(screen,
                                 color,
                                 [(MARGIN + WIDTH) * column + MARGIN,
                                  (MARGIN + HEIGHT) * row + MARGIN,
                                  WIDTH,
                                  HEIGHT])

        # Limit to 60 frames per second
        clock.tick(60)

        # update screen
        pygame.display.flip()

        # check if time is up
        currTime = time.time()
        print(f"curr time: {currTime}")
        if (timer and currTime - startTime >= 2):
            done = True

    pygame.quit()
