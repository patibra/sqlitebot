http://artemis.ms.mff.cuni.cz/pogamut/tiki-view_forum_thread.php?comments_parentId=724&topics_offset=5&forumId=6

http://artemis.ms.mff.cuni.cz/pogamut/tiki-view_forum_thread.php?comments_parentId=483&topics_offset=19&forumId=5

I think I localized what the bugs are within the existing AStar routines that are part of the Pogamut core code.

1)There needs to be a null check(otherwise an excpeption is generated causing the AStar pathing to fail) for the following function:

PathManager.java

```
private Lift isNavpointLift(NavPoint np) {

if (np.UnrealID != null) {
if (np.UnrealID.contains("LiftExit")) return Lift.LIFT_EXIT;
if (np.UnrealID.contains("LiftCenter")) return Lift.LIFT_CENTER;
}

return Lift.NO_LIFT;
}
```

2)memory.seeAllNavPoints (which is only used by AStar) doesn't work right all the time, sometimes it returns no navpoints causing the pathing to fail - don't know how much this is related to memory and the history/batches processing but its a killer bug for the function getPathAStar below. The 'reachable' may also be working incorrectly as well. In the below function I could get AStar to work on a test map with a few nodes, although the bot must have direct line of sight to all nodes it will try to travel to(will run into walls on a regular map). I've replaced seeAllNavPoints with getKnownNavPoints which causes the bot to see each node connected directly(line of sight) to all other nodes - not a solution, just something to get the bot going.

GameMap.java
```
public AStarResult getPathAStar(NavPoint toWhat, int maxNumOfIterations) {
if (toWhat == null)
return null;
AStarGoal goal = new GameMapAStarGoal(toWhat);
AStarMap map = new GameMapAStarMap();

NavPoint start = new NavPoint();
start.location = memory.getAgentLocation();
start.neighbours = new ArrayList();

//ArrayList nvs = memory.seeAllNavPoints();
ArrayList? nvs = memory.getKnownNavPoints(); //JTC
NeighNav? nn;

for (int i = 0; i < nvs.size(); ++i) {
platformLog.info("start_seeNav:"+nvs.get(i).UnrealID.toString());
//if (nvs.get(i).reachable) {
platformLog.info("start_seeNavReachable:"+nvs.get(i).UnrealID.toString());
nn = new NeighNav();
nn.neighbour = nvs.get(i);
start.neighbours.add(nn);
//}
}
...
```

Finally the bot seemed to register function isStucked=true prematurely as well at times causing the path to abort - without knowing why this is immediately you can rig for testing purposes the function to always return false.

With those changes I could get AStar to work, but still not sure why the above functions don't work quite right. Any clues, let me know.

The simple test map I created is downloadable at http://sqlitebot.googlecode.com/files/DM-Test1.ut2


---


**Follow-up** Did some more experimenting later where I only allowed the AStar pathing algorithm to evaluate once instead of multiple times and this seemed to prevent the bot from getting off-tracking or confusing/losing its closest/goal node and allowing the AStar chosen path to complete successfully.  Not sure of the trade-off in keeping the bot from constantly re-evaluating its goal path, but the closest fix I could get.