# Asset files description #

zip file http://ratingsbay.com/flash/shadowshot/assets.zip

## Graphics ##

http://ratingsbay.com/flash/shadowshot/assets/graphics

knight3.png - the font set used by the game from Photon Storm (FlxBitmapFont)

concrete.jpg - the background used for the map - at 158K this could probably be simplified/reduced in size

For the below graphics, the alpha channel is used to convey where the image should be transparent.

ability.png, health.png, time.png - these are just the indicator bars at the game bottom that are scaled to indicate amounts

tiles.png - the tileset used for the map - just the first 10x10 area is used in the game, the other 2 are used in mapmaking

ball\_player2\_tile.png - this is the main player character, 10x10 tileset with tile 1 used for still and tile 2 for moving(tile 0 unreferenced).

boss\_2.png - 10x10, 4-tile animation

guard\_game.png - 1010, 2-tile animation(last 2 tiles unreferenced)

fire\_3.png - the graphic used to imitate gunfire, quick fade-out after shot.


---

## Maps ##

http://ratingsbay.com/flash/shadowshot/assets/graphics

pathfinding\_map.txt, sector\_map.txt - these are just CSV(comma separated value) files - pathfinding map is used for the display and sector\_map.txt is the same, except including some waypoints/patrol points for the AI to follow.  5 maps/levels currently.

Use DAME tilemap editor for easy GUI editing of the tilemaps

http://www.photonstorm.com/archives/1358/dame-2-released-my-favourite-game-map-editor


---

## Audio ##

http://ratingsbay.com/flash/shadowshot/assets/audio

Audio currently used by the game - descriptions later

8-bit retro sounds created using sfxr http://www.drpetter.se/project_sfxr.html