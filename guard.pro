-verbose

# Make sure to check everything always, can't hurt.

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing

# We're a bit oldschool

-target 1.8

# TODO Figure out how to do this but for annotations

-adaptclassstrings
#-adaptresourcefilenames
#-adaptresourcefilecontents

# Keep all methods that overrides a super method, or methods that are an event handler.

-keepclassmembers,allowobfuscation class * {
    @java.lang.Override,cpw.mods.fml.common.eventhandler.SubscribeEvent,cpw.mods.fml.common.Mod$EventHandler <methods>;
}

# Keep all classes with public methods

-keepclasseswithmembers,allowobfuscation public class * {
    public <methods>;
}

# ProGuard really wants to remove the mod instance and sided proxies. This stops it from doing that.

-keepclassmembers,allowobfuscation class * {
    private static iDiamondhunter.morebows.MoreBows inst;
    private static iDiamondhunter.morebows.MoreBows proxy;
}

# These two classes are referred to in String constants inside of annotations. TODO see above (around -adaptclassstrings).

-keepnames,allowoptimization public class iDiamondhunter.morebows.Client
-keepnames,allowoptimization public class iDiamondhunter.morebows.MoreBows

# Keep all initialisers, because ProGuard keeps trying to make them private.

-keepclassmembers class * { public <init>(...); }

# Annotations and generic method signatures are kept, as Forge uses them for reflection.

-keepattributes *Annotation*,Signature

# Repackage all classes into iDiamondhunter.morebows

-repackageclasses iDiamondhunter.morebows

# Bonus optimisations

-optimizationpasses 64
-mergeinterfacesaggressively
-overloadaggressively

# TODO Decide if this is a good idea. allowaccessmodification is probably safe. dontpreverify seems to work on modern JVMs (target needs to be Java 6, lower doesn't matter as preverification doesn't exist yet), need to do more exhaustive testing.

-dontpreverify
-allowaccessmodification

# Debug info

-printusage
-whyareyoukeeping class iDiamondhunter.**
#-printconfiguration proguardDebug.txt
