import discord
from discord.ext import commands
import json
import os

# Impostazioni per il bot
prefix = "!"
level_up_threshold = 100

# Carica i dati dei livelli dei giocatori (se esiste)
if os.path.exists("player_levels.json"):
    with open("player_levels.json", "r") as f:
        player_levels = json.load(f)
else:
    player_levels = {}

bot = commands.Bot(command_prefix=prefix)

@bot.event
async def on_ready():
    print(f'Logged in as {bot.user.name}')

@bot.command()
async def level(ctx):
    user = ctx.author
    level = player_levels.get(str(user.id), 0)
    await ctx.send(f"{user.name} è al livello {level}!")

@bot.event
async def on_message(message):
    if not message.author.bot:
        # Controlla se l'autore del messaggio è nel dizionario dei giocatori
        user_id = str(message.author.id)
        if user_id not in player_levels:
            player_levels[user_id] = 0

        # Aggiungi un punto esperienza all'utente
        player_levels[user_id] += 1

        # Controlla se l'utente ha raggiunto il livello successivo
        if player_levels[user_id] % level_up_threshold == 0:
            await message.channel.send(f"Congratulazioni, {message.author.mention}, sei passato al livello {player_levels[user_id] // level_up_threshold}!")

        # Salva i dati dei livelli dei giocatori
        with open("player_levels.json", "w") as f:
            json.dump(player_levels, f)

    await bot.process_commands(message)

# Avvia il bot
bot.run('YOUR_DISCORD_BOT_TOKEN')
