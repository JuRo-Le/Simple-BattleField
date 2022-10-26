package battleship;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Write your code here
		GameBoard gameBoard = new GameBoard();
		gameBoard.initPlayers();
		gameBoard.prepareShips();
		gameBoard.startWar();
	}
}

class Player {
	public Player(String playerName) {
		this.playerName = playerName;
		this.shipBoard = Tools.initTheFogOfWar();
		this.targetShipBoard = Tools.initTheFogOfWar();
		this.ships = new Ship[DefineShip.values().length];
	}

	private final String playerName;
	private final String[][] shipBoard;
	private final String[][] targetShipBoard;
	private final Ship[] ships;
	private int shipCells;
	private int hitCounter;
	private Ship hitShip;

	public String[][] getShipBoard() {
		return shipBoard;
	}

	public String[][] getTargetShipBoard() {
		return targetShipBoard;
	}

	public int getShipCells() {
		return shipCells;
	}

	public int getHitCounter() {
		return hitCounter;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void updateShipBoard(Ship ship) {
		for (int i = 0; i < ship.cells(); i++) {
			updateShipBoard(ship.location()[i].rowNum(), ship.location()[i].colNum(), ship.denotes()[i].toString());
		}
	}

	private void updateShipBoard(int row, int col, String symbol) {
		shipBoard[row][col] = symbol;
	}

	public void updateTargetShipBoard(int row, int col, String symbol) {
		targetShipBoard[row][col] = symbol;
	}

	public void countThisShipCells(int cells) {
		shipCells += cells;
	}

	public void collectPlayerShips(int shipIndex, Ship nextShip) {
		ships[shipIndex] = nextShip;
	}

	public boolean hitAShoot(int[] shipLocation) {
		boolean hasHitANewShip = ShipSymbol.O.name().equals(shipBoard[shipLocation[0]][shipLocation[1]]);
		boolean isThatOldShip = ShipSymbol.X.name().equals(shipBoard[shipLocation[0]][shipLocation[1]]);
		updateShipBoard(shipLocation[0], shipLocation[1],
				hasHitANewShip || isThatOldShip ? ShipSymbol.X.name() : ShipSymbol.M.name());
		if (hasHitANewShip) {
			updatePlayerShip(shipLocation);
			hitCounter++;
		}
		return hasHitANewShip || isThatOldShip;
	}

	public void updatePlayerShip(int[] shipLocation) {
		int j = 0;
		out:
		for (Ship ship : ships) {
			for (Location location : ship.location()) { // each location has one ship symbol
				if (location.rowNum() == shipLocation[0] && location.colNum() == shipLocation[1]
						&& ship.denotes()[j] == ShipSymbol.O) {
					ship.denotes()[j] = ShipSymbol.X;
					hitShip = ship;
					break out;
				}
				j++;
			}
			j = 0;
		}
	}
	
	public boolean isShipSink() {
		for (int i = 0; i < hitShip.denotes().length; i++) {
			if (hitShip.denotes()[i] == ShipSymbol.O) {
				return false;
			}
		}
		return true;
	}
}

class Tools {
	public static final String FOG = "~";
	private static final String COL_HEADER = "  1 2 3 4 5 6 7 8 9 10";
	private static final String C_SPACE = " ";

	public static String[][] initTheFogOfWar() {
		return new String[][] { { FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG },
				{ FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG, FOG } };
	}

	public static void showFields(String[][] gameFields) {
		char rowHeader = 'A';
		System.out.println(COL_HEADER);
		for (String[] gameField : gameFields) {
			System.out.print(rowHeader++);
			for (String shipOrFog : gameField) {
				System.out.print(C_SPACE + shipOrFog);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

	public static Location[] buildShipLocation(int length, int[] beginEnd) {
		// 0 1 0 5 -> 01 02 03 04 05
		// 0 1 5 1 -> 01 11 21 31 41 51
		int i0 = beginEnd[0];
		int i1 = beginEnd[1];
		int i2 = beginEnd[2];
		int i3 = beginEnd[3];
		Location[] locations = new Location[length];
		for (int i = 0; i < length; i++) {
			if (beginEnd[0] == beginEnd[2] && beginEnd[1] < beginEnd[3]) {
				locations[i] = new Location(i0, i1++);
			} else if (beginEnd[0] == beginEnd[2] && beginEnd[1] > beginEnd[3]) {
				locations[i] = new Location(i0, i3++);
			} else if (beginEnd[1] == beginEnd[3] && beginEnd[0] < beginEnd[2]) {
				locations[i] = new Location(i0++, i1);
			} else if (beginEnd[1] == beginEnd[3] && beginEnd[0] > beginEnd[2]) {
				locations[i] = new Location(i2++, i1);
			}
		}
		return locations;
	}

	public static ShipSymbol[] buildShipSymbols(int size) {
		ShipSymbol[] s = new ShipSymbol[size];
		for (int i = 0; i < size; i++) {
			s[i] = ShipSymbol.O;
		}
		return s;
	}

	public static int[] getInput(String coordinate) {
		try {
			// split("\\s+")
			int space = coordinate.indexOf(C_SPACE);
			int c1 = convertTo(coordinate.charAt(0));
			int c2 = Integer.parseInt(coordinate.substring(1, space)) - 1;
			int c3 = convertTo(coordinate.substring(space + 1, space + 2).charAt(0));
			int c4 = Integer.parseInt(coordinate.substring(space + 2)) - 1;
			return new int[] { c1, c2, c3, c4 };
		} catch (NumberFormatException e) {
			// un-define 1A 5A
			System.out.println(e.getMessage());
			return new int[] {};
		}
	}

	private static int convertTo(char c) {
		int i = -1;
		switch (c) {
		case 'A' -> i = 0;
		case 'B' -> i = 1;
		case 'C' -> i = 2;
		case 'D' -> i = 3;
		case 'E' -> i = 4;
		case 'F' -> i = 5;
		case 'G' -> i = 6;
		case 'H' -> i = 7;
		case 'I' -> i = 8;
		case 'J' -> i = 9;
		}
		return i;
	}

	public static int[] getShootLocation(String choose) {
		// choose.replaceAll(regex,"");
		int row = Tools.convertTo(choose.charAt(0));
		int col = Integer.parseInt(choose.substring(1)) - 1;
		return new int[] { row, col };
	}
}

class GameBoard {
	private static final int PLAYERS = 2;
	private Player[] players;
	private static final Scanner sc = new Scanner(System.in);

	public void initPlayers() {
		players = new Player[PLAYERS];
		// for[0,1]
		players[0] = new Player("Player 1");
		players[1] = new Player("Player 2");
	}

	public void prepareShips() {
		int[] beginEnd;
		boolean isValidBeginEnd;
		Ship nextShip;
		Location[] locations;
		ShipSymbol[] shipSymbols;
		int shipIndex;

		for (int i = 0; i < PLAYERS; i++) {
			System.out.println(players[i].getPlayerName() + ", place your ships on the game field");
			Tools.showFields(players[i].getShipBoard());
			shipIndex = 0;
			for (DefineShip ship : DefineShip.values()) {
				System.out.println("Enter the coordinates of the " + ship.toString());
				do {
					isValidBeginEnd = false;
					beginEnd = Tools.getInput(sc.nextLine().trim().toUpperCase());
					if (beginEnd.length == 4) {
						isValidBeginEnd = checkCoordinate(players[i], ship, beginEnd);
					}
				} while (!isValidBeginEnd);

				locations = Tools.buildShipLocation(ship.getCell(), beginEnd);
				shipSymbols = Tools.buildShipSymbols(ship.getCell());
				nextShip = new Ship(ship.getCell(), locations, shipSymbols);

				players[i].updateShipBoard(nextShip);
				players[i].countThisShipCells(ship.getCell());
				players[i].collectPlayerShips(shipIndex++, nextShip);
				Tools.showFields(players[i].getShipBoard());
			}
			nextPLayer();
		}
	}

	public void startWar() {
		int index = 0;
		int turn, nextTurn;
		do {
			turn = index % 2; // 0 1
			nextTurn = turn == 0 ? 1 : 0;
			Tools.showFields(players[turn].getTargetShipBoard());
			System.out.println("---------------------");
			Tools.showFields(players[turn].getShipBoard());
			System.out.println(players[turn].getPlayerName() + ", it's your turn:");

			int[] location;
			do {
				location = Tools.getShootLocation(sc.nextLine().trim().toUpperCase());
			} while (!checkShootLocation(location));

			boolean howThatShoot = players[nextTurn].hitAShoot(location); // player 1 hit player 2 or versa
			if (howThatShoot) { // hit ship's other player or not?
				players[turn].updateTargetShipBoard(location[0], location[1], ShipSymbol.X.name());
				if (players[nextTurn].getHitCounter() == players[nextTurn].getShipCells()) {
					System.out.println("You sank the last ship. You won. Congratulations!");
					break; // here
				} else if (players[nextTurn].isShipSink()) {
					System.out.println("You sank a ship!\n");
				} else {
					System.out.println("You hit a ship!\n");
				}
			} else {
				players[turn].updateTargetShipBoard(location[0], location[1], ShipSymbol.M.name());
				System.out.println("You missed!");
			}
			index++;
			nextPLayer();
		} while (players[nextTurn].getHitCounter() < players[nextTurn].getShipCells());
	}

	private void nextPLayer() {
		System.out.println("Press Enter and pass the move to another player\n...");
		sc.nextLine();
	}

	private boolean checkShootLocation(int[] choose) {
		if (choose[0] < 0 || choose[1] < 0 || choose[1] > 9) {
			System.out.println("Error! You entered the wrong coordinates! Try again:");
			return false;
		}
		return true;
	}

	private boolean checkCoordinate(Player player, DefineShip ship, int[] coordinate) {
		int i0 = coordinate[0];
		int i1 = coordinate[1];
		int i2 = coordinate[2];
		int i3 = coordinate[3];
		// Math.abs(n);
		// Math.max(a,b)
		int size = i0 == i2 ? Math.abs(i3 - i1) + 1 : Math.abs(i2 - i0) + 1;
		// Math.()
		if (i0 < 0 || i0 > 9 || i1 < 0 || i1 > 9 || i2 < 0 || i2 > 9 || i3 < 0 || i3 > 9) {
			// un-define A-1 A-5
			return false;
		} else if (size != ship.getCell()) {
			System.out.println("Error! Wrong length of the " + ship.getShipName() + "! Try again:");
			return false;
		} else if (i0 != i2 && i1 != i3) { // mean 1 2 2 1, mean A2 B1
			System.out.println("Error! Wrong ship location! Try again:");
			return false;
		} else {
			Location[] newLocations = Tools.buildShipLocation(ship.getCell(), coordinate);
			if (checkAround(player, newLocations)) { // has any ship around?
				System.out.println("Error! You placed it too close to another one. Try again:");
				return false;
			}
		}
		return true;
	}

	private boolean checkAround(Player player, Location[] newLocations) {
		int ySub1, xSub1, yAdd1, xAdd1, row, col;
		for (Location l : newLocations) {
			row = l.rowNum();
			col = l.colNum();
			ySub1 = Math.max(row - 1, 0);
			yAdd1 = Math.min(row + 1, 9);
			xSub1 = Math.max(col - 1, 0);
			xAdd1 = Math.min(col + 1, 9);
			// max are 8 fog around 1 point + itself check
			if (!Tools.FOG.equals(player.getShipBoard()[ySub1][xSub1])
					|| !Tools.FOG.equals(player.getShipBoard()[row][xSub1])
					|| !Tools.FOG.equals(player.getShipBoard()[yAdd1][xSub1])
					|| !Tools.FOG.equals(player.getShipBoard()[ySub1][col])
					|| !Tools.FOG.equals(player.getShipBoard()[yAdd1][col])
					|| !Tools.FOG.equals(player.getShipBoard()[ySub1][xAdd1])
					|| !Tools.FOG.equals(player.getShipBoard()[row][xAdd1])
					|| !Tools.FOG.equals(player.getShipBoard()[yAdd1][xAdd1])
					|| !Tools.FOG.equals(player.getShipBoard()[row][col])) {
				return true;
			}
		}
		return false;
	}
}

enum DefineShip {

	AC("Aircraft Carrier", 5)
	, B("Battleship", 4)
	, S("Submarine", 3)
	, C("Cruiser", 3)
	, D("Destroyer", 2);

	private final String shipName;
	private final int cell;

	DefineShip(String shipName, int cell) {
		this.cell = cell;
		this.shipName = shipName;
	}

	public String getShipName() {
		return shipName;
	}

	public int getCell() {
		return cell;
	}

	@Override
	public String toString() {
		return this.getShipName() + " (" + this.getCell() + " cells)";
	}
}

enum ShipSymbol {
	O, X, M
}

record Ship(int cells, Location[] location, ShipSymbol[] denotes) {
}

record Location(int rowNum, int colNum) {
}