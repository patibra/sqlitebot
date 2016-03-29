This is a placeholder wiki page for details regarding bot/strategy development for the StarCraft AI competition detailed at http://eis.ucsc.edu/StarCraftAICompetition

I got the BWAPI tools and terrain analysis (BWTA) installed without too much issue.  My basic plan at this point is to use a sqlite database as a data 'foundation' to monitor,control and analyze the production and micro-management of units and groups towards game-winning strategies.  More details as I experiment and open to others who want to collaborate or share development,testing or documentation in some way.

An initial database schema could be the same as or modified from an existing squad control schema at SqlitebotSchema , will have to see how this meshes with existing [functions](http://code.google.com/p/bwsal/) for controlling StarCraft AI.

Questions&Answers for this bot/competition should be directed to http://groups.google.com/group/sqlitebot


---


Update: January 23, 2010

Have posted sqlitebot starcraft version 1.0 at http://sqlitebot.googlecode.com/files/sqlitebot.zip

With the BWAPI toolset/set (unzip files to /bwapi-data/AI folder), feel free to play against this bot(or email me to schedule a few matches) and please post any outcomes,patterns,suggestions,replays,videos at http://groups.google.com/group/sqlitebot .

It's only designed for terran and is a basic marine/medic buildup tactic with the mob following the latest enemy onShow event - I need to work on the scouting more so it will seek an enemy and finish the enemy destruction at hand rather than get distracted by something shiny or go fishing :)

Compiled this under BWSAL\_0.9.7, Chaoslauncher BWAPI injector [revision 1914](https://code.google.com/p/sqlitebot/source/detail?r=1914) option checked.  I had some crash problems when I had Chaoslauncher earlier 1830 revision checked.


---


Update: January 26, 2010

Documentation on my first bot effort at
http://code.google.com/p/sqlitebot/wiki/Starcraft1


---

Update: February 3, 2010

Have posted sqlitebot starcraft version 1.1 at http://sqlitebot.googlecode.com/files/BasicAIModule.dll