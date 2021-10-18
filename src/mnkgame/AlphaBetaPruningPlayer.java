package mnkgame;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private MNKBoard board;
	private MNKGameState myWin, yourWin;
	
	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		board = new MNKBoard(M, N, K);
		myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
	}
	
	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		return FC[0];
	}

	// vedere depth
	/*
	ALPHABETA(Tree T, bool mynode, int depth, float a, float b)→ float
		if(depth = 0 or T.isLeaf())											Controllo FC vuote
			return EVALUATE(T)
		elif(mynode = true)
			eval ← ∞
			for(Tree c ∈ T.children())										Iter su FC (priorità vittoria mia)
				eval ← Min(eval,ALPHABETA(c,false,depth-1, a, b))
				b ← Min(eval, b)
				if(b ≤ a) // a cutoff
					break
				endif
			endfor
			return eval
		else
			eval ← -∞
			for(Tree c ∈ T.children())										Iter su FC (priorità vittoria sua)
				eval ← Max(eval,ALPHABETA(c,true,depth-1, a, b))
				a ← Max(eval, a)
				if(b ≤ a) // b cutoff
					break
				endif
			endfor
			return eval
		endif
	*/
	private void alphabeta (MNKBoard board, boolean myTurn, float a, float b) {
	}

	private float evaluate(MNKBoard board) {
		return 0.0F;
	}

	@Override
	public String playerName() {
		return "AlphaBetaPruningPlayer";
	}
}
