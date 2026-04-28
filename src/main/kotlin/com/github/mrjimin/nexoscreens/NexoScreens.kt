package com.github.mrjimin.nexoscreens

import com.github.mrjimin.nexoscreens.command.CommandHandler
import com.github.mrjimin.nexoscreens.listener.EventListener
import com.github.mrjimin.nexoscreens.manager.ScreenManager
import me.clip.placeholderapi.metrics.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class NexoScreens : JavaPlugin() {

    private lateinit var screenManager: ScreenManager
    private lateinit var commandHandler: CommandHandler
    private lateinit var eventListener: EventListener

    companion object {
        lateinit var INSTANCE: NexoScreens
            private set
    }

    override fun onEnable() {
        INSTANCE = this
        Metrics(this, 25025)

        screenManager = ScreenManager(this)
        commandHandler = CommandHandler(screenManager)
        eventListener = EventListener(screenManager)

        server.pluginManager.registerEvents(eventListener, this)
        server.scheduler.runTaskTimer(this, screenManager::tickScreens, 0, 1)

        server.commandMap.register("nexoscreens", commandHandler)

        screenManager.reload()
    }

    override fun onDisable() {
        screenManager.clear()
    }
}