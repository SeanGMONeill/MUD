package Server;


public class Position {
	
	public static enum Direction { NORTH, SOUTH, EAST, WEST }
	
	int x;
	int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setPos(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void move(Direction direction, int distance) {
		switch(direction) {
		case NORTH:
			setY(getY() + distance);
			break;
		case SOUTH:
			setY(getY() - distance);
			break;
		case EAST:
			setX(getX() + distance);
			break;
		case WEST:
			setX(getX() - distance);
			break;
		}
	}
	
	public void move(Direction direction) {
		move(direction, 1);
	}

	/*
	 * @return a copy of this object
	 */
	public Position copy() {
		return new Position(this.x, this.y);
	}
}
