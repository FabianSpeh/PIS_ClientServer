package pis.hue2.server;

public class launchServer{
    public static void main(String[] args){
        /**
        * In der main-Methode der launchServer-Klasse
        * wird lediglich der ServerThread angestossen.
        * @param args String []args
        */
        new Thread(new Server(3000)).start();
    }
}