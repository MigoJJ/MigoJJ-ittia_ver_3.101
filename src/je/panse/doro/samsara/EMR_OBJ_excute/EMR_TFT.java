package je.panse.doro.samsara.EMR_OBJ_excute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import je.panse.doro.GDSEMR_frame;
import je.panse.doro.chartplate.filecontrol.align.String_ConvertToArray;

public class EMR_TFT extends JFrame {
    private JTextField[] textFields;
    private JTextArea inputTextArea;
    private String[] labels = {
            "T3 (pg/mL):",
            "Free T4 (ng/dL):",
            "TSH (mIU/L):",
            "Anti-TSH-Receptor Antibodies (IU/L <1.75):",
            "Anti-Thyroglobulin Antibodies (IU/mL <115):",
            "Anti-Microsomal Antibodies (IU/mL <34):"
        };
    
    public EMR_TFT() {
        // Set JFrame properties
        setTitle("GDS TFT Input Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 360);
//        setLocationRelativeTo(null);
        setLocation(1750, 310);

        // Create panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textFields = new JTextField[labels.length];

        // Create input panel to hold labels and text fields
        JPanel inputPanel = new JPanel(new GridLayout(labels.length, 2, 10, 10));

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setHorizontalAlignment(SwingConstants.RIGHT);

            JTextField textField = new JTextField();
            textField.setName(labels[i]); // Set the name of the text field

            textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 30));
            textField.setHorizontalAlignment(SwingConstants.CENTER);

            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        int currentIndex = getCurrentIndex(textField);
                        if (currentIndex < textFields.length - 1) {
                            textFields[currentIndex + 1].requestFocus();
                        }
                        if (currentIndex == 3 && textFields[3].getText().isEmpty()) {
                            // do something if input is empty for the 4th field
                        	saveData();
                        } else if (currentIndex == 5 && textFields[5].getText().isEmpty()) {
                        	saveData();
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            inputPanel.add(label);
            inputPanel.add(textField);

            textFields[i] = textField;
        }

        // Add input panel to the main panel
        panel.add(inputPanel, BorderLayout.NORTH);

        // Create input text area
        inputTextArea = new JTextArea();
        inputTextArea.setBackground(Color.YELLOW); // Set your desired color here
        inputTextArea.setEditable(true);
        inputTextArea.setPreferredSize(new Dimension(150, 100));

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create buttons
        JButton clearButton = new JButton("Clear");
        JButton saveButton = new JButton("Save");
        JButton quitButton = new JButton("Quit");

        // Add button actions
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);

        // Add input panel and input text area to the main panel
        panel.add(inputTextArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panel to the frame
        add(panel);

        // Display the frame
        setVisible(true);
    }

    private int getCurrentIndex(JTextField textField) {
        for (int i = 0; i < textFields.length; i++) {
            if (textFields[i] == textField) {
                return i;
            }
        }
        return -1;
    }

    private void clearFields() {
        for (JTextField textField : textFields) {
            textField.setText("");
        }
    }

    private void saveData() {
        double[] ranges = {
            0.9, 2.5,   // T3 (ug/dL) range
            10.6, 19.4,  // Free T4 (ug/dL) range
            0.25, 5.0,  // TSH (mIU/L) range
            Double.NEGATIVE_INFINITY, 1.75,  // Anti-TSH-Receptor Antibodies (IU/L) range
            Double.NEGATIVE_INFINITY, 115,   // Anti-Thyroglobulin Antibodies (IU/mL) range
            Double.NEGATIVE_INFINITY, 34    // Anti-Microsomal Antibodies (IU/mL) range
        };

        StringBuilder outputText = new StringBuilder();
        outputText.append("\n");
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            double rangeMin = ranges[i * 2];
            double rangeMax = ranges[i * 2 + 1];

            String value = textFields[i].getText();

            if (!value.isEmpty()) {
                double numericValue = Double.parseDouble(value);

                if (numericValue < rangeMin) {
                    outputText.append("▼\t").append(value).append("\t").append(label).append("\n");
                } else if (numericValue > rangeMax) {
                    outputText.append("▲\t").append(value).append("\t").append(label).append("\n");
                } else {
                    outputText.append(" \t").append(value).append("\t").append(label).append("\n");
                }
            }
        }
        inputTextArea.setText(outputText.toString()); // Update this line with the appropriate UI component
        String_ConvertToArray.main(outputText.toString());
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EMR_TFT();
            }
        });
    }
}
