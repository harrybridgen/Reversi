package reversi;

public class ReversiController implements IController {
	IModel model;
	IView view;

	@Override
	public void initialise(IModel model, IView view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void startup() {
		model.setPlayer(1);
		model.setFinished(false);
		for (int x = 0 ; x < model.getBoardWidth(); x++) {
			for (int y = 0; y < model.getBoardHeight(); y++) {
				if ((x == 3 && y == 3) || (x == 4 && y == 4)) {
					model.setBoardContents(x, y, 1);
				}
				else if ((x == 3 && y == 4) || (x == 4 && y == 3)) {
					model.setBoardContents(x, y, 2);
				}
				else {
					model.setBoardContents(x, y, 0);
				}
			}
		}
		view.feedbackToUser(1, "White player – choose where to put your piece");
		view.feedbackToUser(2, "Black player – not your turn");
		view.refreshView();
	}

	@Override
	public void squareSelected(int player, int x, int y) {
		if (model.hasFinished() || noPiecesTaken(x, y, player) || pieceAt(x, y)){
			return;
		}
		if (!isCurrentPlayer(player)){
			view.feedbackToUser(player, "It is not your turn!");
			return;
		}
		doMove(player, x, y);
	}

	@Override
	public void doAutomatedMove(int player) {
		int besttaken = 0;
		int bestx = 0;
		int besty = 0;
		if (model.hasFinished()){
			return;
		}
		if (!isCurrentPlayer(player)) {
			view.feedbackToUser(player, "It is not your turn!");
			return;
		}
		if (isCurrentPlayer(player)) {
			for (int x = 0 ; x < model.getBoardWidth() ; x++) {
				for (int y = 0; y < model.getBoardHeight(); y++) {
					if (pieceAt(x, y) || noPiecesTaken(x, y, player)) {
						continue;
					}
					int piecesTaken = piecesTaken(player, x, y, false);
					if (piecesTaken > besttaken) {
						besttaken = piecesTaken;
						bestx = x;
						besty = y;
					}
				}
			}
			if (besttaken == 0){
				update();
			}
			else {
				doMove(player, bestx, besty);
			}
		}
	}

	@Override
	public void update() {
		if (isCurrentPlayer(2)){
			view.feedbackToUser(1, "White player – not your turn");
			view.feedbackToUser(2, "Black player – choose where to put your piece");
		}
		else if (isCurrentPlayer(1)){
			view.feedbackToUser(1, "White player – choose where to put your piece");
			view.feedbackToUser(2, "Black player – not your turn");
		}
		if (noValidMove(model.getPlayer())){
			if (isCurrentPlayer(1)) {
				model.setPlayer(2);
				view.feedbackToUser(1, "White player – no available moves, turn passed to black player");
				view.feedbackToUser(2, "Black player – choose where to put your piece");
			}
			else {
				model.setPlayer(1);
				view.feedbackToUser(1, "White player – choose where to put your piece");
				view.feedbackToUser(2, "Black player – no available moves, turn passed to white player");
			}
		}
		if (noValidMove(1) && noValidMove(2)){
			model.setFinished(true);
		}
		if (model.hasFinished()) {
			int p1pieces = countPieces(1);
			int p2pieces = countPieces(2);
			if (p1pieces > p2pieces) {
				view.feedbackToUser(1, "White won. White " + p1pieces + " to Black " + p2pieces + ". Reset game to replay.");
				view.feedbackToUser(2, "White won. White " + p1pieces + " to Black " + p2pieces + ". Reset game to replay.");
			}
			else if (p1pieces < p2pieces) {
				view.feedbackToUser(1, "Black won. Black " + p2pieces + " to White " + p1pieces + ". Reset game to replay.");
				view.feedbackToUser(2, "Black won. Black " + p2pieces + " to White " + p1pieces + ". Reset game to replay.");
			}
			else {
				view.feedbackToUser(1, "Draw. Both players ended with " + p1pieces + " pieces. Reset game to replay.");
				view.feedbackToUser(2, "Draw. Both players ended with " + p1pieces + " pieces. Reset game to replay.");
			}
			view.refreshView();
		}
	}

	public void doMove(int player, int x, int y) {
		piecesTaken(player, x, y, true);
		model.setBoardContents(x, y, player);
		view.refreshView();
		if (isCurrentPlayer(1)){
			model.setPlayer(2);
		}
		else {
			model.setPlayer(1);
		}
		update();
	}

	public boolean noValidMove(int player) {
		boolean noValidMove = true;
		for (int x = 0; x < model.getBoardWidth(); x++) {
			for (int y = 0; y < model.getBoardHeight(); y++) {
				if (pieceAt(x, y)) {
					continue;
				}
				if (noPiecesTaken(x, y, player)) {
					continue;

				}
				noValidMove = false;
			}
		}
		return noValidMove;
	}

	public int countPieces(int player){
		int pieces = 0;
		for (int x = 0; x < model.getBoardWidth(); x++){
			for (int y = 0; y < model.getBoardHeight(); y++){
				if (playerPieceAt(x, y, player)) {
					pieces++;
				}
			}
		} return pieces;
	}

	public int piecesTaken(int player, int x, int y, boolean editBoard){
		int pieces = 0;
		for (int i = -1; i <= 1; i++){
			for (int j = -1; j <= 1; j++){
				if (i == 0 && j == 0){
					continue;
				}
				int opponentPieces = 0;
				int dx = x + i;
				int dy = y + j;
				while (isValidCoordinate(dx,dy) && pieceAt(dx,dy)){
					if (opponentPieceAt(dx, dy, player)){
						opponentPieces++;
						dx = dx + i;
						dy = dy + j;
					}
					else if (playerPieceAt(dx, dy, player)){
						pieces = pieces + opponentPieces;
						if (editBoard){
							for (int k = 0; k < opponentPieces; k++){
								dx = dx - i;
								dy = dy - j;
								model.setBoardContents(dx, dy, player);
							}
						}
						break;
					}
				}
			}
		}
		return pieces;
	}
	public boolean isValidCoordinate(int x, int y) {
		return (x >= 0 && x < model.getBoardWidth() && y >= 0 && y < model.getBoardHeight());
	}
	public boolean playerPieceAt(int x, int y, int player) {
		return (model.getBoardContents(x, y) == player);
	}
	public boolean opponentPieceAt(int x, int y, int player) {
		int opponent = (player == 1) ? 2 : 1;
		return (model.getBoardContents(x, y) == opponent);
	}
	public boolean pieceAt(int x, int y) {
		return (!(model.getBoardContents(x, y) == 0));
	}
	public boolean noPiecesTaken(int x, int y, int player) {
		return piecesTaken(player, x, y, false) == 0;
	}
	public boolean isCurrentPlayer(int player) {
		return model.getPlayer() == player;
	}
}