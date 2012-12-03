package org.jboss.ee6lab.cdi.wumpus;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.ee6lab.cdi.wumpus.scope.CurrentGameId;
import org.jboss.ee6lab.cdi.wumpus.scope.GamesManager;
import org.jboss.ee6lab.cdi.wumpus.xmlbeans.Room;

@SessionScoped
public class CurrentPlayerManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int currentGameId = 0;
	
	@Inject
	private Player currentPlayer;
	
	@Inject
	@Random
	private Instance<Room> initialRoom;
	
	@Inject
	Event<PlayerLogoffEvent> playerLogoffEvent;
	
	@Inject
	Event<PlayerLoginEvent> playerLoginEvent;
	
	@Inject
	Event<PlayerJoinedGameEvent> playerJoinedGameEvent;
	
	@Inject
	Event<PlayerLeftGameEvent> playerLeftGameEvent;
	
	@Inject
	Event<PlayerEnteredRoomEvent> playerEnteredRoomEvent;
	
	@Inject
	GamesManager gamesManager;
	
	private Room currentRoom;
	
	@Produces
	@CurrentGameId
	public Integer getCurrentGameId() {
		return currentGameId;
	}
	
	@Produces
	@Current
	@Named
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	@Inject
	GameMessage gameMessage;
	
	public void joinGame(int gameId) {
		currentGameId = gameId;
		currentPlayer.setShot(false);
		moveTo(initialRoom.get());
		playerJoinedGameEvent.fire(new PlayerJoinedGameEvent());
	}
	
	public void leaveGame() {
		playerLeftGameEvent.fire(new PlayerLeftGameEvent());
		
		currentGameId = 0;
		currentRoom = null;
	}
	
	public void moveTo(Room room) {
		if (room != null) {
			if (currentRoom != null) {
				currentRoom.removePlayer(currentPlayer);
			}
			
			currentRoom = room;
			room.addPlayer(currentPlayer);
	
			playerEnteredRoomEvent.fire(new PlayerEnteredRoomEvent());
		}
	}
	
	public void shootAt(Room room) {
		if (room.shootAt()) {
			gameMessage.add("Your arrow hit a target!");
		}
		else {
			gameMessage.add("Nothing happens...");
		}
	}

	@Produces
	@Current
	@Named
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	public void login() {
		playerLoginEvent.fire(new PlayerLoginEvent());
		currentPlayer.setLoggedIn(true);
	}
	
	@PreDestroy
	public void preDestroy() {
		playerLeftGameEvent.fire(new PlayerLeftGameEvent());
		playerLogoffEvent.fire(new PlayerLogoffEvent());
		System.out.println("XXX predestroy on " + currentPlayer.getName());
	}
}
