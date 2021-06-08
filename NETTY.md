Netty
======

Notes from book "Netty in Action"

Overview/History
-----

Old way(java.net) - blocking I/O <br>
accept() - blocks until a connection is made <br>

To handle multiple connections, need to allocate a new Thread per client socket. This is bad for the following reasons:

- Most threads would probably be blocking most of the time (wasted resources)
- Each thread requires stack space (wasted resources) (64KB - 1MB depending on the OS)
- Context switching within threads becomes a bottle neck (mayber after 10k threads)

![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/onesockperthread.jpeg)

NIO (non-blocking I/O) <br>
selector() - hooks into the event notification API of the OS. Allows 1 Thread to handle more than 1 connection

Main Building blocks

- channel
    - open connection to ta file (socket, device, file etc.) that is capable of doing I/O
- callbacks
    - a method ot be called later. Used to notify. ChannelHandler interface
- future
    - another mechanism for notification (similar to callback)
- events
    - Active/Inactive connection, Data reads, User events, Error events

![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/onethreadmultsock.jpeg)

under the covers netty is using an EventLoop

Chapter2 
------

Main components of a Netty server:
- At least 1 ChannelHandler
- Bootstrap startup up code. Bind to a port, configure channel to notify your class on messages

Architecture
------
Main architecture is composed of the following abstractions:
- Channel - Socket
- EventLoop - Control flow, multithreading, concurrency
- ChannelFuture - Asynchronous notifications

### Channel
Provides an API on top of native Java Socket class to simplify. A channel registers
with an EventLoop for it's entire lifetime

### EventLoop
Core abstraction for handling events
![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/eventloop.jpeg)
EventLoop binds to a single Thread. Can be assigned to more than one Channel

### ChannelFuture
Placeholder for the results of an operation

Development
------
From an App development perspective, the primary component is the ChannelHandler.
It serves as the container for application logic, handling inbound and outbound data.

ChannelInboundHandler and ChannelOutboundHandler are frequently implemented interfaces.
On a practical basis, since there are many events you don't care about and might not want to
implement, you have the ChannelOutboundHandlerAdapter and ChannelInboundHandlerAdapter which you
can extend and only override the methods you're interested in.

In fact, SimpleChannelInboundHandler is the class you will typcially extend

ChannelPipeline is a container for a chain of ChannelHandlers. ChannelInitializer will
create a custom ChannelPipeline when a channel is created.

Encoders and Decoders are classes to convert binary to Java object and vice versa.
