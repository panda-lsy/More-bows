-verbose

# Make sure to check everything always, can't hurt.

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing

-target 17

# TODO Figure out how to do this but for annotations

#-adaptclassstrings
#-adaptresourcefilenames
#-adaptresourcefilecontents

# Keep all methods that overrides a super method.

-keepclassmembers,allowobfuscation class * {
    @java.lang.Override <methods>;
}

# Keep all classes with public methods

-keepclasseswithmembers,allowobfuscation public class * {
    public <methods>;
}

# ProGuard really wants to remove the mod instance and sided proxies. This stops it from doing that.

#-keepclassmembers,allowobfuscation class * {
#    private static iDiamondhunter.morebows.MoreBows inst;
#    private static iDiamondhunter.morebows.MoreBows proxy;
#}

# These two classes are referred to in String constants inside of annotations. TODO see above (around -adaptclassstrings).

#-keepnames,allowoptimization public class iDiamondhunter.morebows.Client
#-keepnames,allowoptimization public class iDiamondhunter.morebows.MoreBows

# Keep config entry names
-keepclassmembernames public class iDiamondhunter.morebows.config.* {
    public *;
}

# Keep all initialisers, because ProGuard keeps trying to make them private.

-keepclassmembers class * { public <init>(...); }

# Annotations and generic method signatures are kept, as Forge uses them for reflection.

#-keepattributes RuntimeVisibleAnnotations,Signature
#-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,Signature
-keepattributes *Annotation*

# Repackage all classes into iDiamondhunter.morebows

#-repackageclasses iDiamondhunter.morebows

# Mixins

-keepclassmembers,allowoptimization,allowobfuscation public class iDiamondhunter.morebows.mixin.AbstractClientPlayerEntityMixin {
    private float getFovMultiplierMixin(float);
}

-keepclassmembers,allowoptimization,allowobfuscation public class iDiamondhunter.morebows.mixin.HeldItemRendererMixin {
    private float *;
    <methods>;
}

# TODO
-keep public class iDiamondhunter.morebows.mixin.HeldItemRendererMixin
-keep public class iDiamondhunter.morebows.mixin.AbstractClientPlayerEntityMixin
-keep public class iDiamondhunter.morebows.MoreBows
-keep public class iDiamondhunter.morebows.Client
-keep public class iDiamondhunter.morebows.modmenu.ModMenuCompat

# Don't obfuscate CustomBow or any entity classes.
# This does slightly increase the mod's file size, but it might help with mod compatibility.

-keep public class iDiamondhunter.morebows.CustomBow {
    public protected *;
}

-keep public class iDiamondhunter.morebows.entities.** {
    public protected *;
}

# Needed to prevent ProGuard from changing a method signature
-optimizations !method/removal/parameter

# Bonus optimisations

-optimizationpasses 64
-mergeinterfacesaggressively
-overloadaggressively
-allowaccessmodification

# Debug info

-printusage
-whyareyoukeeping class iDiamondhunter.**
#-printconfiguration proguardDebug.txt
