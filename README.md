# More Bows for 1.7.10!

[![Licence badge](https://img.shields.io/github/license/NeRdTheNed/More-bows "Licence")](https://github.com/NeRdTheNed/More-bows/blob/forge-1.7.10/LICENSE)
[![Java SE version compatibility badge](https://img.shields.io/badge/Java%20SE-5-orange?logo=java "Java SE version compatibility")](https://en.wikipedia.org/wiki/Java_version_history#J2SE_5.0)

**_This port is nearly finished, but things might still be incomplete or not work as intended! Stay tuned for progress on it, and feel free to check out the latest builds!_**

<p align="center">
    <picture>
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-360.jxl 360w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-480.jxl 480w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo.jxl" type="image/jxl">
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-360.webp 360w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-480.webp 480w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo.webp" type="image/webp">
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-360.png 360w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo-480.png 480w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo.png" type="image/png">
        <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/MoreBowsLogo/MoreBowsLogo.png" alt="More Bows Logo" title="More Bows!" style='width: 100%; object-fit: contain'/>
    </picture>
</p>

This is a port of the More Bows mod to 1.7.10! This mod was originally created by GaussFire, and then maintained & updated by iDiamondhunter. This port aims to faithfully re-create the original mod, while fixing some bugs & potentially introducing new mechanics (or re-adding cut ideas!).

### Original descriptions of each bow (not currently 100% accurate!):

- Reinforced Bow ![Reinforced Bow Icon](src/main/resources/assets/morebows/textures/items/StoneBow1.png): This bow is just like the regular wooden bow, but has more durability.
- Iron Bow ![Iron Bow Icon](src/main/resources/assets/morebows/textures/items/IronBow1.png): This bow is a step up from the reinforced bow, having slightly more damage and durability, along with a minuscule upgrade of draw speed.
- Golden Bow ![Golden Bow Icon](src/main/resources/assets/morebows/textures/items/GoldBow1.png): The bow forged of gold: it has a pathetic amount of uses but overall does the most damage. Quick reflexes allow you to shoot at double the speed!
- Crystal Bow ![Crystal Bow Icon](src/main/resources/assets/morebows/textures/items/DiamondBow1.png): A bow sealed with the power of diamond, not many can survive it's swift and damaging moves. With a diamond base, you don't need to worry about over-pulling, this bow also doubles your draw speed!
- Blazing Bow ![Blazing Bow Icon](src/main/resources/assets/morebows/textures/items/FlameBow1.png): A bow from the horrible pits of the Nether, the power of fire emanates from the weapon in your hand. With this bow, you can do double the damage of a normal bow, while setting your foes alight!
- Ender Bow ![Ender Bow Icon](src/main/resources/assets/morebows/textures/items/EnderBow1.png): A mysterious bow with the secrets of the Ender within. Along with having a slow drawback, it will use the Ender's technique to shoot a regular arrow that will not damage anything but, after 3 seconds, will summon 5 more arrows to kill it's opponents.
- Legia Bow ![Legia Bow Icon](src/main/resources/assets/morebows/textures/items/MultiBow1.png): The final and most overwhelming bow, it delivers fast and many blows to its enemies. Even with two arrows, your draw speed will be faster than the iron bow!
- Frost Bow ![Frost Bow Icon](src/main/resources/assets/morebows/textures/items/FrostBow1.png): Bonus bow for Christmas! Present for everyone who was supporting iDiamondhunter with the mod! This bow makes entities go slowly but its draw speed is awful. It also creates a snow layer on impact, and freezes water!

Descriptions taken from [iDiamondhunter's page for the mod](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286765-1-6-2-more-bows-mod).

### Other people's ports:
- LucidSage's port of the More Bows mod to 1.8 can be found [here](https://github.com/LucidSage/More-bows)!

### Building:
The Gradle build script resolves dependancies automatically, but it relies on having both a Java 6 compatible JDK installed (which Gradle builds the mod with) _and_ a Java 8 compatible JDK (or higher) installed (which Gradle itself runs on). This can be worked around by changing the Gradle build script to build the mod with a JDK that's compatible with one installed on the computer building this mod, although this could potentially result in slight differences or incompatibilities with truely ancient versions of Java. Note that ProGuard is used to output bytecode compatible with Java 5, so this might be a moot point.

### Licence:
As iDiamondhunter ["removed the copyrights" from this mod](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286765-1-6-2-more-bows-mod?comment=733), I presume that this mod is free of any known copyright, and that it is in the public domain. Therefore, I will be keeping it licensed as such.

<p align="center">
    <picture>
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-600.jxl 600w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-768.jxl 768w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-900.jxl 900w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-1024.jxl 1024w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10.jxl" type="image/jxl">
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-600.webp 600w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-768.webp 768w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-900.webp 900w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-1024.webp 1024w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10.webp" type="image/webp">
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-600.png 600w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-768.png 768w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-900.png 900w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10-1024.png 1024w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10.png" type="image/png">
        <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/1-7-10/1-7-10.png" alt="CurseForge user the_spider_overlord: dangit if only this had a 1.7.10 backport!" title="Did someone say backport???" style='height: 100%; width: 100%; object-fit: contain'/>
    </picture>
</p>

<p align="center">
    <picture>
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-320.webp 320w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-480.webp 480w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-600.webp 600w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-768.webp 768w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-900.webp 900w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-1024.webp 1024w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs.webp" type="image/webp">
        <source srcset="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-320.png 320w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-480.png 480w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-600.png 600w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-768.png 768w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-900.png 900w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs-1024.png 1024w, https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs.png" type="image/png">
        <img src="https://raw.githubusercontent.com/NeRdTheNed/More-bows/gh-pages/images/optimised/ScreenshotShootingPigs/ScreenshotShootingPigs.png" alt="A screenshot of a player shooting pigs with the Ender bow" title="A mod for 1.7.10 in 2021? When pigs fly..." style='height: 100%; width: 100%; object-fit: contain'/>
    </picture>
</p>
