![http://sqlitebot.googlecode.com/files/Img_2202.jpg](http://sqlitebot.googlecode.com/files/Img_2202.jpg)

_Anything can be looked at as some version of a tower defense puzzle, using better prediction to advantage_

see also [Wiki pages](http://code.google.com/p/sqlitebot/w/list) [SqlitebotFlow](SqlitebotFlow.md) [DevelopmentIdeas](DevelopmentIdeas.md) [MapGenerator](MapGenerator.md) [Videos](http://code.google.com/p/sqlitebot/wiki/SqlitebotVideos)

document http://docs.google.com/Doc?docid=0AUGdvmaCDgbOZGcyYnozbjZfNTdma2g3cWNnZw&hl=en <br />
article http://aigamedev.com/open/articles/sqlite-bot <br />
powerpoint http://docs.google.com/present/view?id=dg2bz3n6_51dh2xbpcj <br />
youtube http://www.youtube.com/watch?v=XWkXcTIPkpM

developer blog http://ochopin.blogspot.com

comments/questions http://groups.google.com/group/sqlitebot or jeremy.cothran@gmail.com - I don't get notification of comments on these wiki pages and don't check often so I'm likely to miss any comments on the wiki pages themselves


---



---

# Updates #

## **Update** - October 8, 2014 ##

Been working on a grappling hook + space combat game in unity currently labeled 'rangerfall'

https://github.com/garcia1968/rangerfall

![https://raw.githubusercontent.com/garcia1968/rangerfall/master/images/ranger2.jpg](https://raw.githubusercontent.com/garcia1968/rangerfall/master/images/ranger2.jpg)

![https://raw.githubusercontent.com/garcia1968/rangerfall/master/images/ranger.gif](https://raw.githubusercontent.com/garcia1968/rangerfall/master/images/ranger.gif)

## **Update** - May 19, 2013 ##

Currently working on an abstract game ['Isaac'](http://nestorgames.com/#isaac_detail) for Board Game Arena website listed below:

https://github.com/garcia1968/isaac

For developers interested in playing or developing already published board games, the following two websites may be of interest:

== Boardspace

http://boardspace.net/english/index.shtml

The above website features mostly single-player abstract games with the AI http://boardspace.net/english/abouttherobot.shtml developed as far as I can tell by the site developer Dave Dyer.  It's a Java site and the games AI there is unavailable for review to my knowledge, although I don't know if anyone has asked for the AI source code to be shared for learning or experimentation purposes.

== Board Game Arena
http://en.boardgamearena.com

The above website is great for finding an online board/card game with multiple players and for developers also features a php/javascript/mysql based platform http://en.boardgamearena.com/#!doc/Studio for developing out a mutually decided published game.  This site doesn't currently host or develop AI's in favor of finding human opponents, but the development model is a good one to utilize or emulate with respect to board/card games.

## **Update** - February 6, 2013 ##

Project [bernoulli](bernoulli.md)

http://www.kongregate.com/games/garcia1968/bernoulli

![http://sqlitebot.googlecode.com/files/bernoulli.jpg](http://sqlitebot.googlecode.com/files/bernoulli.jpg)

## **Update** - November 20, 2012 ##

http://aisandbox.com/start/

_Our Capture The Flag game is a hybrid between the tactical and deterministic combat of FROZEN SYNAPSE, along with the fast objective-based gameplay of franchises like KILLZONE. This provides the best combination of highly predictable skill-based control which is essential for E-Sports (or this competition) along with the complexity of modern levels and gameplay rules._

## **Update** - July 9, 2012 ##

Project [bled2zed](bled2zed.md)

A unity browser based multiplayer zombie/deathmatch project.
![http://sqlitebot.googlecode.com/files/show2.jpg](http://sqlitebot.googlecode.com/files/show2.jpg)

## **Update** - January 12, 2012 ##

Project [phibian](phibian.md)

Wanted to try to provide a game server for more continuous AI development and code sharing. Most AI contests I've seen so far involve sending a code package to a server for execution on a game contest server - while these contests are helpful,fun and informative, they don't provide a forum for sharing and evolving code in a community way.

## **Update** - June 27, 2011 ##

Published the first version of 'shadowshot' , a stealth type game at

http://www.kongregate.com/games/garcia1968/shadowshot

![http://sqlitebot.googlecode.com/files/shadow_full.jpg](http://sqlitebot.googlecode.com/files/shadow_full.jpg)

Fairly straightforward AI that breaks guards into one of several subgroups such as: cover,patrol,pursuit,wander

Created a few subfunctions to return total ground distance of selected paths to determine where good target locations should or should not be placed.

Will probably incorporate other subfunctions to utilize influence/heat maps as part of distance/pathing weights with other projects.

Background music is randomly generated/timed lower/upper notes based on blues scale.

Used flixel(FlxPath), flixel power tools(collision,fonts), DAME and sfxr.

I did enjoy and the game was inspired by http://www.kongregate.com/games/mastermax/ultimate-assassin-3 although I'm trying to extend the AI and some other concepts of that game and would like to push it into some multiplayer aspects seen in http://www.frozensynapse.com


---

## **Update** - July 13, 2010 ##
Getting into flash games development with flixel


---

## **Update** - March 9, 2010 ##
I'm taking a break from development for now - posted some basic documentation on using sqlite with Visual Studio at [VisualStudio](VisualStudio.md) and on the Starcraft AI front, playtesting against 'krasi' who has an excellent tank rush strategy and game experience, I'm feeling a bit overwhelmed that a manual approach might be a never-ending task of game-specific programming tactic vs counter-tactic - for a more generalized learning AI I'd probably go in the direction of spatial analysis(starting with just basic radius areas of effects for multiple enemy units vs the current singular first-sighted enemy target) for avoiding formations at their strong points while attacking enemy formations at their weakest points.  Past that, I still need working scouts, for recon/targeting and also the ability to change build base build strategy and multiple base expansion.

In the end, at the current time, I think of RTS's like a fluid-dynamics chemical catalyst issue - model the game and units as stream/flow or fluid-mixing physics problem to achieve a desired end mixture or reaction that moves toward a final win state.  The really time-consuming bit is how to model these tactics and countertactics in a general way that might work for multiple RTS games and then logging/weighting the optimal strategies accordingly with gameplay experience over time.  A single person/team or closed approach seems like a full-time job while an open-source set of functions/solutions might be a way of distributing and chipping away at the problems over the long term.

The latest svn source code of BasicAIModule.cpp is a bit better in regards to visual debugging using the draw function - group/troop counts are displayed in the upper left corner, home group0 target is a small red circle and away group1 target is a large red circle, hurt units have a small yellow circle.


---

## **Update** - January 26, 2010 ##
Documentation/code for Starcraft AI competition effort posted at [Starcraft1](Starcraft1.md)


---

## **Update** - January 7, 2010 ##
Questions or suggestions regarding this project or AI competitions in general feel free to post to the google group at http://groups.google.com/group/sqlitebot  If there's a common AI or group AI problem that multiple developers are looking for a solution for in their mod work, then that might help form an interest/focus, but most game AI for now just seems like retreads of the same old solutions(and same poor, memory starved behaviors).  More like a puppet show on a rail/gallery shooter than a truer form of learning AI or AI which makes better use of possible gameplay information over time :)

Developing a general bot API/database for the Unity 3D framework http://forum.unity3d.com/viewtopic.php?t=35592&highlight=sqlitebot might be an interest also for indie/browser-based games.

Have also posted on the following forums to see if any developer interest

aigamedev http://forums.aigamedev.com/showthread.php?t=3386 http://forums.aigamedev.com/showthread.php?t=3593
http://forums.aigamedev.com/showthread.php?p=42810

ModDB http://www.moddb.com/forum/thread/sqlitebotsquadsschema-for-ut2004

Epic http://forums.epicgames.com/showthread.php?t=705438&highlight=sqlitebot

Valve TF2 http://forums.steampowered.com/forums/showthread.php?t=1102152 http://forums.steampowered.com/forums/showthread.php?t=1102908

Pogamut https://artemis.ms.mff.cuni.cz/pogamut/tiki-view_forum_thread.php?forumId=6&comments_parentId=789


---

## **Update** - December 14, 2009 ##
I've grown **bored** working on this project by myself without much **feedback** - if there are others who are interested in seeing this project continue, find it useful or contributing in some way, let me know by email(jeremy.cothran@gmail.com) or post on the google group http://groups.google.com/group/sqlitebot otherwise I just feel a bit that I'm working in a vacuum and don't feel very motivated to continue the interest or work.  I'll probably take a shot at the StarCraft AI competition next September http://eis.ucsc.edu/StarCraftAICompetition using a sqlite relational database for the AI as well and will probably post some documentation regarding that.


---

# Pre-install steps #

You'll need the Gamebot API which is a UT2004 mod which supports game state communication with a separately command-line run Gamebot API process thread.

See installation instructions at http://artemis.ms.mff.cuni.cz/pogamut/tiki-index.php , probably the easiest is to run the self-installer listed on that page at http://artemis.ms.mff.cuni.cz/pogamut_files/Pogamut_v2.4.1.exe

# Easy install (jar file) #

Last Update: October 31, 2009

Just download/unzip this [zip file](http://sqlitebot.googlecode.com/files/sqlitebot.zip) to  'c:\sqlitebot' folder(includes necessary .jar and \lib) and run the following command. **The sqlite dbpath uses an absolute path of c:\sqlitebot\sample.db' so the sample.db file must live there for the script to work.** Bot shows up using bot launcher as 'Player\_Jeremy' for now.

`java.exe -jar c:\sqlitebot\sqlitebot.jar`


---

**Update September 9, 2009**

from http://www.botprize.org/

_The 2009 BotPrize Contest has been decided!_

_None of the bots was able to fool enough judges to take the major prize. But all the bots fooled at least one of the judges._

_The most human-like bot was sqlitebot by Jeremy Cothran. The joint runners up were anubot from Chris Pelling and ICE-2009 from the team from Ritsumeikan Univerisity, Japan. Jeremy and Chris are both new entrants, and the ICE team were also runners up in 2008._

My thanks to all those organizing and participating in the botprize 2009 contest.  I really appreciate the infrastructure, work and ideas of those contributing.

**So what's next?**  I'm sure there are lots of ideas on how to better improve the bots, but how to do that more systematically in a more tools/framework environment?  What **open source AI tools/frameworks**(including or in addition to the Gamebot/Pogamut) could the community develop/utilize to better automatically generate/debug/tune code from behavior/planner schema and more easily mix and match bot intelligence/behavioral features?  How do AI projects more effectively build on existing base knowledge towards more novel/inclusive behaviors,features and combinations?

An approach that comes to mind is working with UML diagrams as a general model abstraction which helps inform/populate underlying toolsets.  Not sure what the AI parallel approaches are in this regard.

The approach which I'll probably start next is to refactor the existing code as a [behavioral tree(BT)](http://aigamedev.com/open/articles/bt-overview/) using the pogamut [POSH](http://www.cs.bath.ac.uk/~jjb/web/posh.html) subproject to describe the behavior tree in a .lap file.  Will probably also take a look at the earlier [POSH GUI project](http://artemis.ms.mff.cuni.cz/pogamut/tiki-index.php?page=POSH+GUI) for displaying the .lap content.  Using the .lap file should help better abstract the AI design from its implementation to serve as a better working example for reuse in a variety of other AI and implementation contexts.

Another development aspect would be to develop a [navmesh](http://racc.bots-united.com/tutorial-navmesh.html) directly from the map geometry via [exporting the .t3d file](http://wiki.beyondunreal.com/Legacy:T3D_File) using the unreal editor and analyzing/reprocessing this file to include the automatically generated pathnodes/navpoints within the map and database files.  Map navmeshes developed automatically from the geometry in this way could also be coded for certain point or area attributes like cover,jumping,etc.  _Update: Unreal includes an existing console command for auto-generating pathnodes which is not recommended highly due to performance issues relating to the number of pathnodes, so might focus instead initially on analysis/feature extraction from existing pathnodes versus the map geometry._

Links of immediate interest - AI and gameplay tools/analysis are interlinked<br />
Unreal Visualization Toolkit http://digitalfootprints.co.uk/?page_id=9 <br />
Visualizing Competitive Behaviors http://www.cs.virginia.edu/~gfx/pubs/lithium/

So more questions than answers here - if folks have links or ideas I can list them on this wiki for consideration.  I've started a partial list [here](http://code.google.com/p/sqlitebot/wiki/sqlitebotHome#Links)

If folks are interested in the very latest version of the sqlitebot code, contributing to this project or AI/gameplay consulting/contracting or just a friendly word/comment, send me an email(jeremy.cothran@gmail.com).  I have also setup a [google group discussion list](http://groups.google.com/group/sqlitebot) and [twitter feed](http://twitter.com/sqlitebot).


---

**Interested in some UT2004 bot AI cage match action?**  email me(jeremy.cothran@gmail.com) your Pogamut based bot AI .jar file (including launch file) with bot skill level set at level 4 and I can run a best out of 3 deathmatch on level 'Crash' or some other level to be discussed.  Video results and discussion posted to youtube.  see [botmatch ](http://code.google.com/p/botmatch/)

---

**Table of Contents**


# Introduction #

Many gaming AI's utilize behavioral decision trees, few incorporate locational/situational memory as logged say via a lightweight relational database like sqlite

This is an open source project which is trying to utilize a [sqlite](http://sqlite.org) database to store gaming bot location and situational awareness to help better adapt gaming AI(Artificial Intelligence) and behaviors for games.

The project is starting utilizing the [Pogamut](http://artemis.ms.mff.cuni.cz/pogamut/tiki-index.php) Java/NetBeans based project code.  This bot was also selected as a finalist in the [2009 botprize contest](http://www.botprize.org/teams.html) with finals being held in September 2009.

My(jeremy.cothran@gmail.com) development background is more with Perl so I'll be learning some Java and NetBeans as I go.  I've played with Unrealscript (similar to Java) in the past and familiar with some of the Unreal Tournament 2004 bot functions but will be learning more about that also as I go.

The initial goals of the project are:

  1. Java - figure out how to properly break my files up so functions and variables are scoped properly
  1. begin modifying bot behavior in interesting ways based on sqlite database lookups(locational/situational memory)

Initially I'm looking for help with the above two goals - writing Java classes/functions/packages appropriately split up across files in the project - figuring some interesting behaviors to tackle - and being familiar enough with the standing bot functions to leverage those without rewriting them.

The initial sqlite table would be structured something like:

```
CREATE TABLE obs_type (
    row_id integer PRIMARY KEY,
    row_entry_date text,
    map_id int,
    location text,
    status int
);
```

with **location** reflecting the bots map(**map\_id**) location when a specified **status** (made a kill, was killed,etc) at a specified time(**row\_entry\_date**).

Tracking those things initially should allow a bot to begin selecting better and worse map locations for optimal scoring or begin time-based tactics or behaviors relating to location and predicted location of opponents.

# Installing sqlite driver #

To get going with sqlite and java I utilized the .jar at http://www.zentus.com/sqlitejdbc/ , [.jar file here](http://sqlitebot.googlecode.com/files/sqlitejdbc-v056.jar) although the connection was refused by the program until I added the .jar file to both the netbeans classpath and the location C:\Program Files\Java\jre6\lib\ext\sqlitejdbc-v056.jar (see http://www.newsvoter.com/blog/TechNotes/java-lang-ClassNotFoundException--org-sqlite-JDBC.html )

I'd like to try the Xerial sqlite .jar http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC as it should be faster executing, but the zentus seems to work for the time being.

To establish the 'sqlite' driver in the netbeans IDE, goto Window->Services, then from the pop-up window select Databases->Drivers, then right click on Drivers to add a 'New Driver' and on the pop-up after that click the 'Add' button and path to the location of the sqlite jar file (for me this is C:\Program Files\Java\jre6\lib\ext\sqlitejdbc-v056.jar )

Then click the 'Find' button for 'Driver Class' which should populate the field with 'org.sqlite.JDBC'

For the driver 'Name' field enter 'sqlite' and **be sure it is all lower case and spelled the same** for it to work with the existing code example.  If the name/case is different than 'sqlite' you may need to correct it in your **project.properties file** to get it to stop looking at the wrong/bad example for the sqlite driver.

To add the sqlite driver to the netbeans libraries, from the netbeans IDE menu choose Tools->Libraries  On the pop-up window under 'Class Libraries' choose 'New Library'and on the subsequent pop-up name the library 'sqlite' (all lower case).  Then on the following options use the 'Add JAR/Folder' button to navigate to the sqlite jar file (path is C:\Program Files\Java\jre6\lib\ext\sqlitejdbc-v056.jar )

To add the sqlite driver to the project library, under the Project->Libraries folder, choose 'Add Library' and choose the sqlite library.

## Working with sql, sqlite database ##

Download the sqlite3 windows executable .exe (in a [zip file](http://www.sqlite.org/sqlite-3_6_15.zip)) from the [sqlite download page](http://www.sqlite.org/download.html).

My current path to the database is
`c:\progra~1\pogamu~1\PogamutPlatform\projects\AdvancedBot\src\advancedbot\sample.db`

To open this database and get a sqlite command prompt I issue the following command from a command window.

`c:\Documents and Settings\logan\Desktop>sqlite3 c:\progra~1\pogamu~1\PogamutPlatform\projects\AdvancedBot\src\advancedbot\sample.db`

Notice also that the above database path is required or should be changed in the java functions which access the database(Main.java).

## Basic SQL commands ##

From the sqlite command prompt, to see a list of tables<br />
`.tables`

The only table on the database is 'obs', to see the schema<br />
`.schema obs`

To see a listing of rows on the table<br />
`select * from obs;`

To certain rows on the table<br />
`delete from obs where row_id > 3;`

To delete all rows from the table<br />
`delete from obs;`


---


# google group discussion #

http://groups.google.com/group/sqlitebot

# Subversion #

For notes on using [subversion](http://code.google.com/p/sqlitebot/source/browse/#svn/trunk) for this project, see the notes at http://code.google.com/p/rcoos/wiki/SubversionNotes which should apply the same for this project except that the project name is 'sqlitebot' instead of 'rcoos'


---


# Links #

## delicious ##
http://delicious.com/giraclarc/pogamut <br />
http://delicious.com/giraclarc/bots <br />
http://delicious.com/giraclarc/aigamesdev <br />
http://delicious.com/giraclarc/ai <br />
http://delicious.com/giraclarc/gamesdev <br />


---


http://aigamedev.com <br />
http://christophermpark.blogspot.com <br />
AI Sandbox http://aigamedev.com/open/tutorials/hard-earned-insights <br />
Game SDK's http://aigamedev.com/search/?no_cache=1&tx_ttnews[swords]=sdk <br />
http://www.crymod.com/thread.php?threadid=9531

Mario AI<br />
http://julian.togelius.com/mariocompetition2009/ <br />
http://ice-gic.ieee-cesoc.org/

Google/Tron competition winner forum comments<br />
http://csclub.uwaterloo.ca/contest/forums/search.php?keywords=&terms=all&author=a1k0n&sc=1&sf=all&sr=posts&sk=t&sd=d&st=0&ch=300&t=0&submit=Search

http://en.wikipedia.org/wiki/Robocode

http://www.introversion.co.uk/defcon/bots/ <br />
python API http://www.galcon.com/ <br />
http://omegatechsys.com/Products/Bots/ <br />
http://nerogame.org/

RTS<br />
http://springrts.com/<br />
http://www.boswars.org/<br />
http://wargus.sourceforge.net/<br />

Misc<br />
https://groups.google.com/forum/#!forum/resistance-ai

## AI Life ##

http://nobleape.com <br />
http://critterding.sourceforge.net/

## Industry tools ##

http://www.xaitment.com <br />
http://www.spirops.com <br />
http://www.pathengine.com <br />
http://www.ekione.com <br />
http://www.havok.com <br />

## Books ##

AI Game Programming Wisdom (4 book selected articles series) <br />
http://www.amazon.com/AI-Game-Programming-Wisdom-CD/dp/1584505230

C++ for Game Programmers <br />
http://www.amazon.com/C-Game-Programmers-Development/dp/1584504528/ref=sr_1_3?ie=UTF8&s=books&qid=1265812681&sr=1-3

## Miscellaneous ##

http://www.thinkartificial.org/artificial-intelligence/ai-apparent-intelligences/ <br />
http://www.ai-blog.net/archives/000173.html <br />
http://www.trianglegameconference.com <br />

http://www.cp.eng.chula.ac.th/~vishnu/<br />
http://aigamedev.com/open/coverage/killzone2/<br />

### Tim Sweeney(Epic Games, Unreal) ###

  * Interview http://www.gamasutra.com/view/feature/4035/from_the_past_to_the_future_tim_.php
  * GPU Roadmap http://graphics.cs.williams.edu/archive/SweeneyHPG2009/TimHPG2009.pdf
  * Languages Roadmap http://www.st.cs.uni-sb.de/edu/seminare/2005/advanced-fp/docs/sweeny.pdf

## Developer blogs ##

http://the-witness.net/news <br />
http://fullbright.blogspot.com <br />
http://davidjaffe.biz <br />
http://magicalwasteland.com <br />
http://2dboy.com <br />
http://hcsoftware.sourceforge.net/jason-rohrer <br />




---


# Gotchas, Tips #

Things that took me a while to figure out:

  * In netbeans, to set the 'main project' to compile/debug/run, right click on the project of interest and choose the 'Set as main project' option
  * Set your java, javaw (and netbeans?) thread priorities in Windows to 'realtime' (and UT2004 to 'low') via the Task Manager - otherwise the bot seems to lag/hang in its environment waiting for the Java to execute.
  * When monitoring the bots via Unreal Tournament, press alt-enter for windowed version of game(instead of full screen) and ctrl-esc to move control back to the desktop
  * The format for setting the 'attributes' of the various 'ControlServer commands' listed at [Gamebot API](https://artemis.ms.mff.cuni.cz/pogamut/tiki-index.php?page=Gamebots+API+list) is use of the curly brackets, e.g. like below for setting added bots skill level
`addbot {skill 1}{name harry`}
  * set the game speed - 'set speed of game' in the UT Server Control' project window to about 1/2 of the default value to better follow along the action and the bot log/thoughts as they happen.
  * netbeans jar tutorial http://www.fsl.cs.sunysb.edu/~dquigley/cse219/index.php?it=netbeans&tt=jar&pf=y

---

If you want to modify the Pogamut class code, use the following steps
  * copy the existing class code to a new class under your project using the same .java filename
  * make any changes and successfully compile the code
  * save/exit Pogamut IDE, temporarily rename C:\Program Files\NetBeans 6.5.1\nb6.5\modules\ext\PogamutCore.jar to PogamutCore.jar.zip
  * in the project folder, copy the newly compiled .class files to a 'zip' folder with the same pathing as the file to be replaced in PogamutCore.jar.zip
  * in PogamutCore.jar.zip add/replace the existing .class files from the matching folder path to the new .class files.  You may want to backup the earlier .jar file and/or .class files in case there are problems.
  * rename PogamutCore.jar.zip back to PogamutCore.jar and you should be able to see the new class files in effect

---

Running the java thread in the NetBeans IDE consumes much greater CPU resources than running it as a separate .jar thread.  Running the game speed in the IDE at half-speed or lower can bring down the CPU consumption to something more manageable during testing.

---



# botv1 #

For the first version bot (botv1) all I've done is hack the basic pogamut bot [script](http://code.google.com/p/sqlitebot/source/browse/trunk/bot1/Main.java) to write out its location to a [sqlite table](http://code.google.com/p/sqlitebot/source/browse/trunk/bot1/sample.db) when the navpoint path is broken.

The write function is called 'nasty' and the table called 'people' for no good reason.  Will rename these more appropriately later.

# Update June 22 2009 #

I've modified the bot, borrowing mostly from the 'hunter' bot example and a strafing function from the 'amis/loque' bot(many, many thanks to folks sharing their example code, which helps much with the learning/syntax curve and being able to reuse prior development).  Found it helpful to display all message events to the log and then filter ones out that I wasn't interested in.  The bot now records the map, location and timestamp when it kills or is killed(a database INSERT function) and uses one of these most recent 3 locations recorded in the last 45 game seconds to return to(a database SELECT function).

This database could be utilized to build additional summary tables(something like a heat/influence map) which could reflect further analysis/statistics processing based on the locational data.  Also interesting would be to move beyond just the bots immediate 'recent' row-based memory and begin pattern analysis of the locational data to detect generalized trends/patterns which might recur during gameplay.

The bot could also experiment with behavioral types like 'camping/sniping' at certain locations to determine the most effective spots/regions of the map or player conditions to do so.

Would also like to move into recording of pathing and enemy predicted pathing and outcomes as might be utilized in leading or flanking maneuvers.

The current sqlite table 'obs' is organized as follows

```
sqlite> .schema obs
CREATE TABLE obs (
  row_id integer PRIMARY KEY,
  row_entry_date text,
  map_level text,
  map_id int,
  navpoint_id int,
  location text,
  unreal_id text,
  event_location text,
  event_time real,
  event_weight real);

sqlite> select * from obs;
1|2009-06-22 12:13:06|DM-Flux2|234|180|-1226.0,-1258.0,-319.99|DM-Flux2.PathNode
16|-255.48,1193.43,-315.15|1225.19|1.0
2|2009-06-22 12:13:15|DM-Flux2|234|180|-1226.0,-1258.0,-319.99|DM-Flux2.PathNode
16|-286.4,1357.37,-296.91|1234.79|1.0
3|2009-06-22 12:13:20|DM-Flux2|234|180|-1226.0,-1258.0,-319.99|DM-Flux2.PathNode
16|-29.54,1288.36,-296.94|1239.97|1.0
```

## Issues ##

When the bot is 'hit' (receives a BOT\_DAMAGED message), the hit message stays in memory for a while causing the bot to focus on reacting by turning for seconds longer than is necessary after the event.  To get around this, I tried adding some code to get the bot to only pay attention to hits within a certain narrower time window by keeping track of the hit time/gametime and subsequent requests.

# Bot2 #

[Bot2](Bot2.md)






