package rockpaperscissor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

public class Game {

    // Các biến thành viên
    Player currentPlayer;
    int count = 0;
    int p1, p2;

    // Containers
    private HashSet<String> names = new HashSet<String>();
    private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /*
     * result: trả về kết quả game dạng chuỗi.
     */
    public String result(int player, int hand) {
        String score = null;
        count += 1;
        if (count == 1) {
            p1 = hand;
        } else if (count == 2) {
            p2 = hand;
            count = 0;
            // Kiểm tra người thắng cuộc
            score = hasWinner1() ? "P1 Thắng" : hasWinner2() ? "P2 Thắng" : hasDraw() ? "Hòa" : "Ai đó không chọn";
        }
        return score;
    }

    /*
     * hasDraw: Kiểm tra xem trận đấu hòa hay có thắng thua.
     */
    public boolean hasDraw() {
        boolean isDraw = false;
        if (p1 == p2) {
            isDraw = true;
        }
        return isDraw;
    }

    /*
     * hasWinner1: Kiểm tra xem người 1 có thắng hay không
     */
    public boolean hasWinner1() {
        boolean isWinner1 = false;
        if (p1 == 1 && p2 == 3 || p1 == 2 && p2 == 1 || p1 == 3 && p2 == 2) {
            isWinner1 = true;
        }
        return isWinner1;
    }

    /*
     * hasWinner2: Kiểm tra xem người 2 có thắng hay không
     */
    public boolean hasWinner2() {
        boolean isWinner2 = false;
        if (p1 == 1 && p2 == 2 || p1 == 2 && p2 == 3 || p1 == 3 && p2 == 1) {
            isWinner2 = true;
        }
        return isWinner2;
    }

    /*
     * legalMove: Kiểm tra xem người chơi đã thực hiện lựa chọn chưa?
     */
    public synchronized boolean legalMove(int hand, Player player) {
        boolean isLegalMove = false;
        if (player == currentPlayer && hand >= 0 && hand <= 3) {
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerPlayed(hand);
            isLegalMove = true;
        }
        return isLegalMove;
    }

    /*
     * Class Player: Class inner xử lý các giao tiếp giữa client và server. Ví dụ như tin nhắn, lựa chọn khi chơi game, ...
     */
    public class Player extends Thread {

        // Các biến thành viên
        char number;
        String name;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /*
         * Constructor: Khởi tạo thông tin của mỗi client, cài đặt input/output cho các giao tiếp giữa client và server.
         */
        public Player(Socket socket, char number) {
            this.socket = socket;
            this.number = number;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // Lấy tên của client
                while (true) {
                    output.println("SUBMITNAME");
                    name = input.readLine();
                    if (name == null) {
                        return;
                    }

                    // Thêm số 1 để tránh 2 người trùng tên
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        } else {
                            name = name + "1";
                            names.add(name);
                            break;
                        }
                    }
                }

                // Chấp thuận client
                output.println("NAMEACCEPTED");
                writers.add(output);

                output.println("RULE - Đây là một game oẳn tù tì giành cho hai người chơi.");
                output.println("RULE - Các bạn hãy sử dụng khung chát để giao tiếp thêm.");
                output.println("RULE - Nói chuyện văn minh, lịch sự. Chúc các bạn 1 ngày vui vẻ!");
                output.println("WELCOME Chào mừng người chơi " + number);
                if (number % 2 == 1) {
                    output.println("MESSAGE Đợi người chơi thứ 2 kết nối");
                }
            } catch (IOException e) {
                System.out.println("Người chơi đã rời khỏi game.");
                // Broadcast player has left the game to the other client
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + "Đã rời khỏi game.\nTạo 1 game mới.");
                }
            }
        }

        /*
         * Phương thưc run: Liên tục đọc dữ liệu được truyền giữa clients và server.
         */
        public void run() {
            try {
                // Thông báo cả 2 người chơi đã kết nối.
                output.println("MESSAGE Mọi người chơi đã tham gia.");

                // Nhắc cho người chơi 1 lựa chọn lượt của mình
                if (number == '1') {
                    output.println("MESSAGE Chọn lượt của bạn.\n");
                    output.println("PLAYER1"); // Enables the pickHand button for player1
                }

                // Nhắc cho người chơi 2 lựa chọn lượt của mình
                if (number == '2') {
                    output.println("MESSAGE Đợi đối thủ chọn xong.\n");
                }

                // Nhận yêu cầu, tin nhắn từ client và xử lý.
                while (true) {
                    String command = input.readLine();
                    if (input == null) {
                        return;
                    }

                    if (command.startsWith("MOVE")) {
                        int hand = Integer.parseInt(command.substring(4));
                        if (legalMove(hand, this)) {
                            // lấy kết quả chọn của người 1
                            if (number == '1') {
                                result(number, hand);
                            } else if (number == '2') {
                                // Lấy kết quả
                                String victor = result(number, hand);
                                for (PrintWriter writer : writers) {
                                    writer.println("RESULT" + victor);
                                }
                            }
                        }
                    }
                    // Gửi tin nhắn đến người chơi
                    else {
                        for (PrintWriter writer : writers) {
                            writer.println("MESSAGE " + name + ": " + command);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Người chơi đã rời trò chơi.");
                // Thông báo người chơi đã rời game tới người còn lại
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE Người chơi 1 đã rời trò chơi.\n");
                }

            } catch (NullPointerException e) {
                System.err.println("Người chơi đã rời trò chơi.");
                // Thông báo người chơi đã rời game tới người còn lại
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " đã rời trò chơi.\n");
                }

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        /*
         * setOpponent: Xét đối thủ
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        /*
         * otherPlayerPlayed: Thông báo lượt chọn tới đối thủ
         */
        public void otherPlayerPlayed(int hand) {
            output.println("OPPONENT_PLAYED" + hand);
        }

    }
}