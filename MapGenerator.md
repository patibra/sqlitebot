see also [sqlitebotHome](http://code.google.com/p/sqlitebot/wiki/sqlitebotHome)

<a href='http://www.youtube.com/watch?feature=player_embedded&v=yrIhBlWYqoU' target='_blank'><img src='http://img.youtube.com/vi/yrIhBlWYqoU/0.jpg' width='425' height=344 /></a>

![http://sqlitebot.googlecode.com/files/Out3Big.jpg](http://sqlitebot.googlecode.com/files/Out3Big.jpg)

**Table of Contents**


---

# Intro #

In developing bot AI for Unreal Tournament 2004(UT2004), the need for the bots to better strategically/tactically understand the map and related pathnodes/waypoints is important.  Towards that goal, I've created an initial simple [perl script](http://code.google.com/p/sqlitebot/source/browse/trunk/mapgen/ut_mapgen.pl) which utilizes a [config.ini file](http://code.google.com/p/sqlitebot/source/browse/trunk/mapgen/config.ini) to generate a random room configuration of varying lights,intersecting walls and pickups(aka a '**Danger Room**' ala X-Men :).

Having the ability to automatically generate these simpler/flat maps provides several things

  * a measure of control for repeating AI experiments across a variety of similarly generated maps/scenarios(so the AI/design is testable and tuned in a variety of similarly-featured but not exactly the same maps - not overly optimized to one particular map/setting)
  * further opportunity to automatically create pathnode/waypoint attribute markups(visibility,cover,areas,etc) or other map feature analysis which benefits the AI
  * further opportunity for controlled gameplay/AI analysis, feature extraction and replication(auto-generation of game content with similar themes but controlled variation)

The above map image(DM-Out3Big.ut2) for instance of a large generated map(shown in [UnrealEd](http://en.wikipedia.org/wiki/UnrealEd), the Unreal editing tool included with UT2004) is 8,000x24,000 UU(Unreal Units) in size.

The config file allows for a simple gridded pathnode pass(set path\_option=true in config.ini) on the map for bots to utilize for pathing/navigation.  Better pathnode generation for bot AI utilization will be the focus of ongoing efforts.

## Notes ##

> Download Source(zip) http://sqlitebot.googlecode.com/files/mapgen.zip <br />
> Source http://code.google.com/p/sqlitebot/source/browse/trunk/mapgen

  * Windows-based version of perl for running the script can be found at [ActivePerl](http://www.activestate.com/activeperl/)
  * The script generates a [.t3d](http://wiki.beyondunreal.com/Legacy:T3D_File) ASCII readable text file, which will need to be imported into UnrealEd(menu File->Import), built(menu Build->Build All) and saved(menu File->Save) as a .ut2 map for play
  * The script is an initial version and uses a memory hash for preventing overlaps between the geometry,pickups,etc.  This results in memory being soaked up from your system for the hash, so if the script dies for a larger map it is probably because it ran out of system memory(monitor the system memory when the script runs).  I'll fix this in a later version to buffer objects using just a distance calculation between objects.
  * The map as displayed in UnrealEd top-down view is 180 degrees opposite(positive y goes down the screen)

# Sample maps #

## Out3Big ##
Download [DM-Out3Big.ut2](http://sqlitebot.googlecode.com/files/DM-Out3Big.ut2)

This is the big map (8,000x24,000 UU size) shown in the page top image.  It does not have any pathnodes included for bot play.


---

## Out3 ##

Download [DM-Out3.ut2](http://sqlitebot.googlecode.com/files/DM-Out3.ut2)

This is a smaller map (4,000x8,000 UU size) shown in the below image.  It does include a grid of connected bot pathnodes for bot play/testing.

![http://sqlitebot.googlecode.com/files/Out3.jpg](http://sqlitebot.googlecode.com/files/Out3.jpg)


---

## Out3Disconnect ##

Download [DM-Out3Disconnect.ut2](http://sqlitebot.googlecode.com/files/DM-Out3Disconnect.ut2)

This is a smaller map (4,000x8,000 UU size) shown in the below image.  It include more walls which break the gridded pathnodes into disjoint areas.  A more interesting map for human players with the additional walls, but difficult for bots to currently path/navigate.

![http://sqlitebot.googlecode.com/files/Out3Disconnect.jpg](http://sqlitebot.googlecode.com/files/Out3Disconnect.jpg)

# Sample config.ini file #

The config.ini file currently supports the following map parameters.  Note only the listed pickup types are currently supported, can include more as requested or see the code

```
map_name=DM-Out3Big.t3d
map_type=xDeathmatch

#small x=4000,y=8000,z=512
#large x=8000,y=24000,z=512
map_size_x=8000
map_size_y=24000
map_size_z=512

#default = 600000, lower for more, higher for less
light_density=300000

#################################
#walls

#default = 400000, lower for more, higher for less
#800000 better number for insuring bot paths connect(more open area) with gridded pathnodes
wall_density=400000

#clear buffer area at bottom and top of map
end_buffer=200

wall_width=100
wall_rand_length=300
wall_min_length=300
wall_rand_height=100
wall_min_height=128

#buffer area around each wall
wall_buffer=30

##################################
#textures

vault_texture=ArboreaTerrain.ground.flr02ar

wall_texture_1=AbaddonArchitecture.Base.bas01go
wall_texture_2=ArboreaTerrain.ground.moss02ar
wall_texture_3=ArboreaTerrain.ground.Sand01ARb
wall_texture_4=ArboreaTerrain.Miscellaneous.tree08ar

##################################
#pathing

#path_option=true for gridded pathnodes(if bots will be using map), false for none
path_option=false
pathnode_grid_distance=600

###

#player_start_max will override player_start_density
player_start_max=6
#default = 9,000,000, lower for more, higher for less
player_start_density=9000000

#below array is type,count
pickup_list=RocketLauncher 3 RocketAmmoPickup 4 FlakCannon 3 FlakAmmoPickup 4 LinkGun 3 LinkAmmoPickup 4 SniperRifle 3 SniperAmmoPickup 4 Minigun 3 MinigunAmmoPickup 4 NewHealthCharger 6 MiniHealthPack 10
```

# Further Development #

  * better pathnode analysis and placement.  Analyze map for areas which should be connected with the minimal or most useful number of pathnodes
    * would like to utilize [recast/detour](http://code.google.com/p/recastnavigation/) tool for this but struggling with documentation/examples on incorporating the tool into a command-line workflow
  * integration of map/pathnode generation with AI strategic/tactical attributes(visibility,cover,area,etc) generation - stored within RDB schema
  * gameplay/AI movement/event logging via sqlite relational database(RDB) with results such as map hotspots,etc displayed as a secondary map or overlay image
    * map feature extraction/rating for further auto-generation of maps with similar features
  * additional map shapes,objects,textures - area grouping of textures,lighting
  * additional map variation beyond simple/flat vault room in regards to terrain deformations or x/y curving map sections


---

# Update October 31, 2009 #

Updated the generator script with the following changes
  * pathnodes are 'split' when attempting to drop on a wall object to help increase chance of successful pathing
  * background lighting levels set to 'white' from random color
  * player starts and pickups are spotlight color-coded as shown in the map below for a better visual guide when observing AI behavior
  * default ceiling height raised to 3000 UU to allow panning while spectating without losing sound,etc from leaving the world area
  * script supplies wall line segment and pickup points in separate output text files(ut\_wall.txt,ut\_health.txt,etc) for reuse in gnuplot graphing/analysis,etc later

The below map shows the original map('sourwood') in the Unreal Editor with the following spotlight legend
  * green=player start
  * blue=health
  * orange=weapon
  * red=ammo

Download [DM-Sourwood.ut2](http://sqlitebot.googlecode.com/files/DM-Sourwood.ut2)

![http://sqlitebot.googlecode.com/files/sourwood.jpg](http://sqlitebot.googlecode.com/files/sourwood.jpg)
