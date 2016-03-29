**Table of Contents**


Currently the MapGenerator script provides the map content(walls,pickups,etc) via text files to the [gnuplot](http://www.gnuplot.info) graphing tool([demo gallery](http://gnuplot.sourceforge.net/demo_4.5),[windows download](http://www.tatsuromatsuoka.com/gnuplot/Eng/winbin/)) which is combined with observed data from the [database schema](SqlitebotSchema.md) output as text files and also given as input to gnuplot.

[MapGenerator script](http://code.google.com/p/sqlitebot/source/browse/trunk/mapgen/ut_mapgen.pl) [Database query script](http://code.google.com/p/sqlitebot/source/browse/trunk/scripts/bot_graph.pl) [Gnuplot graph script](http://code.google.com/p/sqlitebot/source/browse/trunk/scripts/gnuplot.script)


---

# Map analysis #

The below map shows the original map('sourwood') in the Unreal Editor with the following spotlight legend
  * green=player start
  * blue=health
  * orange=weapon
  * red=ammo

Download [DM-Sourwood.ut2](http://sqlitebot.googlecode.com/files/DM-Sourwood.ut2)

![http://sqlitebot.googlecode.com/files/sourwood.jpg](http://sqlitebot.googlecode.com/files/sourwood.jpg)

Sample gnuplot generated analysis maps

The left map is the starting map showing walls and the legend where
  * green plus=player start
  * blue cross=health
  * purple square=weapon
  * red cross=ammo

The right map shows the locations where the player was 'last seen' during gameplay, the influence of the health pickups can be seen

![http://sqlitebot.googlecode.com/files/map.png](http://sqlitebot.googlecode.com/files/map.png) ![http://sqlitebot.googlecode.com/files/last_seen.png](http://sqlitebot.googlecode.com/files/last_seen.png)

The end goal would be to use these time-accumulated statistics to influence the bot pathing goals(for example, weighting highly trafficed pathnodes more heavily for selection) or detect and address possible event or frequency related strategies associated with these observed patterns.


---

# Bot spawn script #

Created the following [perl script](http://code.google.com/p/sqlitebot/source/browse/trunk/scripts/spawn.pl) which will run continuously in a command window, trying to spawn bots as available (`select count(*) from bot where available=1`)

This potentially has the advantage of having greater control of being able to control over where and what types of bots respawn depending on the gametype/gameplay factors.  For now it only controls the initial spawn of the bot.

Unfortunately I'm not able yet to also automatically set the thread priority of the automatically generated spawn to a high 'realtime' priority, so the bots are 'stuttery' until I manually give assign the thread that high priority.

Also the system\_settings table manual\_spawn column can be set equal to 1 cause the process thread to end when the bot dies.

