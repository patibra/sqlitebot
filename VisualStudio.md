This is a summary of steps that I went through to compile the sqlite3.lib static library and some sample C++ code which utilizes that library to connect, write and query from a sample sqlite database.

This was done using the free Visual C++ 2008 Express edition IDE from Microsoft.

First download the latest C source code zip [sqlite-amalgamation-3\_6\_22.zip](http://www.sqlite.org/sqlite-amalgamation-3_6_22.zip) from http://www.sqlite.org/download.html



---

# Create static library sqlite.lib #

As a shortcut, I've included a link to the sqlite.lib that I created on my Windows XP machine [here](http://sqlitebot.googlecode.com/files/sqlite3.lib) - probably better to compile the library on your system using the below steps as it may or may not work depending on the system.

Start a new project where the application type will be a static library and uncheck the precompiled headers option.

![http://sqlitebot.googlecode.com/files/sqlite2.jpg](http://sqlitebot.googlecode.com/files/sqlite2.jpg)

![http://sqlitebot.googlecode.com/files/sqlite3.jpg](http://sqlitebot.googlecode.com/files/sqlite3.jpg)

Add the .c source file to the 'Source Files' of the project and the .h files to the 'Header Files' of the project and compile a 'Release' version of the sqlite.lib file

![http://sqlitebot.googlecode.com/files/sqlite4.jpg](http://sqlitebot.googlecode.com/files/sqlite4.jpg)


---

# Sample project and code #

Start a new project (the below was done as a Win32 console application) which includes in the project folder sqlite.h and sqlite.lib.  In the 'Header Files' include sqlite.h and reference the sqlite.lib file as shown below.  The screenshot below should say 'Release' instead of 'Debug' for the Configuration box.

![http://sqlitebot.googlecode.com/files/sqlite1.jpg](http://sqlitebot.googlecode.com/files/sqlite1.jpg)

The sample sqlite database referenced in the example code can be downloaded at http://sqlitebot.googlecode.com/files/sample.db and assumes the filepath `C:\sqlitebot\sample.db` The schema for this database is shown at http://code.google.com/p/sqlitebot/wiki/SqlitebotSchema and that webpage also contains further examples of SQL usage(SELECT,INSERT,UPDATE,DELETE sql statements) with this schema

The following [article](http://www.linuxjournal.com/content/accessing-sqlite-c) provided the initial examples used in the below code

Include files
```
#include <stdio.h>
#include "sqlite3.h"
```

## Establishing database connection ##
```
int _tmain(int argc, _TCHAR* argv[])
{
sqlite3 *db;

sqlite3_stmt    *res;
int             rec_count = 0;
const char      *errMSG;
const char      *tail;

int error = sqlite3_open("C:\\sqlitebot\\sample.db", &db);
if (error)
    {
    fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
    }
```

## SELECT - preparing the read/query and processing the resultset ##
The following sample code reads all table 'bot' records and displays the first 4 record columns from the database resultset.
```
error = sqlite3_prepare_v2(db,
        "SELECT * FROM bot",
        1000, &res, &tail);

if (error != SQLITE_OK)
    {
    puts("We did not get any data!");
    return 0;
    }

puts("==========================");

while (sqlite3_step(res) == SQLITE_ROW)
    {
    printf("%d|", sqlite3_column_text(res, 0));
    printf("%s|", sqlite3_column_text(res, 1));
    printf("%s|", sqlite3_column_text(res, 2));
    printf("%d\n", sqlite3_column_int(res, 3));

    rec_count++;
    }

puts("==========================");
printf("We received %d records.\n", rec_count);
```

## INSERT, UPDATE, DELETE ##
```
error = sqlite3_exec(db,
    "INSERT INTO command(status,level,priority,squad_name,command,target) VALUES (0,2,1.0,\'gonzo.roam\',\'hunt\',\'jerr\')",
    0, 0, 0);

error = sqlite3_exec(db,
    "UPDATE command SET status = 0, priority=1.0 WHERE row_id = 5",
    0, 0, 0);

error = sqlite3_exec(db,
    "DELETE FROM command WHERE target = \'jerr\' AND priority > 1.0",
    0, 0, 0);
```

## Closing the statement handler and database connection ##
```
sqlite3_finalize(res);
sqlite3_close (db); 

return 0;
}
```

---

## Transactions ##

Note that sqlite database supports **[transactions](http://www.sqlite.org/lang_transaction.html)** which is a much faster way to batch process multiple SQL INSERT or UPDATE statements.  If you have one statement every few seconds, then you probably don't need to use a transaction, but if you have several hundred statements within a second, then transactions would probably help save time.

The outline of the SQL commands for this is:

```
BEGIN TRANSACTION or BEGIN

some set of SQL statements or statement in a loop

END TRANSACTION or COMMIT
```

If any error occurs with the transaction, the set/batch of statements will **not** be committed to the database - the statements will be rolled back(ROLLBACK) to the previous BEGIN TRANSACTION or COMMIT statement.

## Indexing and Optimizations ##

There are a variety of ways to get better database performance by schema design and statement structure detailed at http://www.sqlite.org/optoverview.html http://www.sqlite.org/cvstrac/wiki?p=PerformanceTuning and on the [web](http://code.google.com/p/xenia/wiki/XeniaPackageSqlite#Optimization).

The main way to speed up SELECT statements against tables which might hold several million records is to create a table index which is utilized by the SELECT statement when the conditions of the WHERE clause match or partially match(the column reference order should be the same as the index-described order) the columns referenced by the index.

```
CREATE INDEX some_table_idx ON some_table(a,b,c)

SELECT * FROM some_table WHERE a = 1 AND b > 2 AND c < 3
```

A UNIQUE index will insure that no duplicate records are allowed sharing the same referenced columns.

```
CREATE UNIQUE INDEX some_table_idx ON some_table(a,b,c)
```

## Time/Tick Gating Database Read/Write ##

In the past, to prevent the database from being constantly read or written to by a number of active AI's, I've used latest time and location variables to help time-gate unnecessary repeated access to the database.  Essentially the idea is to object/variable cache recent information so the database is only periodically read or written to and not constantly accessed and updated possibly dragging down performance.

So for instance, if a target enemy was seen at x time and y location saved to a global variable cache, don't write a new database record enemy time/location record until the enemy variable cache has change by x+delta time or y+delta location.  The delta time which I've been using in games has been around 2 seconds.  Similarly for reads, don't request a new database read of table lookup information if less than some minimum delta time has passed since the last read.

## Longer term geospatial analysis ##

Down the road there are some possible geospatial analysis/statistic methods that might be interesting or strategically useful to apply to the gameplay logged events/locations

SqlitebotAnalysis <br />
Quake death heat map http://www.quakeworld.nu/forum/viewtopic.php?pid=50185 <br />
Spatial pattern analysis with R http://www.csiro.au/resources/Spatia...erns-in-R.html <br />
Unreal Visualization Toolkit <br />
Visualizing Competitive Behaviors http://www.cs.virginia.edu/~gfx/pubs/lithium/ <br />
TF2 death heat maps http://www.steampowered.com/status/tf2/tf2_stats.php <br />

A file-portable database could also be utilized over a history of games as a cross-game-reference for
  * one or several adaptive AI agents, groups/squads
  * adaptive player-modeling