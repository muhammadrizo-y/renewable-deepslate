// Compile-time stub of net.neoforged.fml.common.Mod (NeoForge fancymodloader).
// Real annotation also has dist() and depends() with defaults; only value() is required.
package net.neoforged.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    String value();
}
