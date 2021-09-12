package server;

import java.nio.file.Path;

public record MMQConfig(Path serverDir, Path catalogDir, Path queuesDir , int port) {
}
