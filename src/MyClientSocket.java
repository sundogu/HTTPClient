import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class MyClientSocket {
    // Class Variables //
    private final int SERVER_PORT = 80;
    private Socket socket;
    private Scanner scanner;
    private String hostname, filePath;
    private int statusCode;
    private Map<String, String> header;


    // Constructors //
    public MyClientSocket(String serverURL) {
        processUrlRequest(serverURL);
        initialize(serverURL);
    }

    // Methods //
    public void initialize(String serverURL) {
        initSocket(serverURL);
        initScanner();

        header = new HashMap<>();
    }
    public void httpGetRequest() {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            // send a GET HTTP request to the web server
            pw.println(String.format("GET %s HTTP/1.1", filePath));
            pw.println(String.format("Host: %s:%d", hostname, SERVER_PORT));
            pw.println("Connection: Close");
            pw.println();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

    }

    private static void processUrlRequest(String serverURL) {
        System.out.println(String.format("URL Requested: %s", serverURL));
    }

    private InetAddress handleURL(String serverURL) throws MalformedURLException, UnknownHostException {
        hostname = new URL(serverURL).getHost();
        filePath = new URL(serverURL).getFile();
        filePath = (filePath.equals(""))? "/" : filePath;

        InetAddress serverAddress = InetAddress.getByName(hostname);

        if (serverURL.toLowerCase().startsWith("https")) {
            System.out.println("HTTPS Not Supported");
            System.exit(0);
        }

        return serverAddress;
    }

    private void initSocket(String serverURL) {
        try {
            InetAddress serverAddress = handleURL(serverURL);
            socket = new Socket(serverAddress, SERVER_PORT);

            System.out.println(String.format("Client: %s %s"
                    , socket.getLocalAddress().getHostAddress(), socket.getLocalPort()));
            System.out.println(String.format("Server: %s %s"
                    , serverAddress.getHostAddress(), socket.getPort()));

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private void initScanner() {
        try {
            scanner = new Scanner(new BufferedReader(
                    new InputStreamReader(socket.getInputStream())));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    public void parseStatusCode() {
        burnNext('w', 1);
        statusCode = scanner.nextInt();
    }


    public void parseHeader() {
        burnNext('l', 1);

        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            String[] temp = line.split(": ", 2);
            header.put(temp[0], temp[1]);
        }
    }

    public void handleStatusCode() {
        if (statusCode >= 400 && statusCode <= 599) {
            System.out.println(String.format("Retrieval Failed (%d)", statusCode));
            System.exit(0);
        } else if (statusCode == 301 || statusCode == 302) {
            String permTemp = (statusCode == 301)? "permanently" : "temporarily";
            System.out.println(String.format("Resource %s moved to %s", permTemp, header.get("Location")));

            initialize(header.get("Location"));
            httpGetRequest();
            parseStatusCode();
            parseHeader();
            handleStatusCode();
        } else {
            System.out.println("Retrieval Successful");
        }
    }

    public void printDate(char mode) {
        switch (mode) {
            case 'a': {
                System.out.println(String.format("Date Accessed: %s"
                        , MyDateParser.parseDate(header.get("Date"))));
                break;
            }case 'l': {
                if (header.get("Last-Modified") == null) {
                    System.out.println("Last Modified not available");
                } else {
                    System.out.println(String.format("Last Modified: %s"
                            , MyDateParser.parseDate(header.get("Last-Modified"))));
                }
            }
        }
    }

    private String getFileType() {
        switch(header.get("Content-Type")) {
            case "text/plain": {
                return ".txt";
            }case "text/html": {
                return ".html";
            }case "text/css": {
                return ".css";
            }case "application/json": {
                return ".json";
            }case "application/octet-stream": {
                return "";
            }default: {
                return ".js";
            }
        }
    }

    public void handleBody() {
        try {
            FileOutputStream file = new FileOutputStream("output" + getFileType());
            PrintWriter writer = new PrintWriter(file);

            while ((scanner.hasNextLine())) {
                writer.println(scanner.nextLine());
            }

            writer.close();
            file.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void clean() {
        try {
            scanner.close();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private void burnNext(char mode, int num) {
        for (int i = 0; i < num; i++) {
            String burner = (mode == 'l')? scanner.nextLine() : scanner.next();
        }
    }


}
