package net;

//Might not need the alias if I can keep a reference to the Channel object
public enum MessageType {
    CONNECT_TO_PUBLISH(1),  // 1B - msgType, 7B - alias
    CONNECT_TO_CONSUME(2),  // 1B - msgType, 7B - alias
    PUBLISH(3),             // 1B - msgType, 30B - msg
    DISCONNECT(4),          // 1B - msgType, 7B - alias
    CREATE_QUEUE(5),        // 1B - msgType, 7B - queueName
    READ(6);                // 1B - msgType

    private final int type;

    MessageType(int type){
        this.type = type;
    }

    public static MessageType from(int type){
        return switch (type){
            case 1 -> CONNECT_TO_PUBLISH;
            case 2 -> CONNECT_TO_CONSUME;
            case 3 -> PUBLISH;
            case 4 -> DISCONNECT;
            case 5 -> CREATE_QUEUE;
            case 6 -> READ;
            default -> throw new RuntimeException("Unknown type " + type);
        };
    }

    public int getType(){
        return type;
    }
}
