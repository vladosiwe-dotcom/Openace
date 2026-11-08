package com.vladosiwe.openace;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server config (config/openace-server.toml in the world's serverconfig). */
public final class OpenaceConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue EVERY_JOIN;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("awakening");
        ENABLED = b.comment("Сцена пробуждения при входе игрока на сервер (лёжа → открывает глаза → садится → встаёт).",
                "Сам игрок видит её от первого лица, остальные рядом — со стороны.").define("enabled", true);
        EVERY_JOIN = b.comment("true — при каждом входе на сервер, false — только при самом первом").define("everyJoin", true);
        b.pop();
        SPEC = b.build();
    }

    private OpenaceConfig() {}
}
