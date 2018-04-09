# MiguelBridge
Bridge Telegram - Matrix

Using https://github.com/rubenlagus/TelegramBots


## Installation
1. Create a folder called "files" in the working directory
2. Copy `sample_botsettings.json` inside the new folder and call it `botsettings.json`
3. Edit the new file, changing every parameter (keep every value as a string between quotes):
    1. `matrixhomeserver`: change only the address of the server and keep the rest (it should be like `https://something/_matrix/client/r0`)
    2. `tgtoken`: write the telegram bot token that @BotFather bot gives you
    3. `matrixuser`: write the username of the matrix bot (like `@username:server.com`)
    4. `matrixpswd`: write the password of the matrix bot
    
    Then, for every pair of room you want to link togheter:
    1. `matrixname`: write the name of the matrix room
    2. `tgname`: write the name of the Telegram room
    3. `tgid`: write the id of the telegram chat. You can get it sending the `/chatid` command to the Telegram Bot
    4. `matrixid` : write the id of the matrix room. You can get it from the room settings, under "internal id" (like `!something:server.com`)
    5. `lastmessageid`: this parameter is managed by the application, don't edit it
4. Start the program

Now the program should send every text message it receives from the telegram chat room to the linked matrix chat room and vice versa.
