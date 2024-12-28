package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

class Contact {
    String name;
    String phone;
    String imagePath;

    public Contact(String name, String phone, String imagePath) {
        this.name = name;
        this.phone = phone;
        this.imagePath = imagePath;
    }
}

public class ContactManager extends JFrame {
    private final ArrayList<Contact> contacts = new ArrayList<>();
    private final JTextField nameField;
    private final JTextField phoneField;
    private final JLabel imageLabel;
    private File selectedImageFile; // To store the selected image file
    private final DefaultTableModel tableModel;
    private final JTable contactTable;

    // Maximum dimensions for the image
    private static final int MAX_IMAGE_WIDTH = 100;
    private static final int MAX_IMAGE_HEIGHT = 100;

    public ContactManager() {
        setTitle("Contact Manager");
        setSize(800, 600); // Adjusted size for better layout
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Create text fields with larger font
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        Font font = new Font("Arial", Font.PLAIN, 16);
        nameField.setFont(font);
        phoneField.setFont(font);

        imageLabel = new JLabel("No Image Selected");

        // Set up the table model and JTable
        String[] columnNames = {"Name", "Phone", "Image"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return ImageIcon.class; // Set the "Image" column to use ImageIcon
                }
                return String.class;
            }
        };
        contactTable = new JTable(tableModel);
        contactTable.setRowHeight(100);
        contactTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));

        JButton addButton = new JButton("Add Contact");
        JButton chooseImageButton = new JButton("Choose Image");
        JButton deleteButton = new JButton("Delete Contact");
        JButton updateButton = new JButton("Update Contact");

        addButton.addActionListener(e -> addContact());
        chooseImageButton.addActionListener(e -> uploadImage());
        deleteButton.addActionListener(e -> deleteContact());
        updateButton.addActionListener(e -> {
            int selectedRow = contactTable.getSelectedRow();
            updateContact(selectedRow);
        });

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(chooseImageButton);
        add(imageLabel);
        add(addButton);
        add(deleteButton);
        add(updateButton);
        add(scrollPane);
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            imageLabel.setText(selectedImageFile.getName());
            JOptionPane.showMessageDialog(this, "Image selected: " + selectedImageFile.getName());
        } else {
            JOptionPane.showMessageDialog(this, "No image selected.");
        }
    }

    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty");
            return;
        }

        if (phone.length() < 10 || !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid phone number. It must be at least 10 digits and contain only numbers.");
            return;
        }

        String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;
        contacts.add(new Contact(name, phone, imagePath));

        ImageIcon imageIcon = null;
        if (selectedImageFile != null) {
            imageIcon = new ImageIcon(new ImageIcon(selectedImageFile.getAbsolutePath())
                    .getImage().getScaledInstance(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, Image.SCALE_SMOOTH));
        }

        tableModel.addRow(new Object[]{name, phone, imageIcon});
        JOptionPane.showMessageDialog(this, "Contact added!");

        nameField.setText("");
        phoneField.setText("");
        imageLabel.setText("No Image Selected");
        selectedImageFile = null;
    }

    private void updateContact(int selectedRow) {
        if (selectedRow != -1) {
            Contact contactToUpdate = contacts.get(selectedRow);

            String newName = JOptionPane.showInputDialog(this, "Enter new name:", contactToUpdate.name);
            if (newName == null || newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty");
                return;
            }

            String newPhone = JOptionPane.showInputDialog(this, "Enter new phone number:", contactToUpdate.phone);
            if (newPhone == null || newPhone.isEmpty() || !newPhone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid phone number.");
                return;
            }

            contactToUpdate.name = newName;
            contactToUpdate.phone = newPhone;

            tableModel.setValueAt(newName, selectedRow, 0);
            tableModel.setValueAt(newPhone, selectedRow, 1);

            JOptionPane.showMessageDialog(this, "Contact updated!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to update.");
        }
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();

        if (selectedRow != -1) {
            contacts.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Contact deleted!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
        }
    }

    // Custom Pink Theme
    public static void main(String[] args) {
        // Set FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
        }

        // Set custom colors for the pink theme
        UIManager.put("Panel.background", new Color(255, 228, 225)); // Pastel Pink
        UIManager.put("Button.background", new Color(255, 255, 204)); // Pastel Yellow
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Table.selectionBackground", new Color(173, 216, 230)); // Pastel Blue
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("TextField.background", Color.white); // light gray
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Table.background", new Color(255, 255, 255)); // White for table background
        UIManager.put("Table.gridColor", new Color(200, 200, 200)); // Light gray grid color


        SwingUtilities.invokeLater(() -> {
            ContactManager manager = new ContactManager();
            manager.setVisible(true);
        });
    }
}