import os
from datetime import datetime

COMMAND = "java mnkgame.MNKPlayerTesterCustom 3 3 3 {} {}"
DIR = "/home/manuel/Scuola/UNI/1 Anno/2Semestre/Algoritmi e Strutture Dati/progetto/MNKGame/bin/"

first_player = "mnkgame.CrazyPlayer.CrazyPlayer"
enemy = "mnkgame.QuasiRandomPlayer"
# enemy = "mnkgame.CrazyPlayer.CrazyPlayer"
# enemy = "mnkgame.ENEMY1"
# enemy = "mnkgame.monkey.ENEMY2"

print(f"{datetime.now()}: {first_player.split('.')[-1]} vs {enemy.split('.')[-1]}\n")
input("Insert Local Changes: ")

os.chdir(DIR)
os.system(COMMAND.format(first_player, enemy))

################################################################

# 1. (3, 3, 3, "Patta"),
# 2. (4, 3, 3, "Vittoria (Primo giocatore)"),
# 3. (4, 4, 3, "Vittoria (Primo giocatore)"),
# 4. (4, 4, 4, "Patta"),
# 5. (5, 4, 4, "Patta"),
# 6. (5, 5, 4, "Patta"),
# 7. (5, 5, 5, "Patta"),
# 8. (6, 4, 4, "Patta"),
# 9. (6, 5, 4, "Vittoria (Primo giocatore)"),
# 10. (6, 6, 4, "Vittoria (Primo giocatore)"),
# 11. (6, 6, 5, "Patta"),
# 12. (6, 6, 6, "Patta"),
# 13. (7, 4, 4, "Patta"),
# 14. (7, 5, 4, "Vittoria (Primo giocatore)"),
# 15. (7, 6, 4, "Vittoria (Primo giocatore)"),
# 16. (7, 7, 4, "Vittoria (Primo giocatore)"),
# 17. (7, 5, 5, "Patta"),
# 18. (7, 6, 5, "Patta"),
# 19. (7, 7, 5, "Patta"),
# 20. (7, 7, 6, "Patta"),
# 21. (7, 7, 7, "?"),
# 22. (8, 8, 4, "Vittoria (Primo giocatore)"),
# 23. (10, 10, 5, "?"),
# 24. (50, 50, 10, "?"),
# 25. (70, 70, 10, "?")

# CrazyPlayerPazzissimo vs ENEMY2
# NO DATE
#   P1 = 2+2+2+2+1+0+2+1+2+2+2+2+1+2+2+2+2+2+1+2+2 = 36
#   P2 = 2+2+2+2+3+5+2+3+2+2+2+2+3+2+2+2+2+2+3+2+2 = 49
# 2022-02-08 20:43:12.143416: CrazyPlayer vs ENEMY2
#   Local changes: clearTT(), string to bitset
#   P1 = 2+2+2+2+1+4+2+1+0+0+2+2+3+1+0+2+2+4+1+2+2 = 37
#   P2 = 2+2+2+2+3+1+2+3+5+5+2+2+1+3+5+2+2+1+3+2+2 = 52
# 2022-02-09 12:30:58.652554: CrazyPlayer vs ENEMY2
#   Local changes: findThreats
#   P1 = 2+2+2+2+2+2+2+2+2+0+2+2+2+2+2+2+2+2+2+1+2+2 = 41
#   P2 = 2+2+2+2+2+2+2+2+2+5+2+2+2+2+2+2+2+2+2+3+2+2 = 48
# 2022-02-09 18:55:09.373229: CrazyPlayer vs ENEMY2
#   Local changes: iterative deepening
#   P1 = 2+2+2+2+2+3+2+2+2+2+3+2+2+2+2+2+2+2+1+2+1+5+1 = 48
#   P2 = 2+2+2+2+2+1+2+2+2+2+1+2+2+2+2+2+2+2+3+2+3+0+3 = 45
# 2022-02-10 18:20:37.800037: CrazyPlayer vs ENEMY2
#   Local changes: Refactor, iterative deepening && max depth 10, findThreats complete no jump, change isCellInBounds con getClosedCells
#   P1 = 2+2+2+2+2+3+2+2+2+2+4+2+2+2+2+5+2+2+3+2+2+5+2 = 56
#   P2 = 2+2+2+2+2+1+2+2+2+2+1+2+2+2+2+0+2+2+1+2+2+0+2 = 39
# 2022-02-11 11:54:11.226947: CrazyPlayer vs ENEMY2
#   Local changes: default
#   P1 = 2+2+2+2+2+3+2+2+2+2+4+2+3+2+2+5+2+3+3+2+2+5+4 =
#   P2 = 2+2+2+2+2+1+2+2+2+2+1+2+1+2+2+0+2+1+1+2+2+0+1






# ENEMY1 vs ENEMY2
# NO DATE
# P1 = 1+2+2+2+2+0+2+1+2+5+2+2+3+5+2+5+2+2+2+2+2 = 48
# P2 = 4+2+2+2+2+5+2+4+2+0+2+2+3+0+2+0+2+2+2+2+2 = 44
# 2022-02-09 22:09:30.072365: ENEMY1 vs ENEMY2
#   P1 = 4+2+2+2+2+5+2+2+0+0+2+2+1+0+2+0+2+2+1+2+2+0+0+0+0 = 37
#   P2 = 1+2+2+2+2+0+2+2+5+5+2+2+4+5+2+5+2+2+3+2+2+5+5+5+5 = 74

# CrazyPlayerPazzissimo vs ENEMY1
# NO DATE
#   P1 = 2+2+2+2+2+2+2+2+3+2+2+2+3+3+2+2+2+2+2+2+2 = 45
#   P2 = 2+2+2+2+2+2+2+2+1+2+2+2+1+1+2+2+2+2+2+2+2 = 39
# 2022-02-08 11:21:10.925409: CrazyPlayer vs ENEMY1
#   P1 = 2+2+2+2+2+2+2+1+3+5+2+2+5+2+2+5+2+2+2+2+2 = 51
#   P2 = 2+2+2+2+2+2+2+4+1+0+2+2+0+2+2+0+2+2+2+2+2 = 37
# 2022-02-08 20:42:55.120580: CrazyPlayer vs ENEMY1
#   Local changes: clearTT(), string to bitset
#   P1 = 2+2+2+2+2+2+2+1+3+5+2+2+5+2+2+5+2+2+2+2+2 = 51
#   P2 = 2+2+2+2+2+2+2+4+1+0+2+2+0+2+2+0+2+2+2+2+2 = 37
# 2022-02-09 12:32:58.749867: CrazyPlayer vs ENEMY1
#   Local changes: findThreats
#   P1 = 2+2+2+2+2+2+2+3+3+5+2+2+5+5+5+5+2+2+2+1+2+5 = 63
#   P2 = 2+2+2+2+2+2+2+1+1+0+2+2+0+0+0+0+2+2+2+3+2+0 = 28
# 2022-02-09 18:34:33.403414: CrazyPlayer vs ENEMY1 (23 match)
#   Local changes: iterative deepening
#   P1 = 2+2+2+2+3+4+2+2+5+5+2+2+5+5+5+5+2+5+2+2+2+5+5 = 76
#   P2 = 2+2+2+2+1+1+2+2+0+0+2+2+0+0+0+0+2+0+2+2+2+0+0 = 26
# 2022-02-10 17:53:07.422641: CrazyPlayer vs ENEMY1
#   Local changes: Refactor, iterative deepening, findThreats complete no jump, change isCellInBounds con getClosedCells
#   P1 = 2+2+2+2+2+2+2+2+5+5+2+2+5+2+5+5+2+2+2+2+2+5+5 = 67
#   P2 = 2+2+2+2+2+2+2+2+0+0+2+2+0+2+0+0+2+2+2+2+2+0+0 = 32
# 2022-02-10 18:04:53.610968: CrazyPlayer vs ENEMY1
#   Local changes: Refactor, max depth 5, findThreats complete no jump, change isCellInBounds con getClosedCells
#   P1 = 2+2+2+2+2+2+2+2+5+5+2+2+5+2+5+5+2+2+2+2+2+5+5 = 67
#   P2 = 2+2+2+2+2+2+2+2+0+0+2+2+0+2+0+0+2+2+2+2+2+0+0 = 32
# 2022-02-10 19:59:22.786467: CrazyPlayer vs ENEMY1
#   Local changes: Refactor, iterative deepening && max depth 10, findThreats complete no jump, change isCellInBounds con getClosedCells
#   P1 = 2+2+2+2+3+3+2+3+5+5+3+2+5+5+5+5+2+3+2+2+2+5+2 = 72
#   P1 = 2+2+2+2+1+1+2+1+0+0+1+2+0+0+0+0+2+1+2+2+2+0+2 = 27
# 2022-02-11 10:36:24.036138: CrazyPlayer vs ENEMY1
#   Local Changes: findThreats block type e clearTT (default)
#   P1 = 2+2+2+2+3+3+2+5+5+5+2+2+5+5+5+5+2+4+5+2+2+5+3+5+5 = 88
#   P2 = 2+2+2+2+1+1+2+0+0+0+2+2+0+0+0+0+2+1+0+2+2+0+1+0+0 = 24








