package dev.kunet.turtleschematic

import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TurtlePlugin : JavaPlugin(), Listener {
    override fun onEnable() {
        TurtleLibrary.initializeTurtle()
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun awa(e: PlayerCommandPreprocessEvent) {
        if (!e.message.startsWith("/awa")) return
        e.isCancelled = true

        e.player.sendMessage("sup")
        runBlocking {
            val a = now()
            val schem = createTurtleSchematic(File("test.schematic"))
            if (schem == null) {
                e.player.sendMessage("broke :9")
                return@runBlocking
            }
            val b = now()
            e.player.sendMessage("loaded in ${b - a}ms (fully async)")
            val inter = schem.createIntermediary(0, 1, 0)
            val c = now()
            e.player.sendMessage("inter in ${c - b}ms (fully async)")
            val imed = TurtleIntermediateEdit(e.player.world, inter)
            var i = 0
            while (!imed.poll()) {
                e.player.sendMessage("pollage $i")
                i++
            }
            val d = now()
            e.player.sendMessage("placed in ${d - c}ms (BLOCKING)")

        }
    }
}
