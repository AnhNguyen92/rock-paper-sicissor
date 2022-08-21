package rockpaperscissor;

import java.io.IOException;
import java.net.ServerSocket;

/*
 *  Server lắng nghe ở cổng 4242, chờ đợi 2 clients kết nối và ghép cặp họ, tạo
 *  1 game giữa hai người
 */

public class Server {

    // Khởi tạo
    ServerSocket listener = null;
    private static int SERVER_PORT = 4242;

    /*
     * Constructor: Xét server lắng nghe ở cổng 4242 được khai báo bởi biến toàn cục.
     */
    public Server(int port) throws IOException {
        try {
            listener = new ServerSocket(port);
            System.out.println("Server đang chạy!");

        } catch (IOException e) {
            System.err.printf("Server: Không thể kết nối từ cổng: %d.", port);
            System.exit(-1);
        }
        ServerLoop();
    }

    /*
     * ServerLoop: chạy liên tục, chấp nhận kết nối từ client thông qua server socket, bắt đầu các thread cho mỗi người chơi
     * khi cả hai kết nối
     */
    public void ServerLoop() throws IOException {
        try {
            while (true) {
                Game game = new Game();

                Game.Player player1 = game.new Player(listener.accept(), '1');
                Game.Player player2 = game.new Player(listener.accept(), '2');

                player1.setOpponent(player2);
                player2.setOpponent(player1);

                game.currentPlayer = player1;

                player1.start();
                player2.start();
            }
        } finally {
            listener.close();
        }
    }

    // hàm main
    public static void main(String[] args) throws Exception {
        new Server(SERVER_PORT);
    }
}