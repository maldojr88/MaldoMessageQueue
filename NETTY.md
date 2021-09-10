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
    - open connection to a file (socket, device, file etc.) that is capable of doing I/O
- callbacks
    - a method to be called later. Used to notify. ChannelHandler interface
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
with an EventLoop for it's entire lifetime.
Channel LifeCytle:
- ChannelRegistered
- ChannelActive
- ChannelInactive
- ChannelUnregistered

### EventLoop
Core abstraction for handling events

![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/eventloop.jpeg)

EventLoop binds to a single Thread. Can be assigned to more than one Channel

![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/eventloop2.jpeg)

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

In fact, SimpleChannelInboundHandler is the class you will typically extend

![alt text](https://github.com/maldojr88/MaldoMessageQueue/blob/main/notes/channelrelationship.jpeg)

ChannelPipeline is a container for a chain of ChannelHandlers. ChannelInitializer will
create a custom ChannelPipeline when a channel is created. 

ChannelHandlers typically provide the following:
1. Transforming data from one format to another (encoders/decoders)
2. Providing notification of Exceptions
3. Providing notification of a Channel becoming active/inactive
4. Provide notification when a Channel is registered/deregistered from EventLoop
5. Providing notification about user-defined events
ChannelHandlerAdaptors are provide basic implementations to help you write your custom Channel Handlers

IdleStateHandler - for idle connections

ChannelHandlerContext - allow ChannelHandlers in a ChannelPipeline to communicate


ZeroCopy is a Linux feature that allows you to copy data from the filesystem
to the network without copying to user space. This functionality is serviced
by the epoll Linux system call

ByteBuf is an API on top of ByteBuffer to allow for easier usage. You can use
a version that is allocated on the heap or one that is allocated off the heap. PooledByteBufAllocator and
UnpooledByteBufAllocator are the two main classes to allocate ByteBuf. The former 
is based on jemalloc to help improve performance and minimize memory fragmentation.
Unpooled and ByteBufUtil are utility classes to manipulate ByteBufs. hexdump is a popular
function as it eases debugging of byte data.

Bootstrapping

Connecting all the components together to form your networking application
Server devotes a parent channel to accepting connections from clients and creating child channels for 
conversing with them. Add multiple ChannelHandlers by implementing your own ChannelInitializer class
and passing that into the bootstrap process

Client will most likely require only a single, non-parent channel for all network interactions


#### Testing
EmbeddedChannel use it to facilitate in testing ChannelHandlers


#### Encoders and Decoders
Encoders and Decoders are classes to convert binary to Java object and vice versa.
ByteToMessageDecoder - main abstract class for decoders. ReplayingDecoder is also a class worth looking at

ByteToMessageDecoder - same for encoders.

CombinedChannelDuplexHandler - combines an encoder and a decoder into 1

Serialization Options
- JDK serialization
- JBoss Marshalling (faster and more compact than JDK)
- Protocol Buffers

Another alternative for Java Serialization is Protocol Buffers

#### TLS
SSLContext and SSLEngine are the Java APIs for SSL. Netty leverages these API's to secure communication.
SslHandler is the netty handler for performing this type of work. Netty also provides OpenSslEngine
which is its own implementation of SSLEngine with "added performance". SSLChannelInitializer can be 
used to add the SSLHandler to the ChannelPipeline

Websocket protocol
https://datatracker.ietf.org/doc/html/rfc6455

Links
---
https://netty.io/wiki/user-guide-for-4.x.html
https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example
