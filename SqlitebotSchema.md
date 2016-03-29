# Diagram #

![http://sqlitebot.googlecode.com/files/sqlitebot_schema.jpg](http://sqlitebot.googlecode.com/files/sqlitebot_schema.jpg)

**Table of Contents**


# Schema/Table description #

All tables use a simple autocount **row\_id** as the primary key reference.  Each row also has a **row\_entry\_date** which is the system/real-world time.

Some table may also include a **gametime** which is the internal game/tick time and **map\_level** which is the identifier for the current map being played.


---

## bot ##
  * name - name of the bot
  * skill - skill level of the bot (1-7)
  * skin - character skin for the bot
  * team - team number for bot (0-255)
  * available - is bot available for play? 0=no(in play), 1=yes
  * location - current bot location(x,y,z) on map (updated every 3 seconds by refreshInfo function)
  * status - bot status code (attacking,defending,etc)


---

## unit\_to\_squad ##
  * unit\_name - name of unit(bot/player)
  * bot - 0=unit is not a bot, 1=unit is a bot
  * squad\_name - name of squad bot belongs to


---

## squad ##
  * squad\_name - name of squad
  * type - strategic/tactical type of squad(like infantry,armor,etc)
  * skin - character skin associated with this squad
  * player\_assoc - player this squad is associatiod with


---

## command ##
  * gametime\_given - gametime the command was issued
  * gametime\_expire - gametime after which the command is no longer in action
  * status - 0=active, 1=inactive
  * given\_by - name handle for whom the command was given by
  * level - level of command, currently level 2=higher/strategic level(hunt/follow), level 1=reactive level(no\_engage)
  * priority - priority order of command where lower real number equals higher priority
  * squad\_name - name of squad command applies to
  * command - various high and low level commands (hunt,follow,no\_engage,etc)
  * target - target name the command references (bot/player name)
  * location - (x,y,z) location the command references
  * radius - UU(unreal unit) radius for successfully reaching target/location point

The command table and supporting script should support a hierarchy of goals via the 'level' of command with several commands at the same level possibly having an order or 'priority' of consideration or execution.

`game->strategy/analysis->command->squad->unit`


---

## system\_settings ##
  * manual\_spawn - 0=bots respawn automatically, 1=bots are manually spawned by outside process
  * behavior(this column will probably be renamed 'gametype') - what gametype are we playing


---

## last\_seen ##
  * location - last sighted location(x,y,z) of target
  * unit\_name - name of target
  * bot - 0=target is not a bot, 1=target is a bot
  * report\_by\_unit - name of reporting unit
  * report\_by\_squad - name of reporting unit associated squad


---

## kill\_score ##
  * killed\_name - name of bot/player killed
  * killed\_location - location(x,y,z) of bot/player killed
  * damage\_type(not shown) - type of damage involved with kill
  * killer\_name - name of killing bot/player (if available)
  * killer\_location - location(x,y,z) of bot/player (if available)

---

# Sample table rows #

```
sqlite> insert into unit_to_squad(unit_name,bot,squad_name) values ('jerry',0,'evil');
sqlite> select * from unit_to_squad;
1||jerr|0|mojo
2||mark|1|gonzo
3||ralph|1|gonzo
4||lloyd|1|gonzo
5||julius|1|gonzo
6||barry|1|gonzo
7||john|0|evil
8||jerry|0|evil
```

```
insert into command(status,level,priority,squad_name,command,target) values (0,2,1.0,'gonzo.roam','hunt','jerr');
insert into command(status,level,priority,squad_name,command) values (0,1,1.0,'gonzo','engage');
insert into command(status,level,priority,squad_name,command) values (0,1,1.0,'gonzo','pursue');

update command set status=0 where row_id=5;

sqlite> select * from command;
2||||0|||2|1.0|gonzo|hunt|jerr||
4||||0|||1|1.0|gonzo.roam|no_engage|||
5||||0|||2|1.0|gonzo.roam|hunt|jerr||
6||||0|||1|1.0|gonzo|no_engage|||
7||||0|||2|0.5|gonzo|follow|ralph||
```

```
==level 1
engage, no_engage
pursue, no_pursue

==level 2
halt
hunt(player/last_seen), target
follow(bot), target
```

# Further tables #

squad\_type table
  * number/type unit list/lead unit(pass pathing info)
  * ai director queries/assigns units into squads

formation\_table (formations as percentage scale vs absolute number)
  * attack,travel,defend
  * maintain squad formation/cohesion,spread(distance between units)

# Miscellaneous #

Note the older initial tables obs and navpoint are not currently utilized.  obs functionality has been replaced by last\_seen and navpoint will be redone with additional functionality in the future.


---

# Maintenance #

The following tables can have their row information removed after gameplay/analysis as needed
  * last\_seen
  * kill\_score

Remove all rows from a table like the following sql command

{{{delete from kill\_score;}}

The 'vacuum' command can also be run periodically to compact the database size

`vacuum;`

# SQL and sqlite tips #

`.schema` or `.schema tablename` from the sqlite command prompt will list the table structure

http://code.google.com/p/xenia/wiki/SqliteTutorial

Look for keyword 'insert' and 'update' sql statements - java program context but SQL syntax the same
http://code.google.com/p/sqlitebot/source/browse/trunk/bot3/Main.java

