// Professional Hotel Management System - Fixed for 14" Laptop Screens
// Optimized for 1920x1080 resolution on Dell Latitude 5490

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HotelManagementSystem extends JFrame {
    
    // === PROFESSIONAL COLOR SCHEME ===
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);      // Professional Blue
    private static final Color SECONDARY_COLOR = new Color(30, 50, 90);     // Dark Blue
    private static final Color SUCCESS_COLOR = new Color(40, 180, 70);      // Green
    private static final Color DANGER_COLOR = new Color(220, 70, 70);       // Red
    private static final Color WARNING_COLOR = new Color(255, 185, 0);      // Amber
    private static final Color LIGHT_BG = new Color(245, 247, 250);         // Light Gray
    private static final Color CARD_BG = Color.WHITE;
    
    // === DB Config ===
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotelreservation?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Aiman@123";
    
    // === UI Components ===
    private JTable roomTable, bookingTable, customerTable;
    private DefaultTableModel roomModel, bookingModel, customerModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel totalRoomsValue, availableRoomsValue, occupiedRoomsValue, 
                   activeBookingsValue, todayRevenueValue, totalCustomersValue;
    private JLabel currentDateLabel, currentTimeLabel;
    
    public HotelManagementSystem() {
        super("🏨 Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // === FIX FOR SMALL SCREEN ===
        // Disable automatic font scaling
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.dpiaware", "true");
        
        // Set explicit size for 14-inch laptop
        setSize(1280, 720); // Smaller than full HD for 14-inch screen
        setLocationRelativeTo(null);
        setResizable(true);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set smaller fonts for 14-inch screen
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 11);
            Font headerFont = new Font("Segoe UI", Font.BOLD, 13);
            
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", headerFont);
            UIManager.put("ComboBox.font", defaultFont);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadAllData();
        startClock();
        setVisible(true);
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // === HEADER BAR (SMALLER) ===
        JPanel headerBar = createHeaderBar();
        add(headerBar, BorderLayout.NORTH);
        
        // === MAIN CONTAINER ===
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(LIGHT_BG);
        
        // === SIDEBAR (THINNER) ===
        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);
        
        // === CONTENT AREA WITH SCROLL PANE ===
        JScrollPane contentScrollPane = new JScrollPane();
        contentScrollPane.setBorder(null);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(LIGHT_BG);
        contentArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(LIGHT_BG);
        
        mainPanel.add(createDashboardPanel(), "dashboard");
        mainPanel.add(createRoomsPanel(), "rooms");
        mainPanel.add(createBookingsPanel(), "bookings");
        mainPanel.add(createCustomersPanel(), "customers");
        
        contentArea.add(mainPanel, BorderLayout.CENTER);
        contentScrollPane.setViewportView(contentArea);
        mainContainer.add(contentScrollPane, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
        
        // === STATUS BAR ===
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SECONDARY_COLOR);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(8, 15, 8, 15)
        ));
        header.setPreferredSize(new Dimension(getWidth(), 60));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel logo = new JLabel("🏨");
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        logo.setForeground(Color.WHITE);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        textPanel.setOpaque(false);
        
        JLabel title = new JLabel("HOTEL MANAGEMENT SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Dell Latitude 5490 - Optimized");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitle.setForeground(new Color(200, 200, 200));
        
        textPanel.add(title);
        textPanel.add(subtitle);
        
        titlePanel.add(logo);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(textPanel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JPanel timePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        timePanel.setOpaque(false);
        
        currentDateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));
        currentDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        currentDateLabel.setForeground(Color.WHITE);
        
        currentTimeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        currentTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        currentTimeLabel.setForeground(new Color(200, 200, 200));
        
        timePanel.add(currentDateLabel);
        timePanel.add(currentTimeLabel);
        
        rightPanel.add(timePanel);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));
        sidebar.setPreferredSize(new Dimension(200, 0)); // Thinner sidebar
        sidebar.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        String[][] navItems = {
            {"📊", "Dashboard", "dashboard"},
            {"🏨", "Rooms", "rooms"},
            {"📅", "Bookings", "bookings"},
            {"👥", "Customers", "customers"}
        };
        
        for (String[] item : navItems) {
            JButton navBtn = createNavButton(item[0], item[1], item[2]);
            sidebar.add(navBtn);
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        JPanel quickActions = new JPanel();
        quickActions.setLayout(new BoxLayout(quickActions, BoxLayout.Y_AXIS));
        quickActions.setBackground(new Color(50, 50, 50));
        quickActions.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(70, 70, 70)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel quickTitle = new JLabel("QUICK ACTIONS");
        quickTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        quickTitle.setForeground(new Color(150, 150, 150));
        quickTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActions.add(quickTitle);
        quickActions.add(Box.createVerticalStrut(8));
        
        JButton newBookingBtn = createQuickActionButton("➕ New Booking", SUCCESS_COLOR);
        newBookingBtn.addActionListener(e -> createNewBooking());
        quickActions.add(newBookingBtn);
        quickActions.add(Box.createVerticalStrut(5));
        
        JButton checkInBtn = createQuickActionButton("🔑 Check-In", PRIMARY_COLOR);
        checkInBtn.addActionListener(e -> performCheckIn());
        quickActions.add(checkInBtn);
        quickActions.add(Box.createVerticalStrut(5));
        
        JButton checkOutBtn = createQuickActionButton("🚪 Check-Out", WARNING_COLOR);
        checkOutBtn.addActionListener(e -> {
            try {
                performCheckOut();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        quickActions.add(checkOutBtn);
        
        sidebar.add(quickActions);
        
        return sidebar;
    }
    
    private JButton createNavButton(String icon, String text, String action) {
        JButton button = new JButton("<html><div style='text-align: left; padding: 0px;'>" + 
                                    icon + "  <span style='font-size: 12px;'>" + text + "</span></div></html>");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(220, 220, 220));
        button.setBackground(new Color(40, 40, 40));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 45));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.addActionListener(e -> {
            cardLayout.show(mainPanel, action);
            highlightNavButton(button);
            if (action.equals("dashboard")) refreshDashboard();
            if (action.equals("rooms")) loadRooms();
            if (action.equals("bookings")) loadBookings();
            if (action.equals("customers")) loadCustomers();
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(new Color(70, 70, 70));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(new Color(40, 40, 40));
                }
            }
        });
        
        return button;
    }
    
    private JButton createQuickActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new CompoundBorder(
            new LineBorder(color.darker(), 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(170, 35));
        button.setUI(new BasicButtonUI());
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void highlightNavButton(JButton activeButton) {
        for (Component comp : activeButton.getParent().getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(new Color(40, 40, 40));
                btn.setForeground(new Color(220, 220, 220));
            }
        }
        activeButton.setBackground(PRIMARY_COLOR);
        activeButton.setForeground(Color.WHITE);
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 15));
        dashboard.setBackground(LIGHT_BG);
        dashboard.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Dashboard Overview");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.BLACK);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setForeground(PRIMARY_COLOR);
        refreshBtn.setBackground(Color.WHITE);
        refreshBtn.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        dashboard.add(header, BorderLayout.NORTH);
        
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        statsGrid.add(createStatCardWithLabel("Total Rooms", "🏨", PRIMARY_COLOR, "All rooms in hotel", true));
        statsGrid.add(createStatCardWithLabel("Available Rooms", "✅", SUCCESS_COLOR, "Ready for booking", false));
        statsGrid.add(createStatCardWithLabel("Occupied Rooms", "🛏️", DANGER_COLOR, "Currently occupied", false));
        statsGrid.add(createStatCardWithLabel("Today's Revenue", "💰", WARNING_COLOR, "Income today", false));
        statsGrid.add(createStatCardWithLabel("Active Bookings", "📅", new Color(150, 100, 200), "Current bookings", false));
        statsGrid.add(createStatCardWithLabel("Total Customers", "👥", new Color(0, 150, 150), "Registered guests", false));
        
        dashboard.add(statsGrid, BorderLayout.CENTER);
        
        JPanel recentPanel = new JPanel(new BorderLayout(0, 10));
        recentPanel.setBackground(CARD_BG);
        recentPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        
        JLabel recentTitle = new JLabel("📋 Recent Bookings");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        recentTitle.setForeground(Color.BLACK);
        
        JButton viewAllBtn = new JButton("View All →");
        viewAllBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        viewAllBtn.setForeground(PRIMARY_COLOR);
        viewAllBtn.setBackground(Color.WHITE);
        viewAllBtn.setBorder(new EmptyBorder(4, 12, 4, 12));
        viewAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "bookings");
            highlightNavButton((JButton) sidebar().getComponent(2));
        });
        
        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAllBtn, BorderLayout.EAST);
        
        String[] columns = {"Booking ID", "Customer", "Room", "Check-In", "Check-Out", "Amount", "Status"};
        bookingModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(bookingModel);
        styleTable(bookingTable);
        bookingTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        recentPanel.add(recentHeader, BorderLayout.NORTH);
        recentPanel.add(scrollPane, BorderLayout.CENTER);
        
        dashboard.add(recentPanel, BorderLayout.SOUTH);
        
        return dashboard;
    }
    
    private JPanel createStatCardWithLabel(String title, String icon, Color color, String description, boolean isTotalRooms) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_BG);
        card.setOpaque(true);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(240, 240, 240), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.BLACK);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // FIXED: Ensure numbers are visible with high contrast
        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Increased font size
        valueLabel.setForeground(color.darker()); // Darker for better contrast
        valueLabel.setOpaque(false);
        
        // Store reference
        if (title.equals("Total Rooms")) {
            totalRoomsValue = valueLabel;
        } else if (title.equals("Available Rooms")) {
            availableRoomsValue = valueLabel;
        } else if (title.equals("Occupied Rooms")) {
            occupiedRoomsValue = valueLabel;
        } else if (title.equals("Today's Revenue")) {
            todayRevenueValue = valueLabel;
        } else if (title.equals("Active Bookings")) {
            activeBookingsValue = valueLabel;
        } else if (title.equals("Total Customers")) {
            totalCustomersValue = valueLabel;
        }
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(new Color(100, 100, 100));
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LIGHT_BG);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("🏨 Room Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(100, 100, 100));
        refreshBtn.addActionListener(e -> loadRooms());
        
        JButton addRoomBtn = createActionButton("➕ Add Room", SUCCESS_COLOR);
        addRoomBtn.addActionListener(e -> showAddRoomDialog());
        
        JButton editPriceBtn = createActionButton("💰 Edit Price", WARNING_COLOR);
        editPriceBtn.addActionListener(e -> showEditPriceDialog());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(editPriceBtn);
        buttonPanel.add(addRoomBtn);
        
        header.add(title, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Room ID", "Type", "Price/Night", "Status", "Actions"};
        roomModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        roomTable = new JTable(roomModel);
        styleTable(roomTable);
        roomTable.setRowHeight(30);
        
        roomTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        roomTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Manage"));
        roomTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel totalLabel = new JLabel("Total Rooms: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        totalLabel.setForeground(Color.BLACK);
        
        JLabel availableLabel = new JLabel("Available: 0");
        availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        availableLabel.setForeground(SUCCESS_COLOR);
        
        JLabel occupiedLabel = new JLabel("Occupied: 0");
        occupiedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        occupiedLabel.setForeground(DANGER_COLOR);
        
        infoPanel.add(totalLabel);
        infoPanel.add(availableLabel);
        infoPanel.add(occupiedLabel);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LIGHT_BG);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("📅 Booking Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        
        JButton newBookingBtn = createActionButton("➕ New Booking", SUCCESS_COLOR);
        newBookingBtn.addActionListener(e -> createNewBooking());
        
        JButton checkInBtn = createActionButton("🔑 Check-In", PRIMARY_COLOR);
        checkInBtn.addActionListener(e -> performCheckIn());
        
        JButton checkOutBtn = createActionButton("🚪 Check-Out", WARNING_COLOR);
        checkOutBtn.addActionListener(e -> {
            try {
                performCheckOut();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        
        JButton cancelBtn = createActionButton("❌ Cancel", DANGER_COLOR);
        cancelBtn.addActionListener(e -> cancelBooking());
        
        buttonPanel.add(newBookingBtn);
        buttonPanel.add(checkInBtn);
        buttonPanel.add(checkOutBtn);
        buttonPanel.add(cancelBtn);
        
        header.add(title, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Booking ID", "Room ID", "Customer", "Check-In", "Check-Out", "Nights", "Total", "Status"};
        bookingModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(bookingModel);
        styleTable(bookingTable);
        bookingTable.setRowHeight(30);
        
        bookingTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LIGHT_BG);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("👥 Customer Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        
        JButton addCustomerBtn = createActionButton("➕ Add Customer", SUCCESS_COLOR);
        addCustomerBtn.addActionListener(e -> showAddCustomerDialog());
        
        JButton refreshBtn = createActionButton("🔄 Refresh", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadCustomers());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addCustomerBtn);
        
        header.add(title, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Customer ID", "Name", "Email", "Phone", "Bookings", "Total Spent", "Status"};
        customerModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(customerModel);
        styleTable(customerTable);
        customerTable.setRowHeight(30);
        
        customerTable.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(6, 15, 6, 15)
        ));
        statusBar.setPreferredSize(new Dimension(getWidth(), 30));
        
        JLabel statusLabel = new JLabel("✅ System Connected | Database: hotelreservation");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(80, 80, 80));
        
        JLabel userLabel = new JLabel("User: Admin | Session: Active");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userLabel.setForeground(new Color(80, 80, 80));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new CompoundBorder(
            new LineBorder(color.darker(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new BasicButtonUI());
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                } else {
                    c.setBackground(new Color(220, 240, 255));
                    c.setForeground(Color.BLACK);
                }
                
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(6, 5, 6, 5)
        ));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 12, 0, 0));
        
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        }
        if (table.getColumnCount() > 2) {
            table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        }
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 0 && i != 2) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
    
    // === DATABASE OPERATIONS ===
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    
    private void loadAllData() {
        loadRooms();
        loadBookings();
        loadCustomers();
        updateDashboardStats();
    }
    
    private void loadRooms() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Rooms ORDER BY RoomID")) {
            
            roomModel.setRowCount(0);
            int total = 0, available = 0, occupied = 0;
            
            while (rs.next()) {
                total++;
                boolean isAvailable = rs.getBoolean("IsAvailable");
                if (isAvailable) available++;
                else occupied++;
                
                String status = isAvailable ? "Available" : "Occupied";
                roomModel.addRow(new Object[]{
                    rs.getInt("RoomID"),
                    rs.getString("RoomType"),
                    "$" + String.format("%.2f", rs.getDouble("PricePerDay")),
                    status,
                    "Manage"
                });
            }
            
            updateRoomInfoPanel(total, available, occupied);
            
        } catch (SQLException e) {
            showError("Error loading rooms: " + e.getMessage());
        }
    }
    
    private void loadBookings() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT b.*, r.RoomType FROM Bookings b " +
                 "JOIN Rooms r ON b.RoomID = r.RoomID " +
                 "ORDER BY b.BookingID DESC LIMIT 20")) { // Limit for small screen
            
            bookingModel.setRowCount(0);
            
            while (rs.next()) {
                long days = ChronoUnit.DAYS.between(
                    rs.getDate("CheckInDate").toLocalDate(),
                    rs.getDate("CheckOutDate").toLocalDate()
                );
                
                String status = rs.getDate("CheckOutDate").toLocalDate().isAfter(LocalDate.now()) 
                    ? "Active" : "Completed";
                
                bookingModel.addRow(new Object[]{
                    rs.getInt("BookingID"),
                    rs.getInt("RoomID"),
                    rs.getString("CustomerName"),
                    rs.getDate("CheckInDate").toString(),
                    rs.getDate("CheckOutDate").toString(),
                    days,
                    "$" + String.format("%.2f", rs.getDouble("TotalRent")),
                    status
                });
            }
            
        } catch (SQLException e) {
            showError("Error loading bookings: " + e.getMessage());
        }
    }
    
    private void loadCustomers() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT CustomerName, COUNT(*) as bookings, SUM(TotalRent) as total " +
                 "FROM Bookings GROUP BY CustomerName ORDER BY CustomerName LIMIT 20")) { // Limit for small screen
            
            customerModel.setRowCount(0);
            int id = 1;
            
            while (rs.next()) {
                customerModel.addRow(new Object[]{
                    "CUST" + String.format("%03d", id++),
                    rs.getString("CustomerName"),
                    generateEmail(rs.getString("CustomerName")),
                    generatePhone(),
                    rs.getInt("bookings"),
                    "$" + String.format("%.2f", rs.getDouble("total")),
                    "Active"
                });
            }
            
        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }
    
    private void updateDashboardStats() {
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Rooms");
            if (rs.next() && totalRoomsValue != null) {
                totalRoomsValue.setText(String.valueOf(rs.getInt(1)));
            }
            
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Rooms WHERE IsAvailable = 1");
            if (rs.next() && availableRoomsValue != null) {
                availableRoomsValue.setText(String.valueOf(rs.getInt(1)));
            }
            
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Rooms WHERE IsAvailable = 0");
            if (rs.next() && occupiedRoomsValue != null) {
                occupiedRoomsValue.setText(String.valueOf(rs.getInt(1)));
            }
            
            rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Bookings WHERE CheckInDate <= CURDATE() AND CheckOutDate >= CURDATE()");
            if (rs.next() && activeBookingsValue != null) {
                activeBookingsValue.setText(String.valueOf(rs.getInt(1)));
            }
            
            rs = conn.createStatement().executeQuery(
                "SELECT SUM(TotalRent) FROM Bookings WHERE CheckInDate <= CURDATE() AND CheckOutDate >= CURDATE()");
            if (rs.next() && todayRevenueValue != null) {
                double revenue = rs.getDouble(1);
                todayRevenueValue.setText("$" + String.format("%.2f", revenue));
            }
            
            rs = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT CustomerName) FROM Bookings");
            if (rs.next() && totalCustomersValue != null) {
                totalCustomersValue.setText(String.valueOf(rs.getInt(1)));
            }
            
        } catch (SQLException e) {
            showError("Error updating stats: " + e.getMessage());
        }
    }
    
    private void updateRoomInfoPanel(int total, int available, int occupied) {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component child : panel.getComponents()) {
                    if (child instanceof JPanel) {
                        JPanel childPanel = (JPanel) child;
                        if (childPanel.getComponentCount() > 0) {
                            Component grandChild = childPanel.getComponent(0);
                            if (grandChild instanceof JLabel && 
                                ((JLabel) grandChild).getText().contains("🏨 Room Management")) {
                                Component[] bottomComps = panel.getComponents();
                                for (Component bottomComp : bottomComps) {
                                    if (bottomComp instanceof JPanel && 
                                        ((JPanel) bottomComp).getComponentCount() > 0) {
                                        JPanel infoPanel = (JPanel) bottomComp;
                                        infoPanel.removeAll();
                                        
                                        JLabel totalLabel = new JLabel("Total Rooms: " + total);
                                        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                        totalLabel.setForeground(Color.BLACK);
                                        
                                        JLabel availableLabel = new JLabel("Available: " + available);
                                        availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                                        availableLabel.setForeground(SUCCESS_COLOR);
                                        
                                        JLabel occupiedLabel = new JLabel("Occupied: " + occupied);
                                        occupiedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                                        occupiedLabel.setForeground(DANGER_COLOR);
                                        
                                        infoPanel.add(totalLabel);
                                        infoPanel.add(Box.createHorizontalStrut(15));
                                        infoPanel.add(availableLabel);
                                        infoPanel.add(Box.createHorizontalStrut(15));
                                        infoPanel.add(occupiedLabel);
                                        
                                        infoPanel.revalidate();
                                        infoPanel.repaint();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // === DIALOG METHODS (FIXED FOR SMALL SCREEN) ===
    
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Add New Room", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 320); // Smaller for 14-inch
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel title = new JLabel("Add New Room");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);
        
        String[] labels = {"Room Number:", "Room Type:", "Price per Night:"};
        JComponent[] fields = new JComponent[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(Color.BLACK);
            content.add(label, gbc);
            
            if (i == 1) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                combo.setPreferredSize(new Dimension(180, 28));
                fields[i] = combo;
            } else {
                JTextField field = new JTextField(12);
                field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(6, 8, 6, 8)
                ));
                fields[i] = field;
            }
            
            gbc.gridx = 1;
            content.add(fields[i], gbc);
            gbc.gridx = 0;
        }
        
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        content.add(Box.createVerticalStrut(10), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Save Room");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(SUCCESS_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(110, 32));
        saveBtn.setBorder(new EmptyBorder(6, 15, 6, 15));
        saveBtn.addActionListener(e -> {
            try {
                int roomId = Integer.parseInt(((JTextField) fields[0]).getText());
                String roomType = (String) ((JComboBox<?>) fields[1]).getSelectedItem();
                double price = Double.parseDouble(((JTextField) fields[2]).getText());
                
                addRoomToDatabase(roomId, roomType, price);
                
                JOptionPane.showMessageDialog(dialog, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadRooms();
                updateDashboardStats();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridy = labels.length + 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        content.add(buttonPanel, gbc);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void createNewBooking() {
        List<String> availableRooms = getAvailableRooms();
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rooms available!", "No Rooms", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Create New Booking", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400); // Smaller for 14-inch
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel title = new JLabel("New Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);
        
        JTextField customerField = new JTextField(15);
        JComboBox<String> roomCombo = new JComboBox<>(availableRooms.toArray(new String[0]));
        roomCombo.setPreferredSize(new Dimension(220, 28));
        JTextField checkInField = new JTextField(LocalDate.now().toString(), 12);
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(2).toString(), 12);
        
        String[] labels = {"Customer Name:", "Select Room:", "Check-In Date:", "Check-Out Date:"};
        JComponent[] fields = {customerField, roomCombo, checkInField, checkOutField};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(Color.BLACK);
            content.add(label, gbc);
            
            if (fields[i] instanceof JTextField) {
                ((JTextField) fields[i]).setFont(new Font("Segoe UI", Font.PLAIN, 12));
                ((JTextField) fields[i]).setBorder(new CompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(6, 8, 6, 8)
                ));
            } else if (fields[i] instanceof JComboBox) {
                ((JComboBox<?>) fields[i]).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
            
            gbc.gridx = 1;
            content.add(fields[i], gbc);
            gbc.gridx = 0;
        }
        
        JLabel priceLabel = new JLabel("Price will be calculated after selection");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        priceLabel.setForeground(new Color(100, 100, 100));
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        content.add(priceLabel, gbc);
        
        gbc.gridy = labels.length + 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        content.add(Box.createVerticalStrut(15), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Create Booking");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(SUCCESS_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 32));
        saveBtn.setBorder(new EmptyBorder(6, 15, 6, 15));
        saveBtn.addActionListener(e -> {
            try {
                String customerName = customerField.getText().trim();
                if (customerName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter customer name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String selectedRoom = (String) roomCombo.getSelectedItem();
                int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
                LocalDate checkIn = LocalDate.parse(checkInField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText());
                
                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(dialog, "Check-out date must be after check-in", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                createBookingInDatabase(customerName, roomId, checkIn, checkOut);
                
                JOptionPane.showMessageDialog(dialog, "Booking created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadRooms();
                loadBookings();
                loadCustomers();
                updateDashboardStats();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridy = labels.length + 3;
        gbc.weighty = 0;
        content.add(buttonPanel, gbc);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel title = new JLabel("Add Customer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);
        
        String[] labels = {"Full Name:", "Email:", "Phone:"};
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(Color.BLACK);
            content.add(label, gbc);
            
            fields[i] = new JTextField(12);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fields[i].setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(6, 8, 6, 8)
            ));
            
            gbc.gridx = 1;
            content.add(fields[i], gbc);
            gbc.gridx = 0;
        }
        
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        content.add(Box.createVerticalStrut(10), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Save Customer");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(SUCCESS_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(110, 32));
        saveBtn.setBorder(new EmptyBorder(6, 15, 6, 15));
        saveBtn.addActionListener(e -> {
            String name = fields[0].getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter customer name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(dialog, 
                "Customer '" + name + "' will be added when creating a booking.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridy = labels.length + 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        content.add(buttonPanel, gbc);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showEditPriceDialog() {
        JDialog dialog = new JDialog(this, "Update Room Price", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel title = new JLabel("Update Room Price");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);
        
        JLabel roomLabel = new JLabel("Room ID:");
        roomLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        content.add(roomLabel, gbc);
        
        JTextField roomField = new JTextField(10);
        roomField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roomField.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        gbc.gridx = 1;
        content.add(roomField, gbc);
        
        JLabel priceLabel = new JLabel("New Price:");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        content.add(priceLabel, gbc);
        
        JTextField priceField = new JTextField(10);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceField.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        gbc.gridx = 1;
        content.add(priceField, gbc);
        
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        content.add(Box.createVerticalStrut(10), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setPreferredSize(new Dimension(80, 30));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Update");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(WARNING_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(90, 30));
        saveBtn.addActionListener(e -> {
            try {
                int roomId = Integer.parseInt(roomField.getText());
                double newPrice = Double.parseDouble(priceField.getText());
                
                updateRoomPrice(roomId, newPrice);
                JOptionPane.showMessageDialog(dialog, "Room price updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadRooms();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        content.add(buttonPanel, gbc);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void performCheckIn() {
        String bookingId = JOptionPane.showInputDialog(this, 
            "Enter Booking ID to Check-In:", "Check-In", JOptionPane.PLAIN_MESSAGE);
        
        if (bookingId != null && !bookingId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(bookingId);
                if (validateBooking(id, true)) {
                    try (Connection conn = getConnection()) {
                        int roomId = 0;
                        try (PreparedStatement stmt = conn.prepareStatement(
                            "SELECT RoomID FROM Bookings WHERE BookingID = ?")) {
                            stmt.setInt(1, id);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                roomId = rs.getInt("RoomID");
                            }
                        }
                        
                        if (roomId > 0) {
                            try (PreparedStatement stmt = conn.prepareStatement(
                                "UPDATE Rooms SET IsAvailable = 0 WHERE RoomID = ?")) {
                                stmt.setInt(1, roomId);
                                stmt.executeUpdate();
                            }
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        "Check-In successful for Booking ID: " + bookingId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRooms();
                    updateDashboardStats();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Booking ID", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performCheckOut() throws SQLException {
        String bookingId = JOptionPane.showInputDialog(this, 
            "Enter Booking ID to Check-Out:", "Check-Out", JOptionPane.PLAIN_MESSAGE);
        
        if (bookingId != null && !bookingId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(bookingId);
                if (validateBooking(id, false)) {
                    checkOutBooking(id);
                    JOptionPane.showMessageDialog(this,
                        "Check-Out successful for Booking ID: " + bookingId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRooms();
                    loadBookings();
                    updateDashboardStats();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Booking ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelBooking() {
        String bookingId = JOptionPane.showInputDialog(this, 
            "Enter Booking ID to Cancel:", "Cancel Booking", JOptionPane.PLAIN_MESSAGE);
        
        if (bookingId != null && !bookingId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel Booking ID: " + bookingId + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(bookingId);
                    cancelBookingInDatabase(id);
                    JOptionPane.showMessageDialog(this,
                        "Booking cancelled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRooms();
                    loadBookings();
                    loadCustomers();
                    updateDashboardStats();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // === DATABASE CRUD OPERATIONS ===
    
    private void addRoomToDatabase(int roomId, String roomType, double price) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Rooms (RoomID, RoomType, PricePerDay, IsAvailable) VALUES (?, ?, ?, ?)")) {
            
            stmt.setInt(1, roomId);
            stmt.setString(2, roomType);
            stmt.setDouble(3, price);
            stmt.setBoolean(4, true);
            stmt.executeUpdate();
        }
    }
    
    private void createBookingInDatabase(String customerName, int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            double pricePerDay = 0;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT PricePerDay FROM Rooms WHERE RoomID = ?")) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    pricePerDay = rs.getDouble("PricePerDay");
                } else {
                    throw new SQLException("Room not found");
                }
            }
            
            long days = ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalRent = days * pricePerDay;
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Bookings (RoomID, CustomerName, CheckInDate, CheckOutDate, TotalRent) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setInt(1, roomId);
                stmt.setString(2, customerName);
                stmt.setDate(3, Date.valueOf(checkIn));
                stmt.setDate(4, Date.valueOf(checkOut));
                stmt.setDouble(5, totalRent);
                stmt.executeUpdate();
            }
            
            LocalDate today = LocalDate.now();
            if (!checkIn.isAfter(today)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Rooms SET IsAvailable = 0 WHERE RoomID = ?")) {
                    stmt.setInt(1, roomId);
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
        }
    }
    
    private void updateRoomPrice(int roomId, double newPrice) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE Rooms SET PricePerDay = ? WHERE RoomID = ?")) {
            
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, roomId);
            int rows = stmt.executeUpdate();
            
            if (rows == 0) {
                throw new SQLException("Room not found");
            }
        }
    }
    
    private List<String> getAvailableRooms() {
        List<String> rooms = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT RoomID, RoomType, PricePerDay FROM Rooms WHERE IsAvailable = 1 ORDER BY RoomID")) {
            
            while (rs.next()) {
                rooms.add(rs.getInt("RoomID") + " - " + rs.getString("RoomType") + 
                         " ($" + String.format("%.2f", rs.getDouble("PricePerDay")) + "/night)");
            }
        } catch (SQLException e) {
            showError("Error loading available rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    private boolean validateBooking(int bookingId, boolean checkIn) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM Bookings WHERE BookingID = ?")) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Booking not found", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            Date checkOutDate = rs.getDate("CheckOutDate");
            if (checkOutDate.toLocalDate().isBefore(LocalDate.now()) && checkIn) {
                JOptionPane.showMessageDialog(this, "Booking has expired", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
            
        } catch (SQLException e) {
            showError("Error validating booking: " + e.getMessage());
            return false;
        }
    }
    
    private void checkOutBooking(int bookingId) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            int roomId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT RoomID FROM Bookings WHERE BookingID = ?")) {
                stmt.setInt(1, bookingId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    roomId = rs.getInt("RoomID");
                } else {
                    throw new SQLException("Booking not found");
                }
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Rooms SET IsAvailable = 1 WHERE RoomID = ?")) {
                stmt.setInt(1, roomId);
                stmt.executeUpdate();
            }
            
            conn.commit();
        }
    }
    
    private void cancelBookingInDatabase(int bookingId) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            int roomId = 0;
            Date checkInDate = null;
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT RoomID, CheckInDate FROM Bookings WHERE BookingID = ?")) {
                stmt.setInt(1, bookingId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    roomId = rs.getInt("RoomID");
                    checkInDate = rs.getDate("CheckInDate");
                } else {
                    throw new SQLException("Booking not found");
                }
            }
            
            int rowsAffected;
            try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Bookings WHERE BookingID = ?")) {
                stmt.setInt(1, bookingId);
                rowsAffected = stmt.executeUpdate();
            }
            
            if (rowsAffected == 0) {
                throw new SQLException("No booking was deleted");
            }
            
            LocalDate today = LocalDate.now();
            if (checkInDate != null && !checkInDate.toLocalDate().isAfter(today)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Rooms SET IsAvailable = 1 WHERE RoomID = ?")) {
                    stmt.setInt(1, roomId);
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            throw new SQLException("Error cancelling booking: " + e.getMessage(), e);
        }
    }
    
    // === HELPER METHODS ===
    
    private JPanel sidebar() {
        return (JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(0);
    }
    
    private void refreshDashboard() {
        loadRooms();
        loadBookings();
        loadCustomers();
        updateDashboardStats();
    }
    
    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            currentTimeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            currentDateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));
        });
        timer.start();
    }
    
    private String generateEmail(String name) {
        return name.toLowerCase().replace(" ", ".") + "@email.com";
    }
    
    private String generatePhone() {
        return "+1 (555) " + (100 + (int)(Math.random() * 900)) + "-" + (1000 + (int)(Math.random() * 9000));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // === CUSTOM RENDERERS ===
    
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel label = (JLabel) c;
            
            if (value != null) {
                String status = value.toString();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                
                if (status.equals("Available") || status.equals("Active")) {
                    label.setForeground(SUCCESS_COLOR);
                    label.setBorder(new CompoundBorder(
                        new LineBorder(SUCCESS_COLOR, 1),
                        new EmptyBorder(3, 6, 3, 6)
                    ));
                    label.setBackground(new Color(SUCCESS_COLOR.getRed(), SUCCESS_COLOR.getGreen(), SUCCESS_COLOR.getBlue(), 20));
                } else if (status.equals("Occupied") || status.equals("Pending")) {
                    label.setForeground(WARNING_COLOR);
                    label.setBorder(new CompoundBorder(
                        new LineBorder(WARNING_COLOR, 1),
                        new EmptyBorder(3, 6, 3, 6)
                    ));
                    label.setBackground(new Color(WARNING_COLOR.getRed(), WARNING_COLOR.getGreen(), WARNING_COLOR.getBlue(), 20));
                } else if (status.equals("Completed") || status.equals("Cancelled")) {
                    label.setForeground(new Color(100, 100, 100));
                    label.setBorder(new CompoundBorder(
                        new LineBorder(new Color(150, 150, 150), 1),
                        new EmptyBorder(3, 6, 3, 6)
                    ));
                    label.setBackground(new Color(240, 240, 240));
                }
                
                label.setOpaque(true);
            }
            
            return label;
        }
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private String buttonText;
        
        public ButtonRenderer(String text) {
            this.buttonText = text;
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
            setForeground(Color.WHITE);
            setBackground(PRIMARY_COLOR);
            setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_COLOR.darker(), 1),
                new EmptyBorder(4, 10, 4, 10)
            ));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(buttonText);
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText("Manage");
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_COLOR.darker(), 1),
                new EmptyBorder(4, 10, 4, 10)
            ));
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            int row = roomTable.getSelectedRow();
            if (row >= 0) {
                int roomId = Integer.parseInt(roomTable.getValueAt(row, 0).toString());
                showRoomDetails(roomId);
            }
            return button.getText();
        }
        
        private void showRoomDetails(int roomId) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Rooms WHERE RoomID = ?")) {
                
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String status = rs.getBoolean("IsAvailable") ? "Available" : "Occupied";
                    JOptionPane.showMessageDialog(HotelManagementSystem.this,
                        "<html><div style='font-size:12px;'>" +
                        "<h3>Room Details</h3>" +
                        "<b>Room ID:</b> " + roomId + "<br>" +
                        "<b>Type:</b> " + rs.getString("RoomType") + "<br>" +
                        "<b>Price/Night:</b> $" + String.format("%.2f", rs.getDouble("PricePerDay")) + "<br>" +
                        "<b>Status:</b> " + status + "<br>" +
                        "</div></html>",
                        "Room Details - " + roomId, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                showError("Error loading room details: " + e.getMessage());
            }
        }
    }
    
    // === MAIN METHOD ===
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                new HotelManagementSystem();
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                    "MySQL JDBC Driver not found!\nPlease add mysql-connector-java.jar to classpath.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}