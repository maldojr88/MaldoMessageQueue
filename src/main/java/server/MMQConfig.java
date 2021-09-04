package server;

import java.nio.file.Path;

public record MMQConfig(Path catalogDir, int port) {
    /*private String catalogDir;
    private int port;

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String getCatalogDir(){
        return catalogDir;
    }

    public void setCatalogDir(String catalogDir){
        this.catalogDir = catalogDir;
    }*/
}
