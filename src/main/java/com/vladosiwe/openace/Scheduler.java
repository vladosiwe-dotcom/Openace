package com.vladosiwe.openace;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Delayed server actions, run on the server thread. */
@EventBusSubscriber(modid = OpenAce.MODID)
public final class Scheduler {
    private record Task(long due, Runnable action) {}

    private static final List<Task> TASKS = new ArrayList<>();
    private static long now;

    private Scheduler() {}

    public static void later(int ticks, Runnable action) {
        TASKS.add(new Task(now + Math.max(1, ticks), action));
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        now++;
        List<Task> due = new ArrayList<>();
        for (Iterator<Task> it = TASKS.iterator(); it.hasNext(); ) {
            Task t = it.next();
            if (t.due <= now) { due.add(t); it.remove(); }
        }
        for (Task t : due) {
            try { t.action.run(); } catch (Exception e) { OpenAce.LOGGER.error("Openace: delayed action failed", e); }
        }
    }

    @SubscribeEvent
    public static void onStopped(ServerStoppedEvent event) {
        TASKS.clear();
    }
}
