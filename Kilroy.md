If you find the below info or code useful or have comments/questions, feel free to post a comment below or send email with 'kilroy' in subject line to jeremy.cothran@gmail.com

see also [phibian](http://code.google.com/p/sqlitebot/wiki/phibian) for [map analysis scripts](http://code.google.com/p/sqlitebot/wiki/phibian#Map_analysis), [forum post](http://forums.aigamedev.com/showthread.php?5813-example-map-analysis-and-pathing) and [ideas](http://code.google.com/p/sqlitebot/wiki/phibian#expected_AI_client_development)



# Source & Description #

commander code at

version 1 - November 28, 2012
http://code.google.com/p/sqlitebot/source/browse/trunk/aigamedev_ctf/kilroy.py

version 14 - December 10, 2012
http://code.google.com/p/sqlitebot/source/browse/trunk/aigamedev_ctf/kilroy14.py


---

# Version 1 #

http://code.google.com/p/sqlitebot/source/browse/trunk/aigamedev_ctf/kilroy.py

This 'kilroy' bot/commander is a simple first attempt at something past the example commanders given in examples.py

Did a competition between all the example bots and 'Balanced' came out on top by far, probably because of the advanced goal flanking behavior.

Started with Defender commander and added some adaptations to help it track visible enemies and look towards killed teammates to help make it a better contender, then added Balanced commanders flanking method to the currently single attacker on the team and the flanking allows it to come out ahead on scores.

## Noted errors - version 1 ##

This initial version has some errors in it - I haven't been posting my latest development and fixes.


---

On map30 the flag must be delivered in a different place than where it is taken from, so there is flagScoreLocation and a different location where the flag spawns. Thus Kilroy will defend the wrong place.
The reason for this is line 156:
> targetPosition = self.game.team.flagScoreLocation
therefor using either
> targetPosition = self.game.team.flagSpawnLocation
or even
> targetPosition = self.game.team.flag.position
helps, although the flag.position means the bots will not defend the "base" but will stick around a dropped flag.

---


On line 76, the combatEvents list is only checked for the last element = [-1], there's probably a subtle error here that if more than one event happens per tick, events other than the last won't be processed, so modify this into a for loop to process all elements after the last processed index element.


---

# Version 14 #

http://code.google.com/p/sqlitebot/source/browse/trunk/aigamedev_ctf/kilroy14.py

The main changes with this bot are
  * using a dictionary (hash in perl terminology) associated with each bot by the key of bot.name for associated roles, state and properties
  * adding a simple 'evade' behavior so runners(att) retreat/attack from moving enemies that see them
  * sorting runner(att) distances so that runners set themselves up between camping the flagspawn and returning the flag
  * adding a 'bestDefend' function to help a bot only defend angles of interest when near outer map walls and corners

Note:
  * commented using keyword 'FIX' to denote lines/sections that may need corrections
  * first part of the code is used to setup some later checks, initial sections of the code check the bot 'role' while later parts make no role check and apply to the bot in sequence of execution
  * the bot 'state' check is not uniformly applied yet, just to the active roles currently in needing better coordination of 'state'
  * the 'botTrack' property was something I was experimenting with but decided at this point not to use

Still haven't gotten into any interesting map analysis yet - would really like the API to support line-of-sight and pathing queries and pathing influence via something like a heat/weight map so I don't have to work those parts up myself.


---

# Sharing development #

I'm interested to see what an open team-based approach can do in regards to this contest AI and ongoing - particularly if/when a version of the contest might be adapted to other gametypes like deathmatch or include human players.

If you have improvements you want to incorporate and share, I'm interested to incorporate and promote where competitions.py results show a marked improvement or increase in the official rankings.  Feel free to copy and branch the code as needed and can discuss merging as needed.  Will post a credits wiki page, highlighting any contributions.


---

# Development ideas #

lines in bold are next priority for development and testing


---

breakout team into squads with command board - offense, defense, scramble

**==individual bot - additional structure variables**

role - attack, defend, scramble

timeLastCommand - time last command for cooldowns

targetTrack - name of enemy bot tracking

nested dictionary = perl hash?
http://www.devshed.com/c/a/Python/The-Dictionary-Python-Object-Type/2/


---


pair or more offense to break through lines - first might die, but second avenges and breaks through

ambush on choke points for defense, offense

use cover for movement, facing, heatmap

use offense to scout - can see direction and use calculated enemy los or predicted movement+los to pathfind where not to be found or distract enemy in needed direction for traps or to get flag

use knowledge of deaths/respawn, team count to change tactics where man advantage

**simple move-behind - track enemy distance/angle vs self to move out of viewing angle or behind - in paired coordinated movement with enemy reaction, kill enemy(s) not facing between pair**

  * scrambling bots could ‘charge’ down attacking bots/runners from cover/behind


---

**4 defenders - default 4 direction facing for open flag?**

**assign nearest defending individual to tracking closest enemy threat - one defense per enemy unit, move defend facing angle if current facing greater than some x(radians? degrees?)**

if lose attacker by wall or distance while tracking - start longer cooldown wait to defense reset


---


use los calculations to specify where to position(cover) and **not** to look/scan on defense

on dropped flag(killed flag runner) - re-evaluate goal command
> double-check to see flanking goal is flag location and not flagbase for dropped flag

**move flag runners towards less traffic areas (e.g. edges of map, longer paths) - possible use of map analysis, heatmaps**

when runners killed mark high-level grid area and avoid area if consistently killed

runners try random setup locations around enemy flag(direction/radius) to probe weaknesses, repeat/avoid based on success/failure