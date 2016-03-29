

A basic viewer for the game and game results is available  at http://ratingsbay.com/phibian/viewer.html

The viewer has a default sample game '0' loaded with 4 players - just click the 'Start' icon to see the game play.

Note:  If interested in submitting a client AI for testing against the phibian game server(in development) or working on game server development, please email jeremy.cothran@gmail.com with 'phibian' in the subject line.  The initial game server is code development(perl) is very crude and experimental, but glad to share code with others as interested(open source project).

# Introduction #

Wanted to try to provide a game server for more continuous AI development and code sharing.  Most AI contests I've seen so far involve sending a code package to a server for execution on a game contest server - while these contests are helpful,fun and informative, they don't provide a forum for sharing and evolving code in a community way.

To provide an alternative, the 'phibian' (short for amphibian, something between evolutionary stages) project is designed initially to provide a simple game data/event API with which to exchange game information between a game server and clients, allowing AI developers to focus more on the AI development aspects and continual improvement and sharing than a singular, packaged AI designed within the constraints of a singular game.  This design is modeled after human multiplayer interaction, where the only interface a human player has to the game is the screen/audio and controller interfaces and anticipates that at some point machine vision/audio research will progress to the point of being able to 'play' games using the same screen/audio/controller interface that human players use(in other words without having to hack the data stream directly as aimbot cheats do currently).


---

# initial gametype - hunt/seek #

The initial simplest game type would be a top-down 2D map perspective for 2-4 players/clients to 'hunt' each other.  Each player has a map x,y location and a 'cone' of vision with which to detect other players/goals.  Users would provide a username and password to authenticate and register their AI client(not implemented currently).  The game server would accept and respond to the below simple commands on port 2346.  The game would create a simple text output file at the game end(not during the game to prevent cheating) detailing the game turn information for game visualization.  Initial game visualization is being developed in flash using some earlier game code with a similar top-down perspective and 'cone' vision http://code.google.com/p/sqlitebot/wiki/ShadowShot

Ongoing AI/player competition match-ups and rankings would be provided.

Initially the focus is on AI vs AI development, but I would like to incorporate a player visualization/control interface to allow human vs AI also.  Better/different independent visualizations of the player/game data are encouraged.

A similar turn-based tactical shooter game concept is seen in the game 'Frozen Synapse' http://www.frozensynapse.com which draws from earlier classic games such as 'XCOM'.  Later melee combat development might be stats/numbers based, such as higher accuracy for slower movement,etc.


---

## sample client ##

To get started, a simple sample client AI script has been provided at http://sqlitebot.googlecode.com/files/client_5.pl  This is a very 'dumb' AI in that it just picks movement goal locations at random, changing goals on reaching a goal or becoming blocked.  It will try to do a simple tracking of enemy bots it sees based on last known location.

This script can be run with the following command line several times for example to have the AI compete against itself(note all clients are using the same group\_password(grp1),max\_players(4) and max\_score(3) parameters.  The total number of turns can also be set below 5000.  Teams(red1 below) can also be setup(currently teams do not fire on or target each other).

```
perl client_5.pl 0 jcothran "" grp1 red "" 4 3 "" "" "" > out1 &
perl client_5.pl 0 joe "" grp1 red "" 4 3 "" "" "" > out2 &
perl client_5.pl 0 larry "" grp1 "" "" 4 3 "" "" "" > out3 &
perl client_5.pl 0 barry "" grp1 "" "" 4 3 "" "" "" > out4 &
```

```
my ($player_wait,$username,$password,$group_password,$teamname,$team_password,$max_players,$max_score,$total_time,$gametype,$gamemap) = @ARGV;
```

The test game server should be up and running continuously at the address and port shown in the sample client, but may need some fixes with further testing/participation.

### sample client return information ###

the sample perl client should be a simple enough example to get started - the client basically sets up a socket connection with the game server and requests(game\_request) a game and is returned a line with the game\_start,game\_id,player\_id for an available game.

```
c:start=====================
check
c:game_request
c:start=====================
game_start,124013,154084
```

Once the session is established the player attempts to move to the specified path(the play command)

```
c:play,124013,154084,path,102,102
c:start=====================
check:0
```

and receives one of the following response lines from the server

  * check:<turn #>
    * player\_info,x,y,rotation
  * goal\_reached
  * blocked,x,y
  * respawn,x,y
  * score,1,kill
  * hit,player\_slot,x,y
  * health,x  _next update_
  * game\_over

Currently the server moves the player towards the move\_goal at about 3 pixel lengths per turn and auto-faces the player rotation towards the direction of movement(something which will be fixed later so that players can look in a direction other than which they are moving).


---

# sample/test maps #

## map 1 ##

The following map is the one currently referenced by the game server and is represented in the simple text grid file at the following link where closed pixels=1 and open pixels=0  Feed this file data to your AI algorithm for map analysis for this static map.

http://ratingsbay.com/phibian/phibian_map_1.txt

![http://sqlitebot.googlecode.com/files/phibian_map_1.png](http://sqlitebot.googlecode.com/files/phibian_map_1.png)

## sample results output(.csv files for viewer) ##

most commands in the output .csv file are preceded by the player(0-3) to which they are directed - although '0' is used to precede some general game information like max\_score,etc

0,move,x,y  #move to map location x,y - predetermined speed of 'n' units per turn - will stop if hit a block/wall

0,rotate,x  #rotate focus to angle x - predetermined rotation speed of angle amount per turn


---

# Viewer #

A basic viewer of the game results is available using some flash scripting, with the viewer link available at http://ratingsbay.com/phibian/viewer.html

The viewer has a default sample game '0' loaded with 4 players - just click the 'Start' icon to see the game play.

## File selection ##

filename - the user enters the game\_id given back during the client session to enter for the filename number(like 89980), this is a reference to the .csv file at http://ratingsbay.com/phibian/games

turn time - the amount of time between turns - defaults to shortest 0.001

number of players - 2 to 4 players

![http://sqlitebot.googlecode.com/files/phibian_viewer_1.jpg](http://sqlitebot.googlecode.com/files/phibian_viewer_1.jpg)

## Results simulation ##

The viewer shows a turn-based recreation of the chosen game\_id, with the vision 'cones' an approximation to the ray-cast detection used by the game server.  The bot path goals are also shown for debugging purposes.

The light red bar over the username indicate the current player health.  If a player 'detects' another player, it will register as a 'hit' and decrease health.  If health goes below zero, the player will respawn and a score given to the player who made the final 'hit'.  The teamname:username and graph indicated the scoring progress.

![http://sqlitebot.googlecode.com/files/phibian_viewer_2.jpg](http://sqlitebot.googlecode.com/files/phibian_viewer_2.jpg)


---

# expected AI client development #

  * A-star - pathing algorithms to navigate the map
  * map waypoints and/or sectors - to 'remember' or have memory of areas already visited or more intense action/scoring opportunities
  * movement/rotation behaviors, for example
    * when navigating a corridor, align vision with direction along the corrider(don't waste effort looking at walls or places unlikely to find an enemy)
    * when rounding approaching or rounding a corner, 'slice the pie' in regards to rounding the corner slowly to improve accuracy while aiming at the newly exposed area
  * team/squad information sharing - 2 or more clients might share information to more quickly or successful track other players/targets.  Will probably setup a gametype to request team play specifically to prevent an unfair game advantage.
  * code-library or sharing - bringing together individually developed micro/melee and macro/strategy behaviors into larger packages to 'evolve' the clients with better AI features
  * game capture/modeling - statistical map/game analysis for player/opponent modeling


---

# Map analysis #

Some initial map analysis using the python scripts at

http://code.google.com/p/sqlitebot/source/browse/#svn%2Ftrunk%2Fphibian%2Fmaps

vision range = 20 pixels - stronger definition of wall cover, more area defined as 'open'
![http://sqlitebot.googlecode.com/files/vis20.png](http://sqlitebot.googlecode.com/files/vis20.png)

vision range = 100 pixels - blue areas show high direction towards large open areas, slight yellows where areas more open
![http://sqlitebot.googlecode.com/files/vis18.png](http://sqlitebot.googlecode.com/files/vis18.png)

vision range = 400 pixels - most areas more open
![http://sqlitebot.googlecode.com/files/vis19.png](http://sqlitebot.googlecode.com/files/vis19.png)

map rectangle(block) filling experiment - towards auto-defining waypoints
![http://sqlitebot.googlecode.com/files/block_map.png](http://sqlitebot.googlecode.com/files/block_map.png)