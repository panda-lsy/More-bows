# More Bows for 1.7.10!

[![Licence badge](https://img.shields.io/github/license/NeRdTheNed/More-bows "Licence")](https://github.com/NeRdTheNed/More-bows/blob/master/LICENSE)
[![Java SE version compatibility badge](https://img.shields.io/badge/Java%20SE-5-orange?logo=java "Java SE version compatibility")](https://en.wikipedia.org/wiki/Java_version_history#J2SE_5.0)

### _This port is not finished yet! Stay tuned for progress on it, and feel free to check out the latest dev builds!_

<p align="center"> <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/master/src/main/resources/mb.jpg" alt="More Bows Logo" title="More Bows!" style='width: 100%; object-fit: contain'/> </p>

This is a port of the More Bows mod to 1.7.10! This mod was originally created by GaussFire, and then maintained & updated by iDiamondhunter. This port aims to faithfully re-create the original mod, while fixing some bugs & then potentially introducing new mechanics (or re-adding cut ideas!).

Other people's ports:
- LucidSage's port of the More Bows mod to 1.8 can be found [here](https://github.com/LucidSage/More-bows)!

## Progress: Most things (barely) work!

Issues / TODO list:

- ![Ender Bow Icon](https://raw.githubusercontent.com/NeRdTheNed/More-bows/master/src/main/resources/assets/morebowsmod/textures/items/EnderBow1.png) **The ender bow can currently cause ConcurrentModificationExceptions and crash your game!** ![Ender Bow Icon](https://raw.githubusercontent.com/NeRdTheNed/More-bows/master/src/main/resources/assets/morebowsmod/textures/items/EnderBow1.png) This bow really is _still_ in development, so beware of it! (That being said, it doesn't freeze the entire game for 3 seconds anymore.) The ConcurrentModificationExceptions will be fixed when I re-write that code to not use my janky multi-threading, as Minecraft is not a thread-safe game.
- ~The bow animations are all the same speed, and don't reflect how fast you're actually drawing the bow.~ Still working on it, not sure if accurate now.
- The frost arrow renders as a normal arrow instead of a snow cube.
- Particle effects are hit-or-miss on whether they work due to me not implementing sided proxies yet.

Not technically an issue but should be fixed:

- My code is sort of hacky in a few places. I need to clean it up by re-writing a bunch of classes, and also need to be even hackier in some places to remove the access transformer I've used. Also - better documentation is needed!
- The Legia Bow still shoots arrows at weird angles. I'll fix this once the mod is completely ported over.
- ~Run optipng over all assets. Figure out if anything can be done to the .JPEGs without losing any quality.~ Still working on the mod icon, but mostly done!
- Find out if there were ever any translations of this mod.
- Check and update any assets that have mutated too much over the years.
- More stuff I still need to write down here!

Licence:
As iDiamondhunter ["removed the copyrights" from this mod](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286765-1-6-2-more-bows-mod?comment=733), I presume that this mod is free of any known copyright, and that it is in the public domain. Therefore, I will be keeping it licensed as such.

<p align="center"> <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/master/docs/images/1-7-10.png" alt="CurseForge user the_spider_overlord: dangit if only this had a 1.7.10 backport!" title="Did someone say backport???" style='width: 100%; object-fit: contain'/> </p>

<p align="center"> <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/master/docs/images/ScreenshotShootingPigs.png" alt="A mod for 1.7.10 in 2020? When pigs fly..." title="A mod for 1.7.10 in 2020? When pigs fly..." style='height: 100%; width: 100%; object-fit: contain'/> </p>
