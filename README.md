# CrazyPlayerPazzissimo

Progetto di Algoritmi e Strutture Dati, Unibo 21/22.

Il **goal** del progetto è sviluppare un giocatore software in grado di giocare in modo ottimale a tutte le istanze possibili del **(M,N,K)-game**

### Link utili:
- Relazione del progetto: [click here](Relazione%20Progetto%20Algoritmi%20A.A.%202020_2021.pdf)
- Classifica giocatori (M,N,K) al *16/03/22*: [click here](ClassificaASD2021.pdf)
- (M,N,K)-game wiki: [click here](https://en.wikipedia.org/wiki/M,n,k-game)

## Build

- Command-line compile:

      javac src/mnkgame/**/*.java -d bin/mnkgame

## Run

### MNKGame application:

- Human vs Computer:
	
		java -cp bin/mnkgame mnkgame.MNKGame 3 3 3 mnkgame.CrazyPlayer.CrazyPlayer

- Computer vs Computer:

		java -cp bin/mnkgame mnkgame.MNKGame 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer


### MNKPlayerTester application:

- Output score only:

        java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer

- Verbose output:

        java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer -v

- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)

        java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.CrazyPlayer.CrazyPlayer mnkgame.QuasiRandomPlayer -v -t 1 -r 10
