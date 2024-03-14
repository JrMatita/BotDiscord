import discord
from discord.ext import commands

intents = discord.Intents.default()
intents.guild_messages = True
intents.guilds = True

bot = commands.Bot(command_prefix='!', intents=intents)

@bot.event
async def on_ready():
    print('Logged in as')
    print(bot.user.name)
    print(bot.user.id)
    print('------')

@bot.command()
async def new_ticket(ctx):
    guild = ctx.guild
    overwrites = {
        guild.default_role: discord.PermissionOverwrite(read_messages=False),
        ctx.author: discord.PermissionOverwrite(read_messages=True)
    }
    category = discord.utils.get(guild.categories, name="Tickets")
    if category is None:
        category = await guild.create_category(name="Tickets")

    channel = await category.create_text_channel(f"ticket-{ctx.author}", overwrites=overwrites)
    await channel.send(f"{ctx.author.mention} Ticket creato. Un membro del team lo prenderà in carico al più presto.")

@bot.command()
async def close_ticket(ctx):
    if isinstance(ctx.channel, discord.TextChannel) and ctx.channel.name.startswith("ticket-"):
        await ctx.channel.delete()
    else:
        await ctx.send("Questo comando può essere usato solo nei canali dei ticket.")

bot.run('YOUR_DISCORD_BOT_TOKEN')
