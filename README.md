# CrazyPlayer
<img src="logo.png" width=40%><br>

Algorithms and Data Structures Project, UniBO 21/22.

## Introduction

The primary goal of this project is to develop a software player capable of playing optimally in all possible instances of the **(M,N,K)-game**.

The problem is equivalent to solving a complex **Game tree**, and as the game grid increases, the resource load for genarating the game tree grows exponentially.

To tackle this, various optimization techniques have been employed along with some heuristic functions. See [Techniques Used](#Techniques-Used).

## Source Code
The entire development activity is contained within the *CrazyPlayer* package *(other classes were provided by the instructor)*: [CrazyPlayer folder](src/mnkgame/CrazyPlayer)

## Resources:
- Consult the project report: [click here](Relazione%20Progetto%20Algoritmi%20A.A.%202020_2021.pdf)
- Ranking among the players made by students *(my player is CrazyPlayerPazzissimo)*: [click here](ClassificaASD2021.pdf)
- (M,N,K)-game wiki: [click here](https://en.wikipedia.org/wiki/M,n,k-game)

## Techniques Used
- Alpha-Beta Pruning
- Reducing playable moves
- Sorting moves using *TreeSet*
- Transposition Table
- BitBoard
- Searching through symmetric boards
- Iterative Deepening

## Build

- Command-line compile:
	```bash
	javac src/mnkgame/**/*.java -d bin/mnkgame
	```

## Run

### MNKGame application:

- Human vs Computer:
	```bash
	java -cp bin/mnkgame mnkgame.MNKGame 3 3 3 mnkgame.CrazyPlayer.CrazyPlayer
	```

- Computer vs Computer:
	```bash
	java -cp bin/mnkgame mnkgame.MNKGame 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer
	```

### MNKPlayerTester application:

- Output score only:
	```bash
	java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer
	```

- Verbose output:
	```bash
	java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer -v
	```

- Verbose output with customized timeout (1 sec) and number of game repetitions (10 rounds):
	```bash
	java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer -v -t 1 -r 10
	```