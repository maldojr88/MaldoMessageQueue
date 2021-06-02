Netty
======

Notes from book "Netty in Action"

Chapter 1
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
