

# Introduction #

Game may still be very buggy, haven't had a chance to test multiplayer much at all.  Feel free to post comments or feedback at page bottom.

Current game link is at
http://www.kongregate.com/games/garcia1968/bled2zed

Using an account with a limit of 100 concurrent players.  10 player max per room.

Just install the browser unity plug-in if not already installed, then the game should appear after a bit.  Click 'go' to create room or join an existing room.  Can name/create your own room.  Rooms stay up as long as at least 1 player present.

If you wish to start a **private** room, create the name of the game with the word 'private' included, like 'privateJoe' - that game won't show up in the public listing, but will still be accessible to those who know the game name.

**The goal for the current version of the game is to try and get as many zombie kills as possible(solo or co-op) in a 12 minute(720 second) round.  If you get > 2 deaths, your score is reset to zero.**

**An earlier game goal for multiplayer was to try and get the highest score by staying alive longest the most often.**  You get no score for your initial 30 seconds of time alive, but each 30 seconds after that will accumulate points, with each successive 30 seconds alive acting as a score multiplier(so seconds 30-60 = +x points, 60-90 = +2x points, ...)  The 'round' time is currently 720 seconds(12 minutes) at the end of which the scores are reset to zero.

During the round, the lead scorer will have a small red sphere placed high approximately overhead of where they are - this is to prevent someone from hiding/camping in one spot the whole game.

You'll start out in the middle of the map, next to a large tiled tower where zombies are spawned out every 5 seconds to a max of 15 zombies.  The zombies have a radius of player detection - it's smaller when you're still and wider when you're walking/running.

To run, hold left or right shift, but you can only run in small bursts before being forced back to a walk.

Movement is 'WASD' or arrow keys.  Mouse to look.  Space to jump.

**Press 'F' to fire, although accuracy is best in first person view(press 'C' to toggle between first and third person view).**  There is a small red dot screen center which represents your aim(less accurate in third person view).  **Press 'R' to reload**.

Weapons
  * (1) pistol
  * (2) shotgun
  * (3) sniper rifle
  * (4) rocket
  * (G) grenade - hold key down for throw distance - max 3 seconds
  * (T) landmine

Other keys
  * (V) binoculars


---

Your status readout(updated every 5 seconds) is upper right corner:

  * **zed** - number of zed/enemy on level
  * **timeRound** - seconds left in current round
  * **score** - a multiplier of time, the longer you stay alive the higher the multiplier
  * **timeAlive** - seconds since last death
  * **health**
    * start at 100
    * below zero and you're dead
    * if you get hit by zombie or player bullet you'll start to bleed out indicated by '(-)' next to your health score, more hits increases the bleed rate
  * **bandages**
    * **press 'B' to bandage**, but you have to stay in the same spot for 6 seconds and not fire your gun - otherwise the bandage doesn't 'take'
    * max 3 bandages
  * weapon - current weapon
  * **ammo(reload count):total count**
    * on new life always start with 24 pistol bullets, max  pistol bullets is 24
    * pistol reload count is 6 - takes 2 seconds to reload - short delay between firing rounds

---

  * **Zombies**
    * takes about 5 shots to kill
    * zombies alert nearby zombies when they are chasing you
    * when a gun fires, zombies within a large radius will temporarily become more alert move toward the gunfire location
    * respawn towards a level maximum of 6 zombies total

There are 5 fixed player spawn points on the map indicated by a metal tile.  There's one on top of a building with a plank laid to the building side to go up and down - if you drop off a building you will die from the fall.  If you run off the edge of the map, you will die from the fall.


---

# **Pickups** - Ammo,Bandages,Health #

There are 6 pickup spawns on the map indicated by a white tile.  The pickup types(small box/squares) for ammo:
  * yellow = pistol
  * red = shotgun
  * blue = sniper rifle
  * rocket = purple
  * green = grenade
  * black = landmine

Also capsules for bandages(white, 1 bandage) and health(red,30 point health).  A pickup spawns at a random location every 15 seconds - higher chances for ammo.

Pickup locations are circled in red on the below map.
![http://sqlitebot.googlecode.com/files/map.jpg](http://sqlitebot.googlecode.com/files/map.jpg)


---

# Weapon stats #

weaponDistMaxHit = effective range of the weapon <br />
weaponDamage = damage per shot <br />
weaponForceMult = impact force multiplier on kill <br />

```
		//pistol
		weaponName[1] = "pistol";
		weaponAmmoMax[1] = 24;
		weaponAmmoStart[1] = 24;
		weaponReloadMax[1] = 6;
	
		weaponWaitFire[1] = 0.5f;
		weaponDistMaxHit[1] = 100.0f;	
		weaponDamage[1] = 21;	
		weaponForceMult[1] = 1.0f;
		
		//shotgun
		weaponName[2] = "shotgun";
		weaponAmmoMax[2] = 16;
		weaponAmmoStart[2] = 4;
		weaponReloadMax[2] = 2;
	
		weaponWaitFire[2] = 2.0f;
		weaponDistMaxHit[2] = 10.0f;	
		weaponDamage[2] = 105;	
		weaponForceMult[2] = 4.0f;		
		
		//sniper
		weaponName[6] = "sniper rifle";
		weaponAmmoMax[6] = 16;
		weaponAmmoStart[6] = 4;
		weaponReloadMax[6] = 2;
	
		weaponWaitFire[6] = 2.5f;
		weaponDistMaxHit[6] = 600.0f;	
		weaponDamage[6] = 55;	
		weaponForceMult[6] = 2.0f;
```


---

# Videos/Images #

<a href='http://www.youtube.com/watch?feature=player_embedded&v=_-HiQQhdmBw' target='_blank'><img src='http://img.youtube.com/vi/_-HiQQhdmBw/0.jpg' width='425' height=344 /></a>

![http://sqlitebot.googlecode.com/files/show4.jpg](http://sqlitebot.googlecode.com/files/show4.jpg)
![http://sqlitebot.googlecode.com/files/show3.jpg](http://sqlitebot.googlecode.com/files/show3.jpg)


---

# Suggested Fixes/Features #

  * better player model with the usual combat and prone animations
  * tin cans to toss to distract zombies away from self or towards other players
