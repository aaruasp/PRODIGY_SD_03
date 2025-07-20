package com.nt.test;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

@SuppressWarnings("serial")
public class ContactManagerGUI extends JFrame
{
    private JTextField nameField, phoneField, emailField;
    private JButton addButton, saveButton, editButton, deleteButton, viewButton;
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private static final String FILE_NAME = "contacts.txt";

    public ContactManagerGUI() 
    {
        setTitle("Contact Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table Model
        tableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Email"}, 0);
        contactTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(contactTable);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Contact Info"));

        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));

        addButton = new JButton("Add");
        saveButton = new JButton("Save");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        viewButton = new JButton("View Contacts");

        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(tableScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load from file
        loadContactsFromFile();

        // Event Handlers
        addButton.addActionListener(e -> addContact());
        saveButton.addActionListener(e -> clearFields());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());
        viewButton.addActionListener(e -> viewContacts());

        contactTable.getSelectionModel().addListSelectionListener(e -> loadSelectedContact());
    }

    private void addContact() 
    {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        tableModel.addRow(new Object[]{name, phone, email});
        saveContactsToFile();
        clearFields();
    }

    private void editContact()
    {
        int selectedRow = contactTable.getSelectedRow();

        if (selectedRow < 0) 
        {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "All fields are required to edit.");
            return;
        }

        tableModel.setValueAt(name, selectedRow, 0);
        tableModel.setValueAt(phone, selectedRow, 1);
        tableModel.setValueAt(email, selectedRow, 2);

        JOptionPane.showMessageDialog(this, "Contact updated.");
        saveContactsToFile();
        clearFields();
        contactTable.clearSelection();
    }

    private void deleteContact() 
    {
        int selectedRow = contactTable.getSelectedRow();

        if (selectedRow >= 0)
        {
            tableModel.removeRow(selectedRow);
            saveContactsToFile();
            clearFields();
        } 
        else
        {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
        }
    }

    private void viewContacts()
    {
        if (tableModel.getRowCount() == 0) 
        {
            JOptionPane.showMessageDialog(this, "No contacts to display.");
            return;
        }

        StringBuilder contacts = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) 
        {
            contacts.append("Name: ").append(tableModel.getValueAt(i, 0)).append(", ")
                    .append("Phone: ").append(tableModel.getValueAt(i, 1)).append(", ")
                    .append("Email: ").append(tableModel.getValueAt(i, 2)).append("\n");
        }

        JTextArea contactArea = new JTextArea(contacts.toString());
        contactArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(contactArea), "Contact List", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSelectedContact() 
    {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) 
        {
            nameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        }
    }

    private void clearFields() 
    {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    private void saveContactsToFile()
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) 
        {
            for (int i = 0; i < tableModel.getRowCount(); i++)
            {
                writer.write(tableModel.getValueAt(i, 0) + "," +
                        tableModel.getValueAt(i, 1) + "," +
                        tableModel.getValueAt(i, 2));
                writer.newLine();
            }
        }
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Error saving contacts: " + e.getMessage());
        }
    }

    private void loadContactsFromFile()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME)))
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(",");
                if (parts.length == 3)
                {
                    tableModel.addRow(parts);
                }
            }
        } 
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, "Error loading contacts: " + e.getMessage());
        }
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new ContactManagerGUI().setVisible(true));
    }
}
