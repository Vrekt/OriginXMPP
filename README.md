# Origin
A library for interacting with Origins XMPP service, chat with players, send/accept friend requests and more!

# Features
- Support for game presences, have custom text as your status!
- Support for chatting with players.
- Support for friends, listen for friend requests, send them, or accept one!
- Everything else `Nadir` supports.

# Credits
Origin uses `Nadir` made by [RobertoGraham](https://github.com/RobertoGraham/nadir), go check it out!

# WIP
This project is still a work in progress, expect more things to be soon!

# Initializing and connecting
You can either intialize an `Origin` instance with a username and password, or with an already built instance of `Nadir`.

```java
final var origin = Origin.newOrigin(username, password);
origin.connect();
```

```java
final var origin = Origin.newOrigin(nadir);
origin.connect();
```

# Send/reply to messages
```java
origin.chat().addChatListener(message -> {
    System.out.println("Message: " + message.getMessage());
    message.reply("abc");
});
```

# Listeners for when somebody is typing
```java
 origin.chat().addChatListener(new ChatListener() {
            @Override
            public void onMessageReceived(Message message) {
                System.err.println(message.getMessage());
            }

            @Override
            public void onTyping(EntityBareJid from) {
                System.err.println("typing");
            }
});
```

# Send a friend request
```java
try {
    origin.getNadir().accounts().findAllBySearchTerms("vrektwastaken")
        .flatMap((final var list) -> list.stream()
            .findFirst()).ifPresent(account -> origin.friend().sendFriendRequest(account.userId()));
    } catch (final IOException exception) {
        exception.printStackTrace();
    }
```

# Accept one
```java
origin.friend().acceptFriendRequest(1008999560409L);
```

# Friend listeners
```java
origin.friend().addFriendListener(new FriendListener() {
    @Override
    public void onFriendRequestAccepted(Long userId) {
        System.out.println("Accepted!");
       }
});
```

# Presence
```
origin.presence().setGameTextPresence(new GameTextPresence("Your text here", "Your text here"));
```
