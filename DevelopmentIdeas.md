

# currently working on #

  1. using AStar pathing successfully(provided Pogamut core code is buggy) and altering pathing to remove navpoints which are within enemy view/rotation(or predicted pathing view/rotation) for evasive or flanking behaviors
    1. disengage combat, hide - choose hidden point that is further from enemy location than current with path not intersecting enemy current visible point

# next steps #

  1. bot mirror state? adapt skill/accuracy ?  if I stop shooting/moving will bot?  will bot lose interest or toy with lower skill/interest?  people like playing people of similar skill level
  1. bot handle multiple states ? pursue + health, combat + health, etc
  1. shadow - hold attack if from behind until close sure-kill range
  1. have bot randomly stop, circle stop, random fire, random high traffic point fire/lob
  1. flee/lead enemy around corner into projectile fire tactic?
  1. lob projectiles over/around/off objects for kills
  1. bot shouldn't always constantly charge or be vulnerable from predictable tactics
  1. add human noise/imperfection to pathing and target locations, handle collision?
    1. create navpoint locations and offset tolerances for 'off' pathing
    1. serpentine pathing?
  1. navpoint visibility/rotation lookup table - type of navpoint for strategic planning, navpoint locations to determine density/visibility/optimal room location/rotation of navpoints as room/hall/circuit relations
    1. calculate current fov visibility from current rotation
  1. enemy threat level based on kill table?  switch target hard/easy based on proximity, facing and status of engagement?
    1. player kills per minute(kpm) weighting? player favorite locations,etc based on logging earlier gameplay
    1. keep enemy focus on closest player or players of most/least threat(depending on tactics)
  1. subfunctions to command bot to rotate to precise rotation, move forward,back,left,right some amount
  1. bot grab player dropped weapons(if needed)
  1. pickup timing/memory
  1. bot hearing, turn toward player action(not random noises)
  1. team/squad based communication/coordination/tactics/behaviors

# questions #

  1. is there a bot line of sight distance?
  1. game speed effects on logic, AI?
  1. set gamebot infinite time for AI testing on map?