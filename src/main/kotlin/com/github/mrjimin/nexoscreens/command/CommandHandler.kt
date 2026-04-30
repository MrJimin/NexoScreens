package com.github.mrjimin.nexoscreens.command

import com.github.mrjimin.nexoscreens.manager.ScreenManager
import com.github.mrjimin.nexoscreens.util.toMMComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler(private val screenManager: ScreenManager) : Command("nexoscreens") {

    init {
        aliases = listOf("ns")
        description = "NexoScreens main command"
        permission = "nexoscreens.command"
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.hasPermission("nexoscreens.command")) return false

        if (args.isEmpty()) {
            sender.sendMessage(("<gradient:#9055FF:#13E2DA>NexoScreens <gray>| <#b8bbc2>Available commands\n" +
                    "<hover:show_text:\"<gray>fill reload command\"><click:SUGGEST_COMMAND:/nexoscreens reload><#6f737d>/nexoscreens <#b8bbc2>reload</click></hover>  <#6f737d>» <#b8bbc2>plugin reload\n" +
                    "<hover:show_text:\"<gray>fill show command\"><click:SUGGEST_COMMAND:/nexoscreens show ><#6f737d>/nexoscreens <#b8bbc2>show <#43c9fa>args</click></hover>  <#6f737d>» <#b8bbc2>show screens")
                .toMMComponent())
            return false
        }

        when (args[0]) {
            "reload" -> {
                screenManager.reload()
                sender.sendMessage("<gradient:#9055FF:#13E2DA>NexoScreens <gray>| <gray>Screens reloaded!".toMMComponent())
                return true
            }
            "show" -> {
                if (args.size < 7) return sender.usage()
                val players = parsePlayers(args[1]) ?: return sender.usage()
                val screen = screenManager.getScreen(args[2]) ?: return sender.usage()
                val fadeInTicks = args[3].toLongOrNull() ?: return sender.usage()
                val stayTicks = args[4].toLongOrNull() ?: return sender.usage()
                val fadeOutTicks = args[5].toLongOrNull() ?: return sender.usage()
                val color = TextColor.fromHexString(args[6]) ?: return sender.usage()
                val text = if (args.size < 8) Component.empty() else MiniMessage.miniMessage().deserialize(args.copyOfRange(7, args.size).joinToString(" "))

                screenManager.show(players, screen, color, text, fadeInTicks, stayTicks, fadeOutTicks)
                sender.sendMessage("<gradient:#9055FF:#13E2DA>NexoScreens <gray>| <gray>Showing screen!".toMMComponent())
                return true
            }
        }
        return false
    }

    private fun parsePlayers(rawPlayer: String): MutableSet<Player>? {
        val players = mutableSetOf<Player>()
        return if (rawPlayer == "*") {
            players.addAll(Bukkit.getOnlinePlayers())
            players
        } else {
            Bukkit.getPlayer(rawPlayer)?.let { players.add(it); players }
        }
    }

    private fun CommandSender.usage(): Boolean {
        sendMessage("<hover:show_text:\"<gray>fill show command\"><click:SUGGEST_COMMAND:/nexoscreens show ><#6f737d>/ns <#b8bbc2>show <#43c9fa>player <#fa8943>screen <#43fa8c>fadeIn stay fadeOut <#faf443>color <#fa4362>text</click></hover>".toMMComponent())
        return false
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        when (args.size) {
            1 -> list.addAll(listOf("show", "reload"))
            2 -> if (args[0] == "show") list.run {
                addAll(Bukkit.getOnlinePlayers().map { it.name })
                add("*")
            }
            3 -> if (args[0] == "show") list.addAll(screenManager.getScreenKeys())
            4, 5, 6 -> if (args[0] == "show") list.addAll(listOf("5", "10", "20", "40", "60", "80", "100"))
            7 -> if (args[0] == "show") list.addAll(listOf("#0000ff", "#000000", "#FFFFFF"))
            8 -> if (args[0] == "show") list.add("<green>Test")
        }
        list.removeIf { !it.startsWith(args.last()) }
        return list
    }
}