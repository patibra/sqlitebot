see also [sqlitebotHome](sqlitebotHome.md)

Feedback loop based on gameplay stats,etc - thinking at systems level, sharing schema/behavior design


**Table of Contents**


# Content/behavior generation/support/management #
  * MapGenerator - simple perl script to generate simple random map layouts for testing AI
    * also supports capture of map info to database or visualization tools as map is generated

# Database schema #
  * SqlitebotSchema - details regarding the sqlite relational database schema utilized
  * gameplay/AI/stats logging
  * db schema (high/squad goals, low/unit\_type behaviors)

# Low-level game interface/implementation #
  * `UT2004<->Gamebot API<->Pogamut java message objects/scripts`
  * Source files (Main.java, etc)
    * database functions db..., refreshInfo

# High-level analysis/action/visualization #
  * SqlitebotAnalysis - online and offline analysis and visualization of collected game data
  * planner(to be developed based on gametype)
  * perl,etc/AI scripting for generating unit/squads,commands,behaviors
  * offline gameplay/AI visualization/analysis