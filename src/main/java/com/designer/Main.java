package com.designer;
import com.designer.model.DesignElement;
import com.designer.model.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    private DefaultListModel<String> projectListModel = new DefaultListModel<>();
    private JList<String> projectJList = new JList<>(projectListModel);
    private Map<String, Project> projects = new HashMap<>();
    private Project currentProject = null;
    private JPanel canvas = new JPanel(null);
    private ObjectMapper objectMapper = new ObjectMapper();
    private File storageFile = new File("projects.json");
    private DesignElement selectedElement = null;
    private Point dragOffset;

    public Main() {
        setTitle("Java Vector Canvas Designer");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        FlatDarkLaf.setup();
        loadProjects();
        initUI();
    }

    private void initUI() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 800));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Проекты (2-кликОткрыть)"));
        projectJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openSelectedProject();
            }
        });
        leftPanel.add(new JScrollPane(projectJList), BorderLayout.CENTER);

        JPanel projectBtnPanel = new JPanel(new GridLayout(3, 1));
        JButton btnNew = new JButton("Новый проект");
        JButton btnRename = new JButton("Переименовать");
        JButton btnDelete = new JButton("Удалить проект");
        btnNew.addActionListener(e -> createProject());
        btnRename.addActionListener(e -> renameProject());
        btnDelete.addActionListener(e -> deleteProject());
        projectBtnPanel.add(btnNew);
        projectBtnPanel.add(btnRename);
        projectBtnPanel.add(btnDelete);
        leftPanel.add(projectBtnPanel, BorderLayout.SOUTH);

        canvas.setBackground(Color.WHITE);

        JToolBar topBar = new JToolBar();
        JButton btnSave = new JButton("Сохранить все");
        JButton btnClear = new JButton("Очистить холст");
        JButton btnPreview = new JButton("Предпросмотр");
        btnSave.addActionListener(e -> saveProjects());
        btnClear.addActionListener(e -> clearCanvas());
        btnPreview.addActionListener(e -> showPreview());
        topBar.add(btnSave);
        topBar.add(btnClear);
        topBar.add(btnPreview);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.setBorder(BorderFactory.createTitledBorder("Добавить элементы"));
        JButton btnAddText = new JButton("+ Текст");
        JButton btnAddRect = new JButton("+ Прямоугольник");
        JButton btnAddCircle = new JButton("+ Круг");
        btnAddText.addActionListener(e -> addElement("TEXT", "Новый текст", 120, 40));
        btnAddRect.addActionListener(e -> addElement("RECT", "", 100, 100));
        btnAddCircle.addActionListener(e -> addElement("CIRCLE", "", 100, 100));
        bottomBar.add(btnAddText);
        bottomBar.add(btnAddRect);
        bottomBar.add(btnAddCircle);

        add(topBar, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(canvas, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void createProject() {
        String name = JOptionPane.showInputDialog(this, "Введите название проекта:");
        if (name != null && !name.trim().isEmpty()) {
            if (projects.containsKey(name)) {
                JOptionPane.showMessageDialog(this, "Проект существует!");
                return;
            }
            Project p = new Project(name);
            projects.put(name, p);
            projectListModel.addElement(name);
            currentProject = p;
            renderCanvas();
            saveProjects();
        }
    }

    private void renameProject() {
        String selected = projectJList.getSelectedValue();
        if (selected == null) return;
        String newName = JOptionPane.showInputDialog(this, "Новое название:", selected);
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(selected)) {
            Project p = projects.remove(selected);
            p.setName(newName);
            projects.put(newName, p);
            projectListModel.set(projectListModel.indexOf(selected), newName);
            saveProjects();
        }
    }

    private void deleteProject() {
        String selected = projectJList.getSelectedValue();
        if (selected == null) return;
        projects.remove(selected);
        projectListModel.removeElement(selected);
        if (currentProject != null && currentProject.getName().equals(selected)) {
            currentProject = null;
            canvas.removeAll();
            canvas.repaint();
        }
        saveProjects();
    }

    private void openSelectedProject() {
        String selected = projectJList.getSelectedValue();
        if (selected != null) {
            currentProject = projects.get(selected);
            renderCanvas();
        }
    }

    private void clearCanvas() {
        if (currentProject != null) {
            currentProject.getElements().clear();
            renderCanvas();
            saveProjects();
        }
    }

    private void addElement(String type, String text, int w, int h) {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, "Сначала откройте проект!");
            return;
        }
        DesignElement el = new DesignElement(UUID.randomUUID().toString(), type, 50, 50, w, h, text, "#3B82F6", "#FFFFFF");
        currentProject.getElements().add(el);
        renderCanvas();
        saveProjects();
    }

    private void renderCanvas() {
        canvas.removeAll();
        if (currentProject == null) { canvas.repaint(); return; }
        for (DesignElement el : currentProject.getElements()) {
            JPanel elemWidget = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode(el.getBgColor()));
                    if ("CIRCLE".equals(el.getType())) {
                        g2.fillOval(0, 0, getWidth(), getHeight());
                    } else {
                        g2.fillRect(0, 0, getWidth(), getHeight());
                    }
                    if ("TEXT".equals(el.getType()) && el.getText() != null) {
                        g2.setColor(Color.decode(el.getTextColor()));
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(el.getText())) / 2;
                        int y = (getHeight() + fm.getAscent()) / 2 - 2;
                        g2.drawString(el.getText(), x, y);
                    }
                }
            };
            elemWidget.setBounds(el.getX(), el.getY(), el.getWidth(), el.getHeight());
            elemWidget.setOpaque(false);
            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) { dragOffset = e.getPoint(); selectedElement = el; }
                public void mouseDragged(MouseEvent e) {
                    if (dragOffset != null) {
                        int newX = elemWidget.getX() + e.getX() - dragOffset.x;
                        int newY = elemWidget.getY() + e.getY() - dragOffset.y;
                        elemWidget.setLocation(newX, newY);
                        el.setX(newX); el.setY(newY);
                    }
                }
                public void mouseReleased(MouseEvent e) { saveProjects(); }
            };
            elemWidget.addMouseListener(adapter);
            elemWidget.addMouseMotionListener(adapter);
            canvas.add(elemWidget);
        }
        canvas.revalidate();
        canvas.repaint();
    }

    private void saveProjects() {
        try {
            objectMapper.writeValue(storageFile, new ArrayList<>(projects.values()));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadProjects() {
        if (storageFile.exists()) {
            try {
                List<Project> list = objectMapper.readValue(storageFile, new TypeReference<List<Project>>() {});
                for (Project p : list) {
                    projects.put(p.getName(), p);
                    projectListModel.addElement(p.getName());
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void showPreview() {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, "Нечего предпросматривать!");
            return;
        }
        JFrame previewFrame = new JFrame("Предпросмотр: " + currentProject.getName());
        previewFrame.setSize(800, 600);
        JPanel pCanvas = new JPanel(null);
        pCanvas.setBackground(Color.WHITE);
        for (DesignElement el : currentProject.getElements()) {
            JPanel w = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.decode(el.getBgColor()));
                    if ("CIRCLE".equals(el.getType())) g2.fillOval(0, 0, getWidth(), getHeight());
                    else g2.fillRect(0, 0, getWidth(), getHeight());
                    if ("TEXT".equals(el.getType()) && el.getText() != null) {
                        g2.setColor(Color.decode(el.getTextColor()));
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(el.getText(), (getWidth()-fm.stringWidth(el.getText()))/2, (getHeight()+fm.getAscent())/2-2);
                    }
                }
            };
            w.setBounds(el.getX(), el.getY(), el.getWidth(), el.getHeight());
            w.setOpaque(false);
            pCanvas.add(w);
        }
        previewFrame.add(pCanvas);
        previewFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
