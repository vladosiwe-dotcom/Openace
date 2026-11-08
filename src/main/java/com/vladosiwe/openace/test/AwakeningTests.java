package com.vladosiwe.openace.test;

import com.mojang.authlib.GameProfile;
import com.vladosiwe.openace.AwakeningScene;
import com.vladosiwe.openace.OpenAce;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Game tests on a real dedicated server (CI). */
@GameTestHolder(OpenAce.MODID)
@PrefixGameTestTemplate(false)
public final class AwakeningTests {
    private AwakeningTests() {}

    private static ServerPlayer player(GameTestHelper h, String name) {
        ServerPlayer p = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
        p.moveTo(h.absoluteVec(new Vec3(1.5, 1, 1.5)));
        return p;
    }

    /** The scene starts, protects the player, is remembered, and ends after its time. */
    @GameTest(template = "empty")
    public static void scenePlaysAndEnds(GameTestHelper h) {
        ServerPlayer p = player(h, "openace_test");
        h.assertTrue(!p.getData(OpenAce.SEEN), "a new player has already seen the awakening");
        AwakeningScene.start(p);
        h.assertTrue(AwakeningScene.active(p), "the scene did not start");
        h.assertTrue(p.getData(OpenAce.SEEN), "the scene is not remembered");
        var res = p.getEffect(MobEffects.DAMAGE_RESISTANCE);
        h.assertTrue(res != null && res.getAmplifier() >= 4, "the player is not protected while lying");
        AwakeningScene.expireForTest(p);
        h.assertTrue(!AwakeningScene.active(p), "the scene did not end");
        h.succeed();
    }
}
