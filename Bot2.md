

Hi all,

Thanks for the earlier tips regarding better function to use regarding the bot visibility.  Wanted to report my progress as it might be of help or interest to others.

What I have on now are two pieces(source code at http://code.google.com/p/sqlitebot/source/browse/trunk/bot1/Main.java , apologies that the code is kind of an experimental mess at this time):

1)a method(behavior="navlog") of having the bot step through each navpoint in a  given map and log the visibility of other navpoints from that navpoint(in 360 degrees of vision) to a database table(table navpoint).

2)an initial experiment(behavior="hide") that as the bot picks navpoints at random to travel to will log whether the navpoint is visible or not visible based on the earlier database lookup.  Initially I was going to have the bot 'hide' from me based on looking at the nearest navpoint to my position and then going to a navpoint which should not be in my field of view based on the earlier table information.

On point 1, the initial issues which I ran into were that memory.getSeeNavPoints seemed to be the only reliable method I could use to get the bots current 'vision' based on a bot vision of 90 degrees of arc(45 degrees to either side).  The javadoc says 360 degrees of vision but I in my experiment, the bot could only see the usual 90 degrees in front of itself.  To compensate for the other 270 degrees of view that I wanted to register, I had the bot upon arriving at a destination navpoint slowly rotate(using turnHorizontal(5)) until it was within one of 4 target rotations(0,16000,32000,48000) and at each target rotation report/record the visible navpoints(from\_navpoint\_id,to\_navpoint\_id).

I could probably optimize this better by creating a turnToRotation function(couldn't find such a function in the existing Pogamut codebase) which would look at the bots current rotation and apply the proper amount of turnHorizontal for the given and desired rotation arguments.

To have the bot record/insert records for a given map, it should just be setting the bot behavior="navlog" and the bot should begin running around the map(using KnownNavPoints) and inserting records of the map\_level name and each from\_navpoint\_id and associated to\_navpoint\_id's.  I added a few lines to get the bot to avoid recording jumppads(since the bot will get into an infinite loop trying to stay on the jumppad) or navpoints that were previously indexed.  Added a few lines to get the bot to try jump/double-jumping when 'stuck'(WALL\_COLLISION) trying to get to a destination.  Also set the time limit for the map as high as possible via the Gamebot deathmatch startup batch file, although the maximum time limit regardless seemed to be 2 hours.

I'm a bit confused about whether I should use the unreal int id or the 'memory.KnownNavPoints' int id for recording - the memory.KnownNavPoints seem to keep their mapping reference to the unreal int id's between reboots and are easier to work with than the unreal int id's which 'skip' numbers and are more 'difficult' for processing.  In the end I ended up recording the unreal int id to the table and am using a hash on startup to provide a mapping back to the KnownNavPoints.  I think I'd like to just switch to using the KnownNavPoints as this is the usual frame of reference from within Pogamut scripting if that wont cause problems down the line.

```
////////////////////////////////////////////
//Hash code example

//on startup

   for (int i=0; i<memory.getKnownNavPoints().size(); i++) {
         navMap.put(Integer.toString(memory.getKnownNavPoints().get(i).ID), new Integer (Integer.toString(i)));
    }

//working with lookup hash

         ArrayList<Integer> ArrayToGet = new ArrayList<Integer> ();
         while (rs.next()) {
             ArrayToGet.add(rs.getInt(1));
         }

         //get random navpoint
         int myRandHide = random.nextInt(memory.getKnownNavPoints().size());
         int thisNavpoint = memory.getKnownNavPoints().get(myRandHide).ID;
         
         if (ArrayToGet.contains(thisNavpoint)) {
             log.info("navpoint is visible");
         }
         else { log.info("navpoint not visible"); }


         Object lookup = navMap.get(Integer.toString(thisNavpoint));
         int i = Integer.parseInt(lookup.toString());

         log.info("hide_navpoint:"+thisNavpoint+":"+i);
         chosenNavigationPoint = memory.getKnownNavPoints().get(i);
         
////////////////////////////////////////////    
```

Anyway, the new table navpoint has the following schema and sample records for map\_level=DM-1on1-Crash:

```
CREATE TABLE navpoint (
row_id integer PRIMARY KEY,
row_entry_date text,
map_level text,
from_navpoint_id int,
to_navpoint_id int,
visibility int
);

sqlite> select * from navpoint where from_navpoint_id = 183;
45|2009-07-18 20:29:26|DM-1on1-Crash|183|80|1
46|2009-07-18 20:29:26|DM-1on1-Crash|183|82|1
42|2009-07-18 20:29:10|DM-1on1-Crash|183|134|1
39|2009-07-18 20:29:10|DM-1on1-Crash|183|136|1
43|2009-07-18 20:29:26|DM-1on1-Crash|183|185|1
38|2009-07-18 20:29:10|DM-1on1-Crash|183|186|1
40|2009-07-18 20:29:10|DM-1on1-Crash|183|214|1
41|2009-07-18 20:29:10|DM-1on1-Crash|183|215|1
44|2009-07-18 20:29:26|DM-1on1-Crash|183|216|1
```

At this point, the 'visibility' column is defaulted to 1(=visible) and I might remove it unless it comes in handy for holding some other associative types.

The following sql statement can be used to generate a listing of navpoints with the 'most' visibility to other navpoints to 'least' visible.  This is a bit deceiving as currently a bot could be in very isolated 'treasure room' with lots of goodies but with no visibility to the rest of the map.  I'll probably add a navpoint filter using the unreal id string for only navpoints containing 'PathNode' to get a better/truer idea of the overall map visibility.

```
SELECT from_navpoint_id, COUNT(*)
    FROM navpoint
    GROUP BY from_navpoint_id
    HAVING COUNT(*) > 1
    ORDER BY COUNT(*) DESC;

41|56
58|54
60|52
17|51
141|51
140|50
...
```

The sqlite database which includes the navpoint visibility mapping for the 'Crash' level can be downloaded from http://sqlitebot.googlecode.com/files/sample.db

# Next steps #

Will still plan to use the above record information to tell a bot where it could 'hide' from the current players position(also adding programming to help it navigate **away** from an enemy towards a hiding spot) or running an initial sql query like above to create a visibility 'index' which could be initially run/pushed into a hash for later reference and use by the bot in knowing where a good 'hiding' spot or 'action' spot might be.

Would also like to write some scripts to do some further analysis of the gathered navpoint visibility to help weight the navpoints and their collection/connections/relation to each other in regards to helping the bot differentiate say between a 'room' size and 'hall/corridor' and different strategies for those area types and developing/negotiating strategic path circuits.

Thanks again for the help and glad to explain/discuss any of what I've developed as it might be useful to others pursuing similar ideas and techniques.

Jeremy