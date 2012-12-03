package org.jboss.ee6lab.cdi.wumpus;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.ee6lab.cdi.wumpus.xmlbeans.Room;

@Model
public class RoomAction {
		
	@Inject
	CurrentPlayerManager currentPlayerManager;
	
	@Inject
	@Current
	Instance<Room> currentRoomInstance;
	
	private void moveTo(Room room) {
		currentPlayerManager.moveTo(room);
	}
	
	private void shootAt(Room room) {
		currentPlayerManager.shootAt(room);
	}

	@IsAlive
	public void north() {
		moveTo(currentRoomInstance.get().getNorth());
	}
	
	@IsAlive
	public void south() {
		moveTo(currentRoomInstance.get().getSouth());
	}
	
	@IsAlive
	public void east() {
		moveTo(currentRoomInstance.get().getEast());
	}
	
	@IsAlive
	public void west() {
		moveTo(currentRoomInstance.get().getWest());
	}
	
	@IsAlive
	public void shootNorth() {
		shootAt(currentRoomInstance.get().getNorth());
	}
	
	@IsAlive
	public void shootSouth() {
		shootAt(currentRoomInstance.get().getSouth());
	}
	
	@IsAlive
	public void shootEast() {
		shootAt(currentRoomInstance.get().getEast());
	}

	@IsAlive
	public void shootWest() {
		shootAt(currentRoomInstance.get().getWest());
	}
}
