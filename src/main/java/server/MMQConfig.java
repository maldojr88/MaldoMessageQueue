package server;

import java.nio.file.Path;

public record MMQConfig(Path catalogDir, int port) {
}
