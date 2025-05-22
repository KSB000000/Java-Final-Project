import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;

class Pair<V1, V2> {
    private V1 value1;
    private V2 value2;

    public Pair(V1 value1, V2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public V1 getValue1() {
        return value1;
    }

    public void setValue1(V1 value1) {
        this.value1 = value1;
    }

    public V2 getValue2() {
        return value2;
    }

    public void setValue2(V2 value2) {
        this.value2 = value2;
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String filePath) {
        try {
            backgroundImage = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}

public class Bucket extends JFrame {
    private String studentId;
    private boolean login;
    private JMenuItem loginMenuItem;
    private JMenuItem logoutMenuItem;
    private CardLayout Card = new CardLayout();
    private Vector<String> StudentId_List = new Vector<>();
    private Vector<String> Title_List = new Vector<>();
    private HashMap<String, String> Title_StudentId = new HashMap<>();
    private HashMap<String, Vector<String>> StudentId_Title = new HashMap<>();
    private HashMap<String, Vector<Pair<String, String>>> Title_Comment_Student = new HashMap<>();
    private HashMap<String, JPanel> Title_Panel = new HashMap<>();
    private HashMap<String, JPanel> Title_CommentPanel = new HashMap<>();
    private HashMap<String, Vector<Pair<String, Boolean>>> Title_Student_Like = new HashMap<>();
    private HashMap<String, Integer> Title_Likes = new HashMap<>();
    private HashMap<String, String> StudentId_Password = new HashMap<>();

    private JMenuBar mb;
    private JPanel Cards;
    private BackgroundPanel IndexPanel;
    private JPanel WritePanel;
    private JPanel ListPanel;
    private JPanel MyListPanel;
    private JPanel ShowPanel;
    private JPanel LoginPanel;
    private JPanel RegisterPanel;
    private BackgroundPanel MainPanel;

    private int nowIndex;

    public Bucket() {
        this.login = false;
        setTitle("BucketList Write");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - 600) / 2, (screenSize.height - 400) / 2);
        createCards();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MakeIndexPanel();
        setVisible(true);
    }

    public void MakeIndexPanel() {
        setSize(600, 400);
        IndexPanel.setLayout(null);
        JButton Login = new JButton("Login");
        JButton Register = new JButton("Register");

        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MakeLoginPanel();
                Card.show(Cards, "Login");
            }
        });
        Register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MakeRegisterPanel();
                Card.show(Cards, "Register");
            }
        });
        Login.setBounds(250, 85, 103, 50);
        Register.setBounds(253, 210, 100, 50);

        IndexPanel.add(Register);
        IndexPanel.add(Login);
        IndexPanel.revalidate();
        IndexPanel.repaint();
    }

    public void MakeMainPanel() {
        MainPanel.removeAll();
        MainPanel.setLayout(null);
        JPanel Top5_Panel = new JPanel(null);
        int size = Title_List.size();
        Vector<Pair<String, Integer>> Title_Likes_Sort = new Vector<>();
        for (int i = 0; i < Title_List.size(); i++) {
            String title = Title_List.get(i);
            int Likes = Title_Likes.get(title);
            Title_Likes_Sort.add(new Pair<>(title, Likes));
        }
        JLabel Main_Label = new JLabel("Bucket List Community");
        Main_Label.setFont(new Font("SansSerif", Font.BOLD, 45));
        Main_Label.setBounds(160, 50, 600, 100);
        Main_Label.setForeground(new Color(255, 255, 255));
        MainPanel.add(Main_Label);
        JLabel Top5_Label = new JLabel("Top 5 Bucket Lists");
        Top5_Label.setFont(new Font("Arial", Font.ITALIC, 20));
        Top5_Label.setBounds(30, 10, 400, 50);
        Top5_Panel.setBackground(Color.WHITE);
        Top5_Panel.add(Top5_Label);

        ImageIcon Write_icon = new ImageIcon("Write.jpg");
        ImageIcon AllList_icon = new ImageIcon("AllList.jpg");
        ImageIcon MyList_icon = new ImageIcon("MyList.jpg");
        JButton Go_Write = new JButton(Write_icon);
        JButton Go_MyList = new JButton(MyList_icon);
        JButton Go_AllList = new JButton(AllList_icon);
        Go_Write.setBounds(540, 150, 200, 100);
        Go_MyList.setBounds(540, 390, 200, 100);
        Go_AllList.setBounds(540, 270, 200, 100);
        Go_AllList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nowIndex = 0;
                MakeAllListPanel();
                Card.show(Cards, "List");
            }
        });
        Go_Write.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MakeWritePanel();
                Card.show(Cards, "Write");
            }
        });
        Go_MyList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((!StudentId_Title.containsKey(studentId) || StudentId_Title.get(studentId).size() == 0)) {
                    JOptionPane.showMessageDialog(MainPanel, "No entries found for student ID: " + studentId);
                    MakeMainPanel();
                    Card.show(Cards, "Main");
                    return;
                } else {
                    nowIndex = 0;
                    MakeMyListPanel();
                    Card.show(Cards, "MyList");
                }
            }
        });
        MainPanel.add(Go_AllList);
        MainPanel.add(Go_MyList);
        MainPanel.add(Go_Write);
        Title_Likes_Sort.sort(Comparator.comparing(Pair<String, Integer>::getValue2).reversed());
        if (size <= 5) {
            for (int i = 0; i < size; i++) {
                String title = Title_Likes_Sort.get(i).getValue1();
                String id = Title_StudentId.get(title);
                int Likes = Title_Likes_Sort.get(i).getValue2();
                JLabel Title_Label = new JLabel(title);
                JLabel Like_Label = new JLabel(String.valueOf("Likes: " + Likes));
                JLabel Id_Label = new JLabel("ID: " + id);
                Title_Label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JTabbedPane ClickedPane = new JTabbedPane(JTabbedPane.LEFT);
                        String title = ((JLabel) e.getSource()).getText();
                        JPanel ContentPane = Title_Panel.get(title);
                        JPanel CommentPane = Title_CommentPanel.get(title);
                        Title_Panel.get(title).revalidate();

                        ClickedPane.addTab("Content", ContentPane);
                        ClickedPane.addTab("Comment", CommentPane);
                        ClickedPane.setBounds(0, 0, 770, 520);
                        ShowPanel.removeAll();
                        ShowPanel.add(ClickedPane, BorderLayout.CENTER);
                        ShowPanel.revalidate();
                        ShowPanel.repaint();
                        Card.show(Cards, "Show");
                    }
                });
                Title_Label.setBounds(30, 75 + 30 * i, 200, 20);
                Like_Label.setBounds(200, 75 + 30 * i, 100, 20);
                Id_Label.setBounds(300, 75 + 30 * i, 100, 20);
                Top5_Panel.add(Title_Label);
                Top5_Panel.add(Like_Label);
                Top5_Panel.add(Id_Label);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                String title = Title_Likes_Sort.get(i).getValue1();
                String id = Title_StudentId.get(title);
                int Likes = Title_Likes_Sort.get(i).getValue2();
                JLabel Title_Label = new JLabel(title);
                JLabel Like_Label = new JLabel(String.valueOf("Likes: " + Likes));
                JLabel Id_Label = new JLabel("ID: " + id);
                Title_Label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JTabbedPane ClickedPane = new JTabbedPane(JTabbedPane.LEFT);
                        String title = ((JLabel) e.getSource()).getText();
                        JPanel ContentPane = Title_Panel.get(title);
                        JPanel CommentPane = Title_CommentPanel.get(title);
                        Title_Panel.get(title).revalidate();

                        ClickedPane.addTab("Content", ContentPane);
                        ClickedPane.addTab("Comment", CommentPane);
                        ClickedPane.setBounds(0, 0, 770, 520);
                        ShowPanel.removeAll();
                        ShowPanel.add(ClickedPane, BorderLayout.CENTER);
                        ShowPanel.revalidate();
                        ShowPanel.repaint();
                        Card.show(Cards, "Show");
                    }
                });
                Title_Label.setBounds(30, 75 + 30 * i, 200, 20);
                Like_Label.setBounds(200, 75 + 30 * i, 100, 20);
                Id_Label.setBounds(300, 75 + 30 * i, 100, 20);
                Top5_Panel.add(Title_Label);
                Top5_Panel.add(Like_Label);
                Top5_Panel.add(Id_Label);
            }
        }
        Top5_Panel.setBounds(100, 150, 400, 250);
        Top5_Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        MainPanel.add(Top5_Panel);
    }

    public void MakeRegisterPanel() {
        RegisterPanel.removeAll();
        JLabel ID_Label = new JLabel("ID");
        JLabel Password_Label = new JLabel("Password");
        JLabel Password_Confirm = new JLabel("Confirm Password");
        JButton Register_Submit = new JButton("Register");
        JLabel Go_Login = new JLabel("◁ Login");
        JTextField Id_Text = new JTextField(20);
        JTextField Password_Text = new JTextField(20);
        JTextField Confirm_Text = new JTextField(20);
        ID_Label.setBounds(210, 45, 150, 25);
        Password_Label.setBounds(210, 100, 150, 25);
        Password_Confirm.setBounds(210, 155, 150, 25);
        Register_Submit.setBounds(250, 240, 100, 50);
        Go_Login.setBounds(25, 0, 100, 25);

        Id_Text.setBounds(210, 70, 180, 25);
        Password_Text.setBounds(210, 125, 180, 25);
        Confirm_Text.setBounds(210, 180, 180, 25);
        RegisterPanel.add(ID_Label);
        RegisterPanel.add(Id_Text);
        RegisterPanel.add(Password_Label);
        RegisterPanel.add(Password_Text);
        RegisterPanel.add(Password_Confirm);
        RegisterPanel.add(Confirm_Text);
        RegisterPanel.add(Register_Submit);
        RegisterPanel.add(Go_Login);
        Go_Login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Card.show(Cards, "Index");
            }
        });
        Register_Submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Id_Text.getText().trim();
                String password = Password_Text.getText().trim();
                String confirm = Confirm_Text.getText().trim();
                if (id.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterPanel, "There are empty fields. Please fill them in.");
                    return;
                }
                if (StudentId_List.contains(id)) {
                    JOptionPane.showMessageDialog(RegisterPanel, "Already ID exists");
                    return;
                } else {
                    if (!password.equals(confirm)) {
                        JOptionPane.showMessageDialog(RegisterPanel, "The passwords do not match.");
                        return;
                    } else {
                        StudentId_Password.put(id, password);
                        StudentId_List.add(id);
                        int size = Title_List.size();
                        for (int i = 0; i < size; i++) {
                            String title = Title_List.get(i);
                            Title_Student_Like.get(title).add(new Pair<>(id, false));
                        }
                        JOptionPane.showMessageDialog(RegisterPanel, "You have successfully registered.");
                        Card.show(Cards, "Index");
                    }
                }
            }
        });

    }

    public void MakeLoginPanel() {
        LoginPanel.removeAll();
        JLabel ID_Label = new JLabel("ID");
        JLabel Password_Label = new JLabel("Password");
        JButton Login_Button = new JButton("Login");
        JLabel Go_Regisiter = new JLabel("◁ Register");
        Go_Regisiter.setBounds(25, 0, 100, 25);
        ID_Label.setBounds(210, 70, 150, 25);
        Password_Label.setBounds(210, 130, 150, 25);
        Login_Button.setBounds(250, 210, 100, 50);

        JTextField Id_Text = new JTextField(10);
        JTextField Password_Text = new JTextField(10);
        Id_Text.setBounds(210, 95, 180, 25);
        Password_Text.setBounds(210, 155, 180, 25);
        LoginPanel.add(Go_Regisiter);
        LoginPanel.add(ID_Label);
        LoginPanel.add(Password_Label);
        LoginPanel.add(Id_Text);
        LoginPanel.add(Password_Text);
        LoginPanel.add(Login_Button);
        Go_Regisiter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Card.show(Cards, "Index");
            }
        });
        Login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Id_Text.getText().trim();
                String password = Password_Text.getText().trim();
                if (id.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterPanel, "There are empty fields. Please fill them in.");
                    return;
                }
                if (!StudentId_Password.containsKey(id)) {
                    JOptionPane.showMessageDialog(RegisterPanel, "The ID does not exist.");
                    return;
                }
                if (!StudentId_Password.get(id).equals(password)) {
                    JOptionPane.showMessageDialog(RegisterPanel, "The password is incorrect.");
                    return;
                } else {
                    studentId = id;
                    login = true;
                    createMenu();
                    updateMenu();
                    setSize(800, 600);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    setLocation((screenSize.width - 800) / 2, (screenSize.height - 600) / 2);
                    MakeMainPanel();
                    Card.show(Cards, "Main");
                }
            }
        });
    }

    public void createMenu() {

        mb = new JMenuBar();
        JMenuItem Home = new JMenuItem("Home");
        mb.add(Home);
        JMenuItem myListMenuItem = new JMenuItem("My List");
        loginMenuItem = new JMenuItem("Login");
        logoutMenuItem = new JMenuItem("Logout");

        mb.add(Box.createHorizontalGlue());
        JMenu My_Page = new JMenu("My Page");
        My_Page.add(myListMenuItem);
        My_Page.add(loginMenuItem);
        My_Page.add(logoutMenuItem);
        mb.add(My_Page);

        myListMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((!StudentId_Title.containsKey(studentId) || StudentId_Title.get(studentId).size() == 0)) {
                    JOptionPane.showMessageDialog(null, "No entries found for student ID: " + studentId);
                    return;
                }
                nowIndex = 0;
                MakeMyListPanel();
                Card.show(Cards, "MyList");
            }
        });
        Home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MakeMainPanel();
                Card.show(Cards, "Main");
            }
        });
        logoutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login = false;
                mb.setVisible(false);
                Card.show(Cards, "Index");
                setSize(600, 400);
                updateMenu();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                setLocation((screenSize.width - 600) / 2, (screenSize.height - 400) / 2);
                JOptionPane.showMessageDialog(Bucket.this, "Logged out successfully.");
            }
        });
        setJMenuBar(mb);
    }

    public void updateMenu() {
        loginMenuItem.setVisible(!login);
        logoutMenuItem.setVisible(login);
    }

    public void createCards() {
        Cards = new JPanel(Card);
        LoginPanel = new JPanel(null);
        IndexPanel = new BackgroundPanel("Indexpage.jpg");
        MainPanel = new BackgroundPanel("Main.jpg");
        RegisterPanel = new JPanel(null);
        WritePanel = new JPanel(null);
        ListPanel = new JPanel(null);
        MyListPanel = new JPanel(null);
        ShowPanel = new JPanel(null);

        Cards.add("Index", IndexPanel);
        Cards.add("Login", LoginPanel);
        Cards.add("Register", RegisterPanel);
        Cards.add("Write", WritePanel);
        Cards.add("List", ListPanel);
        Cards.add("MyList", MyListPanel);
        Cards.add("Show", ShowPanel);
        Cards.add("Main", MainPanel);

        add(Cards, BorderLayout.CENTER);
    }

    public void MakeWritePanel() {
        WritePanel.removeAll();
        JLabel title = new JLabel("Title :");
        JTextField tf = new JTextField(40);
        JLabel content = new JLabel("Content :");
        JTextPane textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        JButton submitButton = new JButton("Submit");
        JButton ImageButton = new JButton("Add");
        JLabel ImageLabel;
        ImageLabel = new JLabel();
        ImageLabel.setBounds(410, 100, 300, 300);
        ImageLabel.setIcon(new ImageIcon("defaultimages.jpg"));
        ImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        title.setBounds(50, 50, 80, 25);
        tf.setBounds(100, 50, 610, 25);
        content.setBounds(30, 90, 80, 25);
        scrollPane.setBounds(100, 100, 300, 300);
        submitButton.setBounds(170, 405, 100, 30);
        ImageButton.setBounds(100, 405, 60, 30);

        ImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF", "jpg", "gif");
                chooser.setFileFilter(filter);
                int ret = chooser.showOpenDialog(null);
                if (ret != JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(null,
                            "파일을 선택하지 않았습니다", "경고",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String filePath = chooser.getSelectedFile().getPath();
                try {
                    BufferedImage originalImage = ImageIO.read(new File(filePath));
                    Image resizedImage = originalImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                    ImageIcon imageIcon = new ImageIcon(resizedImage);
                    ImageLabel.setIcon(imageIcon);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (login) {
                    JPanel Title_Panel_element = new JPanel(null);
                    String title = tf.getText();
                    String content = textPane.getText();
                    ImageIcon Image = (ImageIcon) ImageLabel.getIcon();
                    if (title.isEmpty() || content.isEmpty()) {
                        JOptionPane.showMessageDialog(WritePanel, "The content is empty. Please fill it in.");
                        return;
                    }
                    if (Title_Panel.containsKey(title)) {
                        JOptionPane.showMessageDialog(WritePanel, "The title already exists.");
                        return;
                    }
                    Title_List.add(title);
                    Title_StudentId.put(title, studentId);
                    if (!StudentId_Title.containsKey(studentId)) {
                        StudentId_Title.put(studentId, new Vector<>());
                    }

                    if (!Title_Student_Like.containsKey(title)) {
                        Title_Student_Like.put(title, new Vector<>());
                    }

                    StudentId_Title.get(studentId).add(title);
                    Title_Likes.put(title, 0);
                    JPanel Like_Panel = new JPanel(null);
                    Like_Panel.setBounds(300, 380, 100, 60);
                    ImageIcon imageIcon = new ImageIcon("likeimage.jpg");
                    JLabel Like_image = new JLabel();
                    Like_image.setIcon(imageIcon);
                    Like_image.setBounds(10, 5, 50, 50);
                    JLabel Likes = new JLabel(String.valueOf(Title_Likes.get(title)));
                    Likes.setBounds(70, 5, 50, 50);
                    Like_Panel.add(Like_image);
                    Like_Panel.add(Likes);
                    Like_Panel.setBackground(Color.WHITE);
                    Like_Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    Like_Panel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int size = Title_Student_Like.get(title).size();
                            for (int i = 0; i < size; i++) {
                                if (Title_Student_Like.get(title).get(i).getValue1().equals(studentId)) {
                                    if (Title_Student_Like.get(title).get(i).getValue2()) {
                                        JOptionPane.showMessageDialog(Bucket.this,
                                                "You have already liked this.");
                                        return;
                                    } else {
                                        Title_Student_Like.get(title).get(i).setValue2(true);
                                        Title_Likes.put(title, Title_Likes.get(title) + 1);
                                    }
                                }
                            }
                            Likes.setText(String.valueOf(Title_Likes.get(title)));
                        }
                    });
                    JLabel contentTitle = new JLabel("Title: " + title);
                    JTextArea contentText = new JTextArea(content);
                    JLabel contentImage = new JLabel(Image);
                    JLabel ID_Label = new JLabel("ID: " + studentId + " ");
                    ID_Label.setBounds(490, 10, 180, 25);
                    contentImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    contentTitle.setBounds(10, 10, 200, 25);
                    contentText.setBounds(10, 45, 320, 300);
                    contentImage.setBounds(370, 45, 300, 300);
                    contentText.setEditable(false);
                    ID_Label.setHorizontalAlignment(SwingConstants.RIGHT);

                    Title_Panel_element.setBounds(0, 0, 800, 600);
                    Title_Panel_element.add(ID_Label);
                    Title_Panel_element.add(contentTitle);
                    Title_Panel_element.add(contentText);
                    Title_Panel_element.add(contentImage);
                    Title_Panel_element.add(Like_Panel);
                    Title_Panel.put(title, Title_Panel_element);

                    if (!Title_Comment_Student.containsKey(title)) {
                        Title_Comment_Student.put(title, new Vector<>());
                    }
                    int ID_size = StudentId_List.size();
                    for (int i = 0; i < ID_size; i++) {
                        Title_Student_Like.get(title).add(new Pair<>(StudentId_List.get(i), false));
                    }
                    JPanel Comment_main_Panel = new JPanel(null);
                    Comment_main_Panel.setBounds(0, 0, 800, 600);
                    JPanel Comments_Panel = new JPanel(null);

                    JScrollPane Comments_ScrollPane = new JScrollPane(Comments_Panel);
                    Comments_ScrollPane.setBounds(0, 0, 690, 400);

                    JTextArea Comment_write = new JTextArea(3, 40);
                    JScrollPane Comment_write_ScrollPane = new JScrollPane(Comment_write);
                    Comment_write_ScrollPane.setBounds(5, 405, 575, 105);

                    JButton Comment_Submit_Button = new JButton("Submit");
                    Comment_Submit_Button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String commentText = Comment_write.getText();
                            if (!commentText.isEmpty()) {
                                Comment_write.setText("");
                                Comments_Panel.removeAll();
                                Title_Comment_Student.get(title).add(new Pair<>(commentText, studentId));
                                Vector<Pair<String, String>> comments = Title_Comment_Student.get(title);
                                int index = 0;
                                int panelHeight = 0;
                                for (Pair<String, String> comment : comments) {
                                    String Student_Id = comment.getValue2();
                                    String Student_Comment = comment.getValue1();
                                    JTextArea Student_Comment_Area = new JTextArea(Student_Comment);
                                    Student_Comment_Area.setEditable(false);
                                    Student_Comment_Area.setLineWrap(true);
                                    Student_Comment_Area.setWrapStyleWord(true);

                                    JPanel commentBox = new JPanel(null);
                                    commentBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                    JLabel ID_Label = new JLabel("ID: " + Student_Id);
                                    ID_Label.setBounds(10, 10, 200, 15);
                                    JScrollPane commentContent_ScrollBar = new JScrollPane(Student_Comment_Area);
                                    commentContent_ScrollBar.setBounds(10, 25, 630, 45);
                                    commentBox.setBounds(5, 5 + 85 * index, 650, 80);
                                    commentBox.add(ID_Label);
                                    commentBox.add(commentContent_ScrollBar);
                                    Comments_Panel.add(commentBox);
                                    index++;
                                    panelHeight = 5 + 80 * index;
                                }

                                Comments_Panel.setPreferredSize(new Dimension(650, panelHeight + 60));
                                Comments_Panel.revalidate();
                                Comments_Panel.repaint();
                                Comments_ScrollPane.revalidate();
                                Comments_ScrollPane.repaint();
                                Comment_main_Panel.validate();
                                Comment_main_Panel.repaint();
                            }
                        }
                    });
                    Comment_Submit_Button.setBounds(585, 405, 100, 105);

                    Comment_main_Panel.add(Comments_ScrollPane);
                    Comment_main_Panel.add(Comment_Submit_Button);
                    Comment_main_Panel.add(Comment_write_ScrollPane);
                    Title_CommentPanel.put(title, Comment_main_Panel);
                    MakeMainPanel();
                    Card.show(Cards, "Main");
                } else {
                    JOptionPane.showMessageDialog(Bucket.this, "You must be logged in to submit.");
                }
            }
        });
        WritePanel.add(title);
        WritePanel.add(tf);
        WritePanel.add(content);
        WritePanel.add(scrollPane);
        WritePanel.add(submitButton);
        WritePanel.add(ImageButton);
        WritePanel.add(ImageLabel);
        WritePanel.revalidate();
        WritePanel.repaint();
    }

    public void MakeMyListPanel() {
        MyListPanel.removeAll();
        int size = StudentId_Title.get(studentId).size();
        if (size == 0) {
            JOptionPane.showMessageDialog(null, "No entries found for student ID: " + studentId);
            MakeMainPanel();
            Card.show(Cards, "Main");
            return;
        }
        Vector<JLabel> TextLabels = new Vector<>();
        Vector<JCheckBox> DeleteBox = new Vector<>();
        JLabel SelectAll_Button = new JLabel(" Select All");
        JLabel Delete_Button = new JLabel(" Delete");
        SelectAll_Button.setBounds(95, 70, 60, 25);
        SelectAll_Button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        SelectAll_Button.setBackground(Color.WHITE);
        SelectAll_Button.setOpaque(true);

        Delete_Button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Delete_Button.setBounds(160, 70, 45, 25);
        Delete_Button.setBackground(Color.WHITE);
        Delete_Button.setOpaque(true);

        JLabel Go_Right = new JLabel(">");
        JLabel Go_Left = new JLabel("<");
        Go_Right.setBounds(430, 405, 20, 25);
        Go_Left.setBounds(370, 405, 20, 25);
        if (nowIndex == size && size > 0) {
            nowIndex -= 10;
        }

        Go_Right.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (nowIndex + 10 >= size) {
                    return;
                } else {
                    nowIndex += 10;
                    MakeMyListPanel();
                }
            }
        });

        Go_Left.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (nowIndex == 0) {
                    return;
                }
                nowIndex -= 10;
                MakeMyListPanel();
            }
        });

        class line extends JPanel {
            public line() {
                setLayout(null);
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < 10; i++) {
                    g.setColor(Color.black);
                    g.drawLine(150, 125 + i * 30, 650, 125 + i * 30);

                }
            }
        }

        line linePanel = new line();
        linePanel.setBounds(0, 0, 800, 600);
        linePanel.add(Go_Left);
        linePanel.add(Go_Right);
        if (size == 0) {
            JLabel NowPageNum = new JLabel(String.valueOf(1));
            NowPageNum.setBounds(400, 410, 15, 15);
            linePanel.add(NowPageNum);

        } else {
            linePanel.add(Delete_Button);
            linePanel.add(SelectAll_Button);
        }

        for (int i = 0; i < size; i++) {

            JLabel NowPageNum = new JLabel(String.valueOf(nowIndex / 10 + 1));
            NowPageNum.setBounds(400, 410, 15, 15);
            JLabel IndexLabel = new JLabel(String.valueOf(i + 1) + ". ");
            JLabel textLabel = new JLabel();
            textLabel.setText(StudentId_Title.get(studentId).get(i));
            textLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JTabbedPane ClickedPane = new JTabbedPane(JTabbedPane.LEFT);
                    String title = ((JLabel) e.getSource()).getText();
                    JPanel ContentPane = Title_Panel.get(title);
                    JPanel CommentPane = Title_CommentPanel.get(title);

                    ClickedPane.addTab("Content", ContentPane);
                    ClickedPane.addTab("Comment", CommentPane);
                    ClickedPane.setBounds(0, 0, 770, 520);
                    ShowPanel.removeAll();
                    ShowPanel.add(ClickedPane, BorderLayout.CENTER);
                    ShowPanel.revalidate();
                    ShowPanel.repaint();
                    Card.show(Cards, "Show");
                }
            });
            JCheckBox checkBox = new JCheckBox();

            TextLabels.add(textLabel);
            DeleteBox.add(checkBox);

            if (i >= nowIndex && i < nowIndex + 10) {
                IndexLabel.setBounds(130, 100 + (i - nowIndex) * 30, 20, 25);
                textLabel.setBounds(150, 100 + (i - nowIndex) * 30, 650, 25);
                checkBox.setBounds(100, 100 + (i - nowIndex) * 30, 20, 25);
                linePanel.add(IndexLabel);
                linePanel.add(textLabel);
                linePanel.add(checkBox);
            }
            linePanel.add(NowPageNum);
        }

        SelectAll_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = 0;
                if (size - nowIndex >= 10) {
                    for (int i = 0; i < 10; i++) {
                        if (DeleteBox.get(i + nowIndex).isSelected()) {
                            index++;
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        if (index == 10) {
                            DeleteBox.get(i + nowIndex).setSelected(false);
                        } else {
                            DeleteBox.get(i + nowIndex).setSelected(true);
                        }
                    }
                } else {
                    for (int i = 0; i < size % 10; i++) {
                        if (DeleteBox.get(i + nowIndex).isSelected()) {
                            index++;
                        }
                    }
                    if (index == size % 10) {
                        for (int i = 0; i < size % 10; i++) {
                            DeleteBox.get(i + nowIndex).setSelected(false);
                        }
                    } else {
                        for (int i = 0; i < size % 10; i++) {
                            DeleteBox.get(i + nowIndex).setSelected(true);
                        }
                    }
                }
            }
        });

        Delete_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = size - 1; i >= 0; i--) {
                    if (DeleteBox.get(i).isSelected()) {
                        String title = StudentId_Title.get(studentId).get(i);
                        StudentId_Title.get(studentId).remove(i);
                        Title_List.remove(title);
                        Title_Panel.remove(title);
                        Title_Likes.remove(title);
                        Title_Student_Like.remove(title);
                        Title_CommentPanel.remove(title);
                        Title_Comment_Student.remove(title);
                        Title_StudentId.remove(title);
                        DeleteBox.remove(i);
                    }
                }
                MakeMyListPanel();
            }
        });
        MyListPanel.add(linePanel);

        MyListPanel.revalidate();
        MyListPanel.repaint();
    }

    public void MakeAllListPanel() {
        ListPanel.removeAll();
        int size = Title_List.size();
        Vector<JLabel> TextLabels = new Vector<>();
        JLabel Go_Right = new JLabel(">");
        JLabel Go_Left = new JLabel("<");
        Go_Right.setBounds(430, 405, 20, 25);
        Go_Left.setBounds(370, 405, 20, 25);
        if (nowIndex == size && size > 0) {
            nowIndex -= 10;
        }

        Go_Right.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (nowIndex + 10 >= size) {
                    return;
                } else {
                    nowIndex += 10;
                    MakeAllListPanel();
                }
            }
        });

        Go_Left.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (nowIndex == 0) {
                    return;
                }
                nowIndex -= 10;
                MakeAllListPanel();
            }
        });

        class line extends JPanel {
            public line() {
                setLayout(null);
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < 10; i++) {
                    g.setColor(Color.black);
                    g.drawLine(150, 125 + i * 30, 650, 125 + i * 30);

                }
            }
        }

        line linePanel = new line();
        linePanel.setBounds(0, 0, 800, 600);
        linePanel.add(Go_Left);
        linePanel.add(Go_Right);
        if (size == 0) {
            JLabel NowPageNum = new JLabel(String.valueOf(1));
            NowPageNum.setBounds(400, 410, 15, 15);
            linePanel.add(NowPageNum);
        }

        for (int i = 0; i < size; i++) {
            JLabel NowPageNum = new JLabel(String.valueOf(nowIndex / 10 + 1));
            NowPageNum.setBounds(400, 410, 15, 15);
            JLabel IndexLabel = new JLabel(String.valueOf(i + 1) + ". ");
            JLabel textLabel = new JLabel();
            textLabel.setText(Title_List.get(i));
            String title = textLabel.getText();
            JLabel student_id_Label = new JLabel();
            student_id_Label.setText("ID: " + Title_StudentId.get(title));
            textLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JTabbedPane ClickedPane = new JTabbedPane(JTabbedPane.LEFT);
                    String title = ((JLabel) e.getSource()).getText();
                    JPanel ContentPane = Title_Panel.get(title);
                    JPanel CommentPane = Title_CommentPanel.get(title);
                    Title_Panel.get(title).revalidate();

                    ClickedPane.addTab("Content", ContentPane);
                    ClickedPane.addTab("Comment", CommentPane);
                    ClickedPane.setBounds(0, 0, 770, 520);
                    ShowPanel.removeAll();
                    ShowPanel.add(ClickedPane, BorderLayout.CENTER);
                    ShowPanel.revalidate();
                    ShowPanel.repaint();
                    Card.show(Cards, "Show");
                }
            });
            TextLabels.add(textLabel);

            if (i >= nowIndex && i < nowIndex + 10) {
                IndexLabel.setBounds(130, 100 + (i - nowIndex) * 30, 20, 25);
                textLabel.setBounds(150, 100 + (i - nowIndex) * 30, 650, 25);
                student_id_Label.setBounds(650, 100 + (i - nowIndex) * 30, 100, 25);
                student_id_Label.setHorizontalAlignment(SwingConstants.LEFT);

                linePanel.add(IndexLabel);
                linePanel.add(textLabel);
                linePanel.add(student_id_Label);
            }
            linePanel.add(NowPageNum);
        }
        ListPanel.add(linePanel);
        ListPanel.revalidate();
        ListPanel.repaint();
    }

    public static void main(String[] args) {
        new Bucket();
    }
}
