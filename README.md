# Messenger
 Web chat using web stockets. Allows to send text, photos and music tracks (.wav).
 Multimedia messages are converted to byte arrays and sent to every observer through web socket.
 After receiving such message user can decide wheter he wants to save it somewhere or reject it.

There is also programme that acts as local server that forwards messages and can be observed by client-app.

#### Back-end: Java, Front-end: JFrame

##### Implements: Observer, Chain and Template Design Pattern

