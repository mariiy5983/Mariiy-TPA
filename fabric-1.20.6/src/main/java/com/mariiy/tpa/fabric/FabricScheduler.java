package com.mariiy.tpa.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

/**
 * Simple delayed task runner on the server tick thread.
 */
final class FabricScheduler {

    private static final Map<UUID, Task> TASKS = new ConcurrentHashMap<>();
    private static boolean registered;

    private FabricScheduler() {
    }

    static synchronized void ensureRegistered() {
        if (registered) {
            return;
        }
        registered = true;
        ServerTickEvents.END_SERVER_TICK.register(FabricScheduler::tick);
    }

    static UUID schedule(Runnable task, long delayTicks, BooleanSupplier cancelled) {
        ensureRegistered();
        UUID id = UUID.randomUUID();
        TASKS.put(id, new Task(Math.max(0, delayTicks), task, cancelled));
        return id;
    }

    static void cancel(Object handle) {
        if (handle instanceof UUID id) {
            TASKS.remove(id);
        }
    }

    private static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Task>> it = TASKS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Task> e = it.next();
            Task t = e.getValue();
            if (t.cancelled.getAsBoolean()) {
                it.remove();
                continue;
            }
            if (t.left-- <= 0) {
                it.remove();
                try {
                    t.task.run();
                } catch (Throwable ex) {
                    MariiyTpaFabric.LOGGER.error("TPA scheduled task failed", ex);
                }
            }
        }
    }

    private static final class Task {
        long left;
        final Runnable task;
        final BooleanSupplier cancelled;

        Task(long left, Runnable task, BooleanSupplier cancelled) {
            this.left = left;
            this.task = task;
            this.cancelled = cancelled;
        }
    }
}
