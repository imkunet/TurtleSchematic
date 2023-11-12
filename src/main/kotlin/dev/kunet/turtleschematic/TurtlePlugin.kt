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

    private var intermediate: TurtleIntermediate? = null

    @EventHandler
    fun awa(e: PlayerCommandPreprocessEvent) {
        if (!e.player.isOp) return

        if (!e.message.startsWith("/awa ") && !e.message.equals("/awa")) return
        e.isCancelled = true

        e.player.sendMessage("sup")
        runBlocking {
            val inter = if (intermediate == null || e.message.contains("force")) {
                val a = now()
                val schem = TurtleSchematic(File("test.schematic"))
                schem.startReading()
                val b = now()
                e.player.sendMessage("loaded in ${b - a}ms (fully async)")
                val inter = schem.createIntermediaryFromWEOrigin(0, 93, 0)
                val c = now()
                e.player.sendMessage("inter in ${c - b}ms (fully async)")
                intermediate = inter
                inter
            } else {
                intermediate!!
            }

            val c = now()
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

    @EventHandler
    fun bawa(e: PlayerCommandPreprocessEvent) {
        if (!e.player.isOp) return
        if (!e.message.equals("/bawa")) return

        e.isCancelled = true

        e.player.sendMessage("BOOM")
        val a = now()
        TurtleTools.fastClearChunk(e.player.world, e.player.location.blockX shr 4, e.player.location.blockZ shr 4)
        e.player.sendMessage("done in ${now() - a}ms")
    }

    @EventHandler
    fun cawa(e: PlayerCommandPreprocessEvent) {
        if (!e.player.isOp) return
        if (!e.message.equals("/cawa")) return

        e.isCancelled = true

        e.player.sendMessage("BOOM")
        val a = now()
        val view = TurtleView(e.player.world)
        val x = e.player.location
        view.setBlock(x.blockX, x.blockY, x.blockZ, 159, 11)
        e.player.sendMessage("done in ${now() - a}ms")
    }
}
