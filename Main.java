

public class Main {

    public static void main(String[] args) {
        MyClientSocket client = new MyClientSocket(args[0]);
        client.httpGetRequest();

        client.parseStatusCode();
        client.parseHeader();

        client.handleStatusCode();
        client.printDate('a');
        client.printDate('l');
        client.handleBody();

        client.clean();
    }
}
