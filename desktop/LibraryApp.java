import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibraryApp {

    private static final String API = "http://localhost:8080";

    private static final Color BG = new Color(18, 18, 18);
    private static final Color SIDEBAR = new Color(24, 24, 27);
    private static final Color HEADER = new Color(30, 30, 34);
    private static final Color CARD = new Color(35, 35, 40);
    private static final Color CARD_ALT = new Color(42, 42, 48);
    private static final Color FIELD = new Color(31, 31, 36);
    private static final Color BORDER = new Color(64, 64, 72);
    private static final Color TEXT = new Color(232, 232, 236);
    private static final Color MUTED = new Color(160, 160, 170);
    private static final Color ACCENT = new Color(105, 125, 255);
    private static final Color ACCENT_HOVER = new Color(126, 144, 255);
    private static final Color SUCCESS = new Color(54, 190, 132);
    private static final Color ERROR = new Color(235, 92, 92);
    private static final Color WARNING = new Color(240, 184, 72);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private JLabel userLabel;
    private JLabel statusLabel;
    private JButton logoutButton;
    private JProgressBar loader;
    private JTable booksTable;
    private JTable issueLogsTable;
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();

    private String token = "";
    private String loggedInUser = "";

    public static void main(String[] args) {
        setupLookAndFeel();
        SwingUtilities.invokeLater(() -> new LibraryApp().show());
    }

    private static void setupLookAndFeel() {
        try {
            Class.forName("com.formdev.flatlaf.FlatDarkLaf")
                    .getMethod("setup")
                    .invoke(null);
        } catch (Exception flatLafMissing) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {
                // Default Swing look and feel is still usable.
            }
        }
    }

    private void show() {
        frame = new JFrame("Librarium - Smart Library System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 720));
        frame.setSize(1180, 760);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createContentPanel(), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
        
        // Set initial nav state - only login visible, others hidden
        updateNavForAuth(false);
        navigate("login");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(248, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(new EmptyBorder(22, 18, 18, 18));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);

        JLabel logo = new JLabel("L");
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setOpaque(true);
        logo.setBackground(ACCENT);
        logo.setPreferredSize(new Dimension(42, 42));

        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        JLabel name = new JLabel("Librarium");
        name.setForeground(TEXT);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Library dashboard");
        subtitle.setForeground(MUTED);
        subtitle.setFont(FONT_SUBTITLE);
        brandText.add(name);
        brandText.add(subtitle);

        brand.add(logo, BorderLayout.WEST);
        brand.add(brandText, BorderLayout.CENTER);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(32, 0, 0, 0));

        // Main navigation items (hidden when logged out)
        addNav(nav, "login", "Login", "01");
        addNav(nav, "catalog", "Books", "02");
        addNav(nav, "addBook", "Add Book", "03");
        addNav(nav, "members", "Members", "04");
        addNav(nav, "issue", "Issue", "05");
        addNav(nav, "return", "Return", "06");
        addNav(nav, "logs", "Logs", "07");

        // Logout nav item (hidden until logged in)
        NavButton logoutNav = new NavButton("08", "Logout");
        logoutNav.addActionListener(e -> logout());
        logoutNav.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutNav.setVisible(false); // Hidden by default
        nav.add(Box.createVerticalStrut(8));
        nav.add(logoutNav);
        navButtons.put("logout", logoutNav);

        JLabel version = new JLabel("v2.0 modern UI");
        version.setForeground(new Color(110, 110, 120));
        version.setFont(FONT_SUBTITLE);
        version.setBorder(new EmptyBorder(12, 8, 0, 0));

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(version, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNav(JPanel nav, String key, String text, String icon) {
        NavButton button = new NavButton(icon, text);
        button.addActionListener(e -> navigate(key));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        navButtons.put(key, button);
        nav.add(button);
        nav.add(Box.createVerticalStrut(8));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 72));
        header.setBackground(HEADER);
        header.setBorder(new EmptyBorder(0, 28, 0, 28));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Smart Library System");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(FONT_SUBTITLE);
        titleBox.add(title);
        titleBox.add(statusLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);
        right.setAlignmentY(Component.CENTER_ALIGNMENT);

        loader = new JProgressBar();
        loader.setIndeterminate(true);
        loader.setVisible(false);
        loader.setPreferredSize(new Dimension(92, 8));
        loader.setBorderPainted(false);

        userLabel = new JLabel("Guest");
        userLabel.setForeground(MUTED);
        userLabel.setFont(FONT_BODY);

        logoutButton = secondaryButton("Logout");
        logoutButton.setEnabled(false);
        logoutButton.addActionListener(e -> logout());

        right.add(loader);
        right.add(userLabel);
        right.add(logoutButton);

        header.add(titleBox, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel createContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(28, 32, 32, 32));

        contentPanel.add(createLoginPanel(), "login");
        contentPanel.add(createCatalogPanel(), "catalog");
        contentPanel.add(createAddBookPanel(), "addBook");
        contentPanel.add(createMembersPanel(), "members");
        contentPanel.add(createIssuePanel(), "issue");
        contentPanel.add(createReturnPanel(), "return");
        contentPanel.add(createLogsPanel(), "logs");
        return contentPanel;
    }

    private JPanel createLoginPanel() {
        PlaceholderField email = new PlaceholderField("admin@library.com");
        PlaceholderPasswordField password = new PlaceholderPasswordField("Password");
        JButton login = primaryButton("Sign in");
        JButton register = secondaryButton("Create account");

        JPanel form = centeredCard("Welcome back", "Sign in to manage books, members, and circulation.");
        form.add(fieldGroup("Email", email));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldGroup("Password", password));
        form.add(Box.createVerticalStrut(24));
        form.add(login);
        form.add(Box.createVerticalStrut(12));
        form.add(register);

        login.addActionListener(e -> {
            String emailValue = email.getText().trim();
            String passwordValue = new String(password.getPassword()).trim();
            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                toast("Please enter email and password.", ERROR);
                return;
            }

            runAsync("Authenticating...", login, () -> {
                String json = "{\"email\":\"" + jsonEscape(emailValue) + "\",\"password\":\"" + jsonEscape(passwordValue) + "\"}";
                return request("POST", "/auth/login", json);
            }, response -> {
                token = extractJsonString(response, "token");
                loggedInUser = emailValue;
                userLabel.setText(emailValue);
                userLabel.setForeground(TEXT);
                logoutButton.setEnabled(true);
                toast("Login successful.", SUCCESS);
                
                // Show main nav items, hide login, show logout
                updateNavForAuth(true);
                navigate("catalog");
                loadBooks();
            });
        });

        register.addActionListener(e -> {
            String emailValue = email.getText().trim();
            String passwordValue = new String(password.getPassword()).trim();
            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                toast("Enter email and password to create an account.", ERROR);
                return;
            }

            runAsync("Creating account...", register, () -> {
                String json = "{\"email\":\"" + jsonEscape(emailValue) + "\",\"password\":\"" + jsonEscape(passwordValue) + "\"}";
                request("POST", "/auth/register", json);
                return request("POST", "/auth/login", json);
            }, response -> {
                token = extractJsonString(response, "token");
                loggedInUser = emailValue;
                userLabel.setText(emailValue);
                userLabel.setForeground(TEXT);
                logoutButton.setEnabled(true);
                toast("Account created and signed in.", SUCCESS);
                
                // Show main nav items, hide login, show logout
                updateNavForAuth(true);
                navigate("catalog");
                loadBooks();
            });
        });

        return page("Login", "Secure access to the library dashboard.", form);
    }

    private JPanel createCatalogPanel() {
        booksTable = table(new String[]{"ID", "Title", "Author", "Status"});
        JScrollPane scroll = tableScroll(booksTable);
        JButton refresh = secondaryButton("Refresh");
        refresh.addActionListener(e -> loadBooks());

        JPanel body = panelCard(new BorderLayout(0, 18));
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionTitle("Books Catalog", "Live catalog from your Spring Boot API."), BorderLayout.WEST);
        toolbar.add(refresh, BorderLayout.EAST);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
        body.putClientProperty("booksTable", booksTable);

        return page("Books", "Browse availability and catalog details.", body);
    }

    private JPanel createAddBookPanel() {
        PlaceholderField title = new PlaceholderField("The Pragmatic Programmer");
        PlaceholderField author = new PlaceholderField("Andrew Hunt");
        JButton add = primaryButton("Add book");

        JPanel form = centeredCard("Add Book", "Register a new book in the catalog.");
        form.add(fieldGroup("Book title", title));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldGroup("Author name", author));
        form.add(Box.createVerticalStrut(24));
        form.add(add);

        add.addActionListener(e -> {
            if (!requireAuth()) return;
            String titleValue = title.getText().trim();
            String authorValue = author.getText().trim();
            if (titleValue.isEmpty() || authorValue.isEmpty()) {
                toast("Please enter title and author.", ERROR);
                return;
            }

            runAsync("Adding book...", add, () -> {
                String json = "{\"title\":\"" + jsonEscape(titleValue) + "\",\"author\":\"" + jsonEscape(authorValue) + "\"}";
                return request("POST", "/books", json);
            }, response -> {
                title.setText("");
                author.setText("");
                toast("Book added successfully.", SUCCESS);
                loadBooks();
            });
        });

        return page("Add Book", "Keep your catalog accurate and current.", form);
    }

    private JPanel createMembersPanel() {
        PlaceholderField name = new PlaceholderField("Member name");
        PlaceholderField email = new PlaceholderField("member@email.com");
        PlaceholderPasswordField password = new PlaceholderPasswordField("Temporary password");
        JButton add = primaryButton("Register member");

        JPanel form = centeredCard("Register Member", "Create a library member account for issuing books.");
        form.add(fieldGroup("Name", name));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldGroup("Email", email));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldGroup("Password", password));
        form.add(Box.createVerticalStrut(24));
        form.add(add);

        add.addActionListener(e -> {
            if (!requireAuth()) return;
            String nameValue = name.getText().trim();
            String emailValue = email.getText().trim();
            String passValue = new String(password.getPassword()).trim();
            if (nameValue.isEmpty() || emailValue.isEmpty() || passValue.isEmpty()) {
                toast("Please fill all member fields.", ERROR);
                return;
            }

            runAsync("Registering member...", add, () -> {
                String json = "{\"name\":\"" + jsonEscape(nameValue) + "\",\"email\":\"" + jsonEscape(emailValue)
                        + "\",\"password\":\"" + jsonEscape(passValue) + "\"}";
                return request("POST", "/users/register", json);
            }, response -> {
                String memberId = extractJsonValue(response, "id");
                name.setText("");
                email.setText("");
                password.setText("");
                toast("Member registered. User ID: " + memberId, SUCCESS);
            });
        });

        return page("Members", "Add members who can borrow books.", form);
    }

    private JPanel createIssuePanel() {
        PlaceholderField bookId = new PlaceholderField("Book ID");
        PlaceholderField userId = new PlaceholderField("User ID");
        JButton issue = primaryButton("Issue book");

        JPanel form = centeredCard("Issue Book", "Check out an available book to a member.");
        form.add(fieldGroup("Book ID", bookId));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldGroup("User ID", userId));
        form.add(Box.createVerticalStrut(24));
        form.add(issue);

        issue.addActionListener(e -> {
            if (!requireAuth()) return;
            String book = bookId.getText().trim();
            String user = userId.getText().trim();
            if (book.isEmpty() || user.isEmpty()) {
                toast("Please enter book ID and user ID.", ERROR);
                return;
            }

            runAsync("Issuing book...", issue, () -> request("POST", "/issues?bookId="
                    + urlEncode(book) + "&userId=" + urlEncode(user), null), response -> {
                bookId.setText("");
                userId.setText("");
                toast("Book issued successfully.", SUCCESS);
                loadBooks();
                loadIssueLogs();
            });
        });

        return page("Issue", "Track circulation with member and book IDs.", form);
    }

    private JPanel createReturnPanel() {
        PlaceholderField issueId = new PlaceholderField("Issue ID");
        JButton submit = primaryButton("Mark returned");

        JPanel form = centeredCard("Return Book", "Close an active issue and make the book available.");
        form.add(fieldGroup("Issue ID", issueId));
        form.add(Box.createVerticalStrut(24));
        form.add(submit);

        submit.addActionListener(e -> {
            if (!requireAuth()) return;
            String id = issueId.getText().trim();
            if (id.isEmpty()) {
                toast("Please enter issue ID.", ERROR);
                return;
            }

            runAsync("Returning book...", submit, () -> request("POST", "/issues/return/" + urlEncode(id), null), response -> {
                issueId.setText("");
                toast("Book returned successfully.", SUCCESS);
                loadBooks();
                loadIssueLogs();
            });
        });

        return page("Return", "Process returned books quickly.", form);
    }

    private JPanel createLogsPanel() {
        issueLogsTable = table(new String[]{"Issue ID", "Book ID", "User ID", "Issued On", "Due Date", "Status", "Returned On"});
        JScrollPane scroll = tableScroll(issueLogsTable);
        JButton refresh = secondaryButton("Refresh");
        refresh.addActionListener(e -> loadIssueLogs());

        JPanel body = panelCard(new BorderLayout(0, 18));
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionTitle("Book Logs", "Track who took each book and when it was returned."), BorderLayout.WEST);
        toolbar.add(refresh, BorderLayout.EAST);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);

        return page("Logs", "Issue and return history for the library.", body);
    }

    private JPanel page(String title, String subtitle, JComponent body) {
        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(BG);
        page.add(sectionTitle(title, subtitle), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        boolean formCard = Boolean.TRUE.equals(body.getClientProperty("formCard"));
        gbc.fill = formCard ? GridBagConstraints.NONE : GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(body, gbc);

        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel centeredCard(String title, String subtitle) {
        JPanel card = panelCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.putClientProperty("formCard", true);
        card.setMinimumSize(new Dimension(460, 0));
        card.setMaximumSize(new Dimension(460, Integer.MAX_VALUE));

        JPanel heading = sectionTitle(title, subtitle);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(26));

        return card;
    }

    private JPanel panelCard() {
        return panelCard(null);
    }

    private JPanel panelCard(LayoutManager layout) {
        JPanel panel = new RoundedPanel(layout == null ? new BorderLayout() : layout, CARD, 18);
        panel.setBorder(new EmptyBorder(28, 30, 30, 30));
        return panel;
    }

    private JPanel sectionTitle(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(FONT_TITLE);
        JLabel s = new JLabel(subtitle);
        s.setForeground(MUTED);
        s.setFont(FONT_SUBTITLE);
        panel.add(t);
        panel.add(Box.createVerticalStrut(5));
        panel.add(s);
        return panel;
    }

    private JPanel fieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label.toUpperCase());
        l.setForeground(MUTED);
        l.setFont(FONT_LABEL);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(l);
        group.add(Box.createVerticalStrut(8));
        group.add(field);
        return group;
    }

    private JButton primaryButton(String text) {
        return new ModernButton(text, ACCENT, ACCENT_HOVER, Color.WHITE);
    }

    private JButton secondaryButton(String text) {
        return new ModernButton(text, CARD_ALT, new Color(54, 54, 62), TEXT);
    }

    private JTable table(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT);
        table.setBackground(CARD);
        table.setGridColor(new Color(50, 50, 58));
        table.setSelectionBackground(new Color(61, 68, 112));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 44));
        header.setBackground(new Color(39, 39, 46));
        header.setForeground(MUTED);
        header.setFont(FONT_LABEL);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setFont(FONT_BODY);
                setForeground(selected ? Color.WHITE : TEXT);
                setBackground(selected ? table.getSelectionBackground() : (row % 2 == 0 ? CARD : CARD_ALT));
                setHorizontalAlignment(column == 0 || column == 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(90);
        table.getColumnModel().getColumn(3).setMaxWidth(150);
        return table;
    }

    private JScrollPane tableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(CARD);
        scroll.setBackground(CARD);
        return scroll;
    }

    private void navigate(String key) {
        contentLayout.show(contentPanel, key);
        navButtons.forEach((name, button) -> button.setActive(name.equals(key)));
        status("Viewing " + navButtons.get(key).text);
        if ("catalog".equals(key) && !token.isEmpty()) {
            loadBooks();
        }
        if ("logs".equals(key) && !token.isEmpty()) {
            loadIssueLogs();
        }
    }

    private void loadBooks() {
        if (token.isEmpty()) return;
        if (booksTable == null) return;

        runAsync("Loading books...", null, () -> request("GET", "/books", null), response -> {
            DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
            model.setRowCount(0);
            for (Map<String, String> book : parseJsonArray(response)) {
                String status = "true".equalsIgnoreCase(book.getOrDefault("available", "true")) ? "Available" : "Issued";
                model.addRow(new Object[]{
                        book.getOrDefault("id", ""),
                        book.getOrDefault("title", ""),
                        book.getOrDefault("author", ""),
                        status
                });
            }
            toast("Catalog updated.", SUCCESS);
        });
    }

    private void loadIssueLogs() {
        if (token.isEmpty()) return;
        if (issueLogsTable == null) return;

        runAsync("Loading logs...", null, () -> request("GET", "/issues", null), response -> {
            DefaultTableModel model = (DefaultTableModel) issueLogsTable.getModel();
            model.setRowCount(0);
            for (Map<String, String> issue : parseJsonArray(response)) {
                boolean returned = "true".equalsIgnoreCase(issue.getOrDefault("returned", "false"));
                model.addRow(new Object[]{
                        issue.getOrDefault("id", ""),
                        issue.getOrDefault("bookId", ""),
                        issue.getOrDefault("userId", ""),
                        issue.getOrDefault("issueDate", ""),
                        issue.getOrDefault("dueDate", ""),
                        returned ? "Returned" : "Issued",
                        issue.getOrDefault("returnDate", "")
                });
            }
            toast("Logs updated.", SUCCESS);
        });
    }

    private boolean requireAuth() {
        if (token.isEmpty()) {
            toast("Please login first.", ERROR);
            navigate("login");
            return false;
        }
        return true;
    }

    private void logout() {
        token = "";
        loggedInUser = "";
        userLabel.setText("Guest");
        userLabel.setForeground(MUTED);
        logoutButton.setEnabled(false);
        toast("Logged out.", WARNING);
        
        // Hide main nav items, show login, hide logout
        updateNavForAuth(false);
        navigate("login");
    }

    private void updateNavForAuth(boolean loggedIn) {
        // Main nav items: login hidden when logged in, others visible when logged in
        navButtons.get("login").setVisible(!loggedIn);
        navButtons.get("catalog").setVisible(loggedIn);
        navButtons.get("addBook").setVisible(loggedIn);
        navButtons.get("members").setVisible(loggedIn);
        navButtons.get("issue").setVisible(loggedIn);
        navButtons.get("return").setVisible(loggedIn);
        navButtons.get("logs").setVisible(loggedIn);
        
        // Logout nav item: visible only when logged in
        navButtons.get("logout").setVisible(loggedIn);
        
        // Refresh the sidebar layout
        frame.revalidate();
        frame.repaint();
    }

    private void status(String text) {
        statusLabel.setText(text);
    }

    private void loading(boolean active, String message) {
        loader.setVisible(active);
        statusLabel.setText(active ? message : "Ready");
    }

    private <T> void runAsync(String loadingText, JComponent source, Task<T> task, Success<T> success) {
        if (source != null) source.setEnabled(false);
        loading(true, loadingText);

        new SwingWorker<T, Void>() {
            protected T doInBackground() throws Exception {
                return task.run();
            }

            protected void done() {
                try {
                    success.accept(get());
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    toast(cause.getMessage() == null ? "Request failed." : cause.getMessage(), ERROR);
                } finally {
                    if (source != null) source.setEnabled(true);
                    loading(false, "");
                }
            }
        }.execute();
    }

    private String request(String method, String path, String body) throws IOException {
        URL url = new URL(API + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("Accept", "application/json");
        if (!token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        if (body != null) {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        InputStream stream = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
        String response = "";

        if (stream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                response = sb.toString();
            }
        }

        if (code < 200 || code >= 300) {
            throw new IOException(response.isEmpty() ? "HTTP " + code : response);
        }
        return response;
    }

    private void toast(String message, Color color) {
        JWindow toast = new JWindow(frame);
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(FONT_BODY);
        label.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel box = new RoundedPanel(new BorderLayout(), color, 16);
        box.add(label);
        toast.setContentPane(box);
        toast.pack();

        Point p = frame.getLocationOnScreen();
        int x = p.x + frame.getWidth() - toast.getWidth() - 32;
        int y = p.y + frame.getHeight() - toast.getHeight() - 42;
        toast.setLocation(x, y);
        toast.setAlwaysOnTop(true);
        toast.setVisible(true);

        Timer timer = new Timer(2600, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    private static String jsonEscape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String extractJsonString(String json, String key) {
        String needle = "\"" + key + "\"";
        int keyIndex = json.indexOf(needle);
        if (keyIndex < 0) return "";
        int colon = json.indexOf(':', keyIndex);
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (colon < 0 || firstQuote < 0 || secondQuote < 0) return "";
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static String extractJsonValue(String json, String key) {
        String needle = "\"" + key + "\"";
        int keyIndex = json.indexOf(needle);
        if (keyIndex < 0) return "";
        int colon = json.indexOf(':', keyIndex);
        if (colon < 0) return "";
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length()) return "";
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return end < 0 ? "" : json.substring(start + 1, end);
        }
        int end = start;
        while (end < json.length() && ",}".indexOf(json.charAt(end)) < 0) end++;
        String value = json.substring(start, end).trim();
        return "null".equalsIgnoreCase(value) ? "" : value;
    }

    private static List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> rows = new ArrayList<>();
        String trimmed = json.trim();
        if (trimmed.length() < 2) return rows;

        int index = 0;
        while (index < trimmed.length()) {
            int start = trimmed.indexOf('{', index);
            if (start < 0) break;
            int end = findObjectEnd(trimmed, start);
            if (end < 0) break;
            rows.add(parseJsonObject(trimmed.substring(start + 1, end)));
            index = end + 1;
        }
        return rows;
    }

    private static int findObjectEnd(String text, int start) {
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;
        for (int i = start; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == '"') {
                inString = !inString;
            } else if (!inString) {
                if (ch == '{') depth++;
                if (ch == '}') depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static Map<String, String> parseJsonObject(String object) {
        Map<String, String> map = new LinkedHashMap<>();
        boolean inString = false;
        boolean escaped = false;
        StringBuilder part = new StringBuilder();
        List<String> pairs = new ArrayList<>();

        for (int i = 0; i < object.length(); i++) {
            char ch = object.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == '"') {
                inString = !inString;
            }

            if (ch == ',' && !inString) {
                pairs.add(part.toString());
                part.setLength(0);
            } else {
                part.append(ch);
            }
        }
        pairs.add(part.toString());

        for (String pair : pairs) {
            int colon = pair.indexOf(':');
            if (colon < 0) continue;
            String key = cleanJsonValue(pair.substring(0, colon));
            String value = cleanJsonValue(pair.substring(colon + 1));
            map.put(key, value);
        }
        return map;
    }

    private static String cleanJsonValue(String value) {
        String cleaned = value.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private interface Task<T> {
        T run() throws Exception;
    }

    private interface Success<T> {
        void accept(T value) throws Exception;
    }

    private static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        RoundedPanel(LayoutManager layout, Color color, int radius) {
            super(layout);
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernButton extends JButton {
        private final Color normal;
        private final Color hover;
        private boolean hovered;

        ModernButton(String text, Color normal, Color hover, Color foreground) {
            super(text);
            this.normal = normal;
            this.hover = hover;
            setForeground(foreground);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(150, 44));
            setMaximumSize(new Dimension(220, 44));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color paint = isEnabled() ? (hovered ? hover : normal) : new Color(70, 70, 78);
            g2.setColor(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NavButton extends JButton {
        private final String icon;
        private final String text;
        private boolean active;
        private boolean hovered;

        NavButton(String icon, String text) {
            super(icon + "   " + text);
            this.icon = icon;
            this.text = text;
            setHorizontalAlignment(SwingConstants.LEFT);
            setForeground(MUTED);
            setFont(FONT_BODY);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 18, 0, 18));
            setPreferredSize(new Dimension(210, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? Color.WHITE : MUTED);
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active || hovered) {
                g2.setColor(active ? new Color(72, 84, 155) : new Color(38, 38, 44));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
            if (active) {
                g2.setColor(ACCENT_HOVER);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PlaceholderField extends JTextField {
        private final String placeholder;
        private boolean focused;

        PlaceholderField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setBackground(FIELD);
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setPreferredSize(new Dimension(400, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setOpaque(false);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            paintField(g, this, focused);
            super.paintComponent(g);
            if (getText().isEmpty() && !focused) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(118, 118, 128));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 16, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        }
    }

    private static class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean focused;

        PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setBackground(FIELD);
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setPreferredSize(new Dimension(400, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setOpaque(false);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            paintField(g, this, focused);
            super.paintComponent(g);
            if (getPassword().length == 0 && !focused) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(118, 118, 128));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 16, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        }
    }

    private static void paintField(Graphics g, JComponent c, boolean focused) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(FIELD);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 16, 16);
        g2.setColor(focused ? ACCENT : BORDER);
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 16, 16);
        g2.dispose();
    }
}
