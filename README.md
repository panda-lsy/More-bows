# More Bows for 1.7.10! 

TODO: Finish port & work on a Fabric port! 

This is a port of the More Bows mod to 1.7.10! This mod was originally created by GaussFire, and then maintained & updated by iDiamondhunter. This port aims to faithfully re-create the original mod, while fixing some bugs & then potentially introducing new mechanics (or re-adding cut ideas!). 

Progress: 

- Most things (barely) work! 

Issues / TODO list: 

- **The ender bow will currently cause ConcurrentModificationExceptions and crash your game!** This bow really is _still_ in development, so beware of it! (That being said, it doesn't freeze the entire game for 3 seconds anymore.) The ConcurrentModificationExceptions will be fixed when I re-write that code to not use my janky multi-threading, as Minecraft is not a thread-safe game. 
- I haven't bothered to mark the bonus arrows as such yet, so you get free arrows when you fire the Legia Bow or Frost Bow! 
- The bow animations are all the same speed, and don't reflect how fast you're actually drawing the bow. 
- The frost arrow renders as a normal arrow instead of a snow cube. 
- Particle effects are hit-or-miss on whether they work due to me not implementing sided proxies yet. 
- One bow has an off-center animation frame. Check and update any assets that are different on the spritesheet than the individual icons. 

Not technically an issue but should be fixed: 

- My code is sort of hacky in a few places. I need to clean it up by re-writing a bunch of classes, and also need to be even hackier in some places to remove the access transformer I've used. Also - better documentation is needed! 
- The Legia Bow still shoots arrows at weird angles. I'll fix this once the mod is completely ported over. 
- Run optipng over all assets. Figure out if anything can be done to the .JPEGs without losing any quality. 
- Find out if there were ever any translations of this mod. 
- More stuff I still need to write down here! 

Licence: 
As iDiamondhunter ["removed the copyrights" from this mod](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286765-1-6-2-more-bows-mod?comment=733), I presume that this mod is free of any known copyright, and that it is in the public domain. Therefore, I will be keeping it licensed as such. 
