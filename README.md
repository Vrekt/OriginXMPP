# Origin
A library for interacting with Origins XMPP service, chat with players, send/accept friend requests and more!

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
origin.chat().addMessageListener(message -> {
    System.out.println("Message: " + message.getMessage());
    message.reply("abc");
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

# Friend listeners
```java
origin.friend().addFriendListener(new FriendListener() {
    @Override
    public void onFriendRequestAccepted(Long userId) {
        System.out.println("Accepted!");
       }
});
```
