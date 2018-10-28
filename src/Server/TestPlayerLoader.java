package Server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestPlayerLoader {

	@Test
	void test() throws NoSuchPlayerException, CorruptFileException {
		Player sean = PlayerLoader.loadPlayer("sean", null);
		assertEquals("Sean", sean.getName());
		assertEquals(10, sean.getPosition().getX());
		assertEquals(24, sean.getPosition().getY());
	}

}
