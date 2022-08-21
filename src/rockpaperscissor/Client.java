package rockpaperscissor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import layout.TableLayout;

public class Client implements ActionListener {
    // Các biến thành phần
    String host = "";
    BufferedReader input;
    PrintWriter output;
    int yourHand, oppoHand;
    int player;

    // Các biến thành phần Swing
    JFrame mainFrame;
    JPanel gamePanel, chatPanel, timePanel, imageResultPanel, radioPanel, btnFunctionPanel;
    JButton btnSignin, btnPick, btnLeave, btnReset;
    JTextArea txtaChat;
    JTextField txtUser, txtChat, txtTimer;
    JRadioButton rbtRock, rbtPaper, rbtScissors;
    JLabel lblUser, yourIcon, selectIcon, remindIcon, lblYou, lblOpponent, lblResult;
    ImageIcon notSelecdPic, remindPic, rockPic, paperPic, scissorsPic;

    /*
     * createDialog: khởi tạo dialog cho nhập thông tin kết nối,
     * lấy thông tin nhập từ client
     */
    private void createDialog() {
        JTextField hostname = new JTextField("localhost");
        final JComponent[] inputs = new JComponent[] { new JLabel("Nhập địa chỉ Server:"), hostname, };
        JOptionPane.showMessageDialog(null, inputs, "Server", JOptionPane.PLAIN_MESSAGE);
        host = hostname.getText();
    }

    /*
     * createComponent: Khởi tạo các thành phần Swing.
     */
    private void createComponents() {
        gamePanel = new JPanel();
        chatPanel = new JPanel();
        timePanel = new JPanel();
        imageResultPanel = new JPanel();
        radioPanel = new JPanel();
        btnFunctionPanel = new JPanel();

        notSelecdPic = new ImageIcon(getClass().getResource("images/notselect.jpg"));
        remindPic = new ImageIcon(getClass().getResource("images/remind.jpg"));
        rockPic = new ImageIcon(getClass().getResource("images/rock.jpg"));
        paperPic = new ImageIcon(getClass().getResource("images/paper.jpg"));
        scissorsPic = new ImageIcon(getClass().getResource("images/scissors.jpg"));

        btnSignin = new JButton("Đăng nhập");
        btnSignin.addActionListener(this);
        btnPick = new JButton("Chọn");
        btnPick.addActionListener(this);
        btnPick.setEnabled(false);
        btnReset = new JButton("Reset");
        btnReset.setEnabled(false);
        btnReset.addActionListener(this);
        btnLeave = new JButton("Thoát");
        btnLeave.addActionListener(this);

        txtTimer = new JTextField("Thời gian");
        txtTimer.setHorizontalAlignment(JTextField.CENTER);
        txtTimer.setFont(new Font("Arial", Font.BOLD, 32));
        txtTimer.setEditable(false);
        txtTimer.addActionListener(this);

        yourIcon = new JLabel(remindPic);
        selectIcon = new JLabel(notSelecdPic);
        selectIcon.setVisible(false);
        remindIcon = new JLabel(notSelecdPic);
        lblYou = new JLabel("Bạn", SwingConstants.CENTER);
        lblYou.setLabelFor(yourIcon);
        lblOpponent = new JLabel("Đối thủ", SwingConstants.CENTER);
        lblOpponent.setLabelFor(selectIcon);
        lblResult = new JLabel("", SwingConstants.CENTER);

        rbtRock = new JRadioButton("Búa");
        rbtRock.addActionListener(this);
        rbtPaper = new JRadioButton("Giấy");
        rbtPaper.addActionListener(this);
        rbtScissors = new JRadioButton("Kéo");
        rbtScissors.addActionListener(this);
        rbtRock.setEnabled(false);
        rbtPaper.setEnabled(false);
        rbtScissors.setEnabled(false);
        ButtonGroup rbtGroup = new ButtonGroup();
        rbtGroup.add(rbtRock);
        rbtGroup.add(rbtPaper);
        rbtGroup.add(rbtScissors);

        txtaChat = new JTextArea("", 25, 20);
        txtaChat.setEditable(false);
        txtaChat.setLineWrap(true);
        txtaChat.setWrapStyleWord(true);
        txtUser = new JTextField(20);
        txtUser.addActionListener(this);
        txtChat = new JTextField(20);
        txtChat.addActionListener(this);
        txtChat.setEditable(false);
        lblUser = new JLabel("User: ");
        lblUser.setLabelFor(txtUser);
        lblUser.setHorizontalAlignment(4);
    }

    /*
     * createFrame: Khởi tạo frame và xét layout cho GUI(Giao diện người dùng) .
     */
    private void createFrame() {
        mainFrame = new JFrame("Game Oản tù tì");
        mainFrame.setMinimumSize(new Dimension(600, 300));
        mainFrame.setResizable(false);

        // Định nghĩa Layouts
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[][] t1 = { { 3, f, 3 }, // columns
                { 3, p, 3, f, 3, p, 3, p, 3 } // rows
        };

        double[][] t2 = { { 3, p, 3, 100, 3, p, 3 }, // columns
                { 3, p, 25, p, 3 } // rows
        };

        double[][] t3 = { { 3, p, 3, f, 3, p, 3 }, // columns
                { 3, p, 5, f, 5, p, 3 } // rows
        };

        TableLayout layout1 = new TableLayout(t1);
        TableLayout layout2 = new TableLayout(t2);
        TableLayout layout3 = new TableLayout(t3);

        // Phần game
        timePanel.add(txtTimer);

        imageResultPanel.setLayout(layout2);
        imageResultPanel.add(yourIcon, "1,1");
        imageResultPanel.add(selectIcon, "5,1");
        imageResultPanel.add(remindIcon, "5,1");
        imageResultPanel.add(lblResult, "3,1");
        imageResultPanel.add(lblYou, "1,3");
        imageResultPanel.add(lblOpponent, "5,3");

        radioPanel.setLayout(new FlowLayout());
        radioPanel.add(rbtRock);
        radioPanel.add(rbtPaper);
        radioPanel.add(rbtScissors);

        btnFunctionPanel.setLayout(new FlowLayout());
        btnFunctionPanel.add(btnPick);
        btnFunctionPanel.add(btnReset);
        btnFunctionPanel.add(btnLeave);

        gamePanel.setLayout(layout1);
        gamePanel.add(timePanel, "1,1");
        gamePanel.add(imageResultPanel, "1,3");
        gamePanel.add(radioPanel, "1,5");
        gamePanel.add(btnFunctionPanel, "1,7");

        // Phần chát
        chatPanel.setLayout(layout3);
        chatPanel.add(lblUser, "1,1");
        chatPanel.add(txtUser, "3,1");
        chatPanel.add(btnSignin, "5,1");
        chatPanel.add(new JScrollPane(txtaChat), "1,3, 5,3");
        chatPanel.add(txtChat, "1,5,5,5");

        // Thêm panels vào frame
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.getContentPane().add(gamePanel, BorderLayout.WEST);
        mainFrame.getContentPane().add(chatPanel, BorderLayout.EAST);

        // Hiển thị window
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /*
     * Phương thức listener, thiết lập cho từng sự kiện sẽ được xử lý.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnSignin) {
            // Không cho phép bỏ trống tên
            if (txtUser.getText().length() != 0) {
                txtUser.setEditable(false);
                btnSignin.setEnabled(false);
                txtChat.setEditable(true);
            }
        } else if (src == txtUser) {
            // Không cho phép bỏ trống tên
            if (txtUser.getText().length() != 0) {
                txtUser.setEditable(false);
                btnSignin.setEnabled(false);
                txtChat.setEditable(true);
            }
        } else if (src == txtChat) {
            // Không cho phép phần văn bản để trống
            if (txtChat.getText().length() != 0) {
                output.println(txtChat.getText());
                txtChat.setText("");
            }
        }

        else if (src == btnPick) {
            btnPick.setEnabled(false);
            rbtRock.setEnabled(true);
            rbtPaper.setEnabled(true);
            rbtScissors.setEnabled(true);
            // Đếm 5 giây cho mỗi lượt chơi
            countDown("5");
        }

        else if (src == btnReset) {
            if (player == 1) {
                btnPick.setEnabled(true);
            }
            rbtRock.setEnabled(false);
            rbtPaper.setEnabled(false);
            rbtScissors.setEnabled(false);
            lblResult.setText("");
            txtTimer.setText("Thời gian");
            btnReset.setEnabled(false);
            selectIcon.setIcon(remindPic);
            selectIcon.setVisible(false);
        }

        else if (src == btnLeave) {
            System.exit(0);
        }

        // Radio button actions
        else if (src == rbtRock) {
            yourIcon.setIcon(rockPic);
            yourHand = 1;
        } else if (src == rbtPaper) {
            yourIcon.setIcon(paperPic);
            yourHand = 2;
        } else if (src == rbtScissors) {
            yourIcon.setIcon(scissorsPic);
            yourHand = 3;
        }
    }

    /*
     * Constructor: Tạo các socket stream cho việc giao tiếp và load GUI.
     */
    public Client() {
        createDialog();
        Socket socket;
        try {
            // Khởi tạo socket & streams
            socket = new Socket(host, 4242);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            // socket.close();
        } catch (IOException e) {
            System.err.println("Kết nối bị từ chối, không tìm thấy server?\nĐóng app.");
            System.exit(0);
        }

        // LOAD GUI
        createComponents();
        createFrame();
    }

    /*
     * Phương thức run: chạy liên tục, kiểm soát giao tiếp giữa client và server.
     */
    private void run() {
        // lặp và đợi user đăng nhập
        String name = null;
        while (txtUser.isEditable()) {
            System.out.print("");
            if (txtUser.isEditable() == false) {
                name = txtUser.getText();
                break;
            }
        }

        try {
            // In thông tin từ tin nhắn, thông tin game lên khung chát
            while (true) {
                String line;
                line = input.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    output.println(name);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    // Bật thanh chát để cho phép gửi tin nhắn
                    txtChat.setEditable(true);
                } else if (line.startsWith("RULE")) {
                    txtaChat.append(line.substring(5) + "\n");
                } else if (line.startsWith("WELCOME")) {
                    txtaChat.append(line.substring(8) + "!\n\n");
                    player = Integer.parseInt(line.substring(29, 30));
                } else if (line.startsWith("MESSAGE")) {
                    if (line.substring(8,8 + name.length()).contains(name)) {
                        String userMessage = line.substring(8 + name.length());
                            txtaChat.append("# Bạn"+ userMessage + '\n');
                    }
                    else {
                            txtaChat.append(line.substring(8) + "\n");
                    }
                    txtaChat.setCaretPosition(txtaChat.getDocument().getLength());
                } else if (line.startsWith("PLAYER1")) {
                    if (player == 1) {
                        btnPick.setEnabled(true);
                    }
                } else if (line.startsWith("OPPONENT_PLAYED")) {
                    if (player == 2) {
                        btnPick.setEnabled(true);
                    }
                    oppoHand = Integer.parseInt(line.substring(15));
                    txtaChat.append("Đối thủ đã chọn xong.\n");
                    switch (oppoHand) {
                    case 1:
                        selectIcon.setIcon(rockPic);
                        break;
                    case 2:
                        selectIcon.setIcon(paperPic);
                        break;
                    case 3:
                        selectIcon.setIcon(scissorsPic);
                        break;
                    }
                } else if (line.startsWith("RESULT")) {
                    //lblResult.setText(line.substring(6));
                    //txtaChat.append("# " + line.substring(6) + '\n');
                    if (line.substring(6).contains("P")) {
                        if (Integer.parseInt(line.substring(7,8)) == player) {
                            txtaChat.append("# Bạn thắng" + '\n');
                            lblResult.setText("Bạn thắng");
                        }
                        else {
                            txtaChat.append("# Bạn thua" + '\n');
                            lblResult.setText("Bạn thua");
                        }
                    }
                    else {
                        lblResult.setText(line.substring(6));
                        txtaChat.append("# " + line.substring(6) + '\n');
                    }
                } else {
                    // Hiển thị tin nhắn từ server
                    // textarea.append("@SYSTEM: " + line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Phương thức countDown: Đếm thời gian cho mỗi người chơi chọn
     */
    private void countDown(final String time) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = Integer.parseInt(time) + 1;

            public void run() {
                Client.this.txtTimer.setText(String.format("%d", --i));
                if (i == 0) {
                    // in lựa chọn của người chơi khi thời gian kết thúc
                    output.println(String.format("MOVE%d", yourHand));
                    rbtRock.setEnabled(false);
                    rbtPaper.setEnabled(false);
                    rbtScissors.setEnabled(false);
                    selectIcon.setVisible(true);
                    btnReset.setEnabled(true);
                } else if (i < 0) {
                    Client.this.txtTimer.setText("0");
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    // Hàm main
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.mainFrame.setVisible(true);
        client.run();
    }

}