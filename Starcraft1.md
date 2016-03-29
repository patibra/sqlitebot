StarCraft project homepage


---

Update: February 3, 2010

Have posted sqlitebot starcraft version 1.1 at http://sqlitebot.googlecode.com/files/BasicAIModule.dll

For those interested in setting up a match via ICCUP servers, as its a bot/code modificaton I play on the 'Abyss' server, I'm on US Eastern time, and usually available for a match between 6:00 to 11:00 PM EDT.  Just let me know a day/time you want to schedule a match and I'll try to be online as user 'sqlitebot' then.  PM me on my ICCUP account(sqlitebot) or my email (jeremy.cothran at gmail.com)


---


This is a discussion of some [sample C++ code](http://code.google.com/p/sqlitebot/source/browse/trunk/starcraft/v1.0/BasicAIModule.cpp) extended from the [BWSAL](http://code.google.com/p/bwsal) AI example.  Many thanks to the [BWAPI](http://code.google.com/p/bwapi), [BWSAL](http://code.google.com/p/bwsal) and [BWTA](http://code.google.com/p/bwapi) teams/individuals/projects for providing these useful game interfaces and examples to program on top of, and [Ben Weber](http://www.youtube.com/watch?v=PbjsL5E1Idw) for organizing the [Starcraft AI competition](http://eis.ucsc.edu/StarCraftAICompetition) whose competitors will be leveraging these tools.

My apologies up front for my poor C++ coding style/indentation - I learned Unix/C(or failed to learn depending on your perspective ;) a long time ago(in a galaxy far,far away) back in university classes and the past several years been more programming with Linux/perl/php(wanting to pick up more python) - so I'm having to grow a familiarity with Microsoft's Visual C++ Express environment and standard(std) libraries as I go.  If anyone wants to suggest a pretty-print utility(or function within the editor I'm unaware of) for the code I can give that a whirl as long as it doesn't muck up the code too much.

If you find these code examples useful, feel free to reuse them, but also please give me some acknowledgement by email that I might keep a public list of.  Also I would be glad to learn, share and incorporate others work into a more robust AI, so email if you have something to mention in the way of development,testing,documentation,etc.

I'd like to see what more open-interfaces/data/source,crowd-sourcing,datamining,collaborative approaches such as the [Netflix prize](http://en.wikipedia.org/wiki/Netflix_Prize) as applied to game AI can provide both businesses and customers of AI-based products.


---

To run this code go through the BWAPI(version 2.6.1) install instructions and then the BWSAL(version 0.9.7) additional instructions.  The follow-up issues that I remember running into were
  * when compiling the BWSAL project, there seemed to be missing references to some of the included libraries, add these libraries in the project references to get a successful compile
  * the BWSAL zip does include a different BWAPI.dll and some other files, be sure to swap the BWSAL version files for the original BWAPI version ones, else the BasicAIModule.dll will fail to load.  You can use a [dll dependency checker](http://dependencywalker.com) to see which support files may be causing a dll load fail issue.
  * chaoslauncher BWAPI installer checkbox should use version 1914(not 1830)
  * BWSAL does a default map terrain analysis, so if loading a new map and the game appears to freeze, give it a minute or two to finish map analysis

Remember to change the bwapi.ini file to reference the BasicAIModule.dll and once you've got the included BWSAL example .cpp case working(base/troops get built and that's about it), then you can swap-in and compile my .cpp file and move the BWSAL\_0.9.7/BasicAIModule/Release .dll,.lib,.exp files to the bwapi-data/AI folder

Also if Visual C++ intellisense(auto-complete on function/pointer parameters,etc) stops working for no good reason, you can delete the project .ncb file and reload the project to get it working again.

The easiest way to follow-along with the below documentation is to look at the side-by-side comparison in the code in the diff page below - I'll be referencing line numbers on the right-hand side that differ from the initial example.

Code diff page
http://code.google.com/p/sqlitebot/source/diff?spec=svn250&r=250&format=side&path=/trunk/starcraft/v1.0/BasicAIModule.cpp

If I don't reference line numbers below, its because it was something experimental that didn't work out for now.



---

# Outline #

The basics of an initial approach utilize the following 4 functions.

  * Build
  * onUnitDestroy
  * onUnitShow
  * checkIdle

My very first approach started with the default terran vulture(vehicle) build, but vultures don't have any air attack, so I switched to marines - lots and lots of marines.

Also I'm testing for now exclusively with terran vs terran matches - later on will consider zerg and protoss strategies, but trying to minimize the complexity of issues to work through for now.

Line #36-43 are global vars, that I reference later.

line #36 enableStart is set to true if you're running the AI from the very beginning of the game and set to false if you're testing the game after the initial build orders are complete.  Otherwise if you restart the game from a saved game midway, the AI will try to rebuild everything again.  There may also be some bugs/side-effects from running/testing this way in regards to how the Arbitrator or other BWSAL code(planning arrays that are populated at game begin) runs with regards to the assumption the game is always run from a clean start.

line #38 countTroop - how many troops do we have?

line #39 attackStrength - some minimum troop level before engaging an attack

line #40 scoutStrength - some minimum troop level before engaging scout(s)

line #42 myBase - pointer to our home command center unit

line #43 baseRadius - a distance radius from the base to help the AI differentiate between attacks on our home base(less than) and attacks in the field(greater than)

## Build ##

line #117-118, 144-208

The initial build order is critical in quickly setting up the kind of production units needed for the strategies/tactics the AI expects to run.  This build order is for a marine/medic mob/attack/defense tactic.  You can experiment with the build order to see how quickly certain units can be produced with what capabilities - against the default game AI, some basic initial defense is needed to prevent from being overtaken by a possible aggressive initial rush.  The BWSAL building routines do a good job of placing building in a well-spaced spiraling outward fashion, but will unfortunately place some building units on the other side of walls and missile turrets without any tactical defense reference(near chokepoints or towards enemy approaches).

## onFrame ##

Not much happening here except for a hook to periodically call checkIdle function

line #227 commented this out as it seemed the update functions might not be getting called in some cases

line #242-246 commented out the scout function call, set a trigger on this behavior later - the default scout when I tested pulled one of my SCV's and after the scout was killed or finished a circuit of the map bases - the scout routine was finished and no further scouting went on.  Still need a good scout function - particularly for subgroups/areas.

line #278 calls checkIdle function about once a second

## onUnitDestroy ##

#line 291-325 very simple functions to simply rebuild whatever matched units or buildings are destroyed

## onUnitShow ##

This is the first event that registers the sight of another unit from the fog of war, so if filtering for enemy units, can start a reaction here.

line #332-356 these are some basic preconditions before attacking, such as
  * keeping track of the last attack time so that units don't spaz out switching between possible new targets
  * not attacking distant targets till some minimum troop attackStrength is met
  * making sure targets are enemies and not self,neutrals,etc

line #360-386 this basically reassigns the unit position=target variable(probably not necessary) and sends ground attack troops directly to the target.  I also tried experimenting with keeping track of the troop unit closest to the target(closestTarget) with the idea that support troops(medics,flyers) should stay behind the front-line of attack, this kind of worked but also failed because the distance function I need is really a ground travel distance and not a bee-line/air distance - so in cases where troops round a corner to an attack the support units would sometimes go backwards away from the front.

line #388-411 special case handling for support troop medics and science vessel

Also on a side-note, when playing the default game AI I had to include the science vessel to counteract the AI's use of cloaked ghosts/wraiths and medic 'restoration' tech to combat enemy medic use of ocular flare to 'blind' my science vessel(which makes the science vessel useless as a cloak detector).

## checkIdle ##

line #467-549 this establishes variable counts for the following debug line in-game

Broodwar->sendText("g:%d:m:%d:t:%d:i:%d:im:%d",gasWorkers,mineralWorkers,countTroop,totalIdle,idleMob);

which are used by other parts of the code to assign workers or establish attack strength/readiness.

Also the unit loop looks for 'hurt' units and sends support troops(medic,science vessel) to the hurt unit.

line #550-602 assign workers to minerals or gas

line #608-642 assign workers to repairs closest to where they are - keeps an array of assigned repair workers so not all workers go to the same repair

line #643-671 this started as a minimal base defense measure - the onUnitShow basically handles most cases, but sometimes an enemy unit might be already be within a home base area attacking and new units produced would not have received the earlier attack order, so this is just a catch-all forcing all units to attack any visible enemy units.  If the enemy is within the baseRadius, troops have to focus on the enemy threat, if the enemy is located far away, then they attack if idle(not already engaged in a firefight).  This also helps keep troops busy firing and from idling during an enemy base attack.


---

# Performance & Issues #

The bot generally does well overwhelming the default game AI as long as a mob of about 40-60 troop size is maintained and the units have leveled up to level 1 weapons with U238 shells and medics with healing.

The mob mainly gets whittled down at chokepoints/bottlenecks like base ramps or picked off one-by-one marching single file before a larger group line is established(movement in column/formations would be better).  This also works against the default AI as well though as it sends its forces in single file into large mob firing squads.

Having a science vessel to reveal cloaked units is also critical in some battles.  The initial marine/medic buildup is quick enough to survive an initial AI marine rush in most cases.

The mob has a problem in that it is a singular entity and isn't able to coordinate attacks from two fronts and essentially leaves the home base defenseless in a 3-4 player match where the base might be enemy overrun while the mob is far away.  The best way to address this is to start providing group management(listed in the next steps section below) to provide multiple defense and attack groups.

  1. Resources management is still flaky - tried using the default BWSAL workerManagement for minerals and gas, but the workers were idle after the mineral field was exhausted.  Workers will also sometimes all go to a depleted gas refinery instead of seeking new resources - so sometimes I manually pull them off towards minerals.  Workers also idle unexpectedly, I think this is related to some timing issues in the build process/planner.
  1. Need to work on a scout - currently the AI doesn't actively scout for the enemy and will remain at home base unless slowly drawn by a series of attacks back to the enemy base - so for testing now I'll manually grab a troop and send him to an enemy base to trigger an attack.
  1. Support troops - did a lot of experimenting with medics and science vessels to try and get support troops to move with the group but not get ahead of the line of fire and in the cross-fire or over into enemy lines.  Sometimes the isIdle function did not seem to be working and had problems essentially with support troops either lagging or leading the front line of attack
  1. AI pathing - mentioned my requests regarding pathing info to the BWAPI team at http://code.google.com/p/bwapi/issues/detail?id=190  While it should be possible to do an A`*` or other pathing algorithm on client-side from the map analysis data, it may be easier to reuse the game pathing or someone else's work on this.
  1. attacking/attacked status - this has been mentioned at the BWAPI thread http://code.google.com/p/bwapi/issues/detail?id=184 , basically would be good to know who is shooting what(coordinating fire) or getting shot by what(coordinating counter-fire or avoidance)
  1. exception cases - in one game, somehow an enemy vulture mine got placed in an impossible to reach area - resulting in the mob massing a target they couldn't get to - so have to handle these strange events as they appear.


---

# Next steps #

## Group assignment, management ##

What I really need to do next is refactor the existing code with groups in mind - basically to begin with the easy to identify groups are
  * worker groups
  * scout groups(maybe just composed of one individual)
  * attack/raiding/harrassment groups
  * defense of bases/resources groups

Each group would have further group attributes like its
  * unit composition - an attack group might have a certain ratio of marines/medics/science\_vessel/SCV for example
  * movement/attack/tactic formation
  * group specific functions or behaviors(e.g. hunters vs gatherer routines)

Group routines should be applicable to other groups of the same type - so two raiding parties should use the same code, just in reference to the units in their group locality.  Groups should also have a way of signaling other groups that they need help(such as home base defense or losing battle) or when a combined group tactic or strength might be more effective(teaming up).

The technical implementation for groups will probably be a series of nested arrays or objects where the routines could be referenced and iterated on for members within each group array or object.  Group selection and function calls will probably be triggered by event locality and group or groups within a target distance.

## Database ##

At this point using a sqlite database is more down the road - so the sqlitebot is a bit of a misnomer for this starcraft project at the time being until some of the more basic group formation, movement and tactic behaviors are worked out.

In the long run a database could be used during or post-game to help permanently log and share the game events so that map/event/timing locality and outcome analysis can be used for producing a better-informed AI or set of cooperative AI's.  Here's a link to my earlier [article](http://aigamedev.com/open/articles/sqlite-bot/) on using a sqlite database with UT2004 bots and an example [database analysis workflow](http://code.google.com/p/sqlitebot/wiki/SqlitebotFlow).


---

# Links #

http://medialogics.com/ma/strategies/starcraft_strategy_guide_6.html <br />
http://classic.battle.net/scc/GS/ <br />
http://bwchart.com

Thanks to Morpher\_King (ICCUP username) for playtesting and following AI guide/strategy suggestions
http://sqlitebot.googlecode.com/files/AI%20Guide.doc

# Tips #

  * Do **not** use getGroundDistance function, it kills performance and will make the game stuttery - I switched back to just getDistance function, although I'd still like a getGroundDistance function without the performance hit
  * use setLocalSpeed function (-1 to 16) for speeding up the game in the onStart function, I'm setting it to about 8 now for testing
  * chaoslauncher has a W-mode checkbox for running starcraft in a windowed mode, very useful for chatting/testing outside of the starcraft/battlenet game supported chat