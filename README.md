- Command-line compile.  In the mnkgame/ directory run::

		javac src/mnkgame/*.java -d bin/mnkgame


MNKGame application:

- Human vs Computer.  In the mnkgame/ directory run:
	
		java -cp bin/mnkgame mnkgame.MNKGame 3 3 3 mnkgame.RandomPlayer


- Computer vs Computer. In the mnkgame/ directory run:

		java -cp bin/mnkgame mnkgame.MNKGame 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer


MNKPlayerTester application:

- Output score only:

	java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer

- Verbose output

	java -cp bin/mnkgame mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer -v


- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)


	java -cp target mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer -v -t 1 -r 10
