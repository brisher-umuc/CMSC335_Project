/**
 * File: SorcerersCave
 * Date: 1/6/16
 * Author: ben risher
 * Purpose: user interface for the sorcerers cave game project
 */
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class SorcerersCave extends JPanel implements ActionListener {
    private static final Font DEFAULT_FONT = new Font("Comic Sans", Font.BOLD, 16);
    private static final char PARTY = 'p';
    private static final char CREATURE = 'c';
    private static final char ARTIFACT = 'a';
    private static final char TREASURE = 't';

    private JFileChooser fileChooser;
    private JButton openButton, searchButton, displayButton;
    private File gameFile;
    private JTextArea textArea;
    private static JTextField textField;
    private JComboBox<String> comboBox;
    private static Boolean sentinel = false;
    private Cave cave = new Cave();
    private HashMap<Integer, Object> indexLookup = new HashMap<Integer, Object>();
    private HashMap<String, Object> typeLookup = new HashMap<String, Object>();
    private HashMap<String, Object> nameLookup = new HashMap<String, Object>();

    public SorcerersCave() {
        setLayout(new GridBagLayout());

        // settings for most components
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        this.textArea = new JTextArea(20, 80);
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        this.textArea.setFont(DEFAULT_FONT);
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        add(scrollPane, c);

        c.weightx = 0.05;
        c.weighty = 0.05;

        textField = new JTextField("Search Target");
        textField.setFont(DEFAULT_FONT);
        textField.setForeground(Color.lightGray);
        textField.addActionListener(this);
        textField.getDocument().addDocumentListener(new DocumentListener() {  // tracks user actually using the textfield
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                textField.setForeground(Color.black);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                textField.setForeground(Color.black);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                textField.setForeground(Color.black);
            }
        });
        c.ipady = 20;
        c.gridy = 1;
        c.gridwidth = 2;
        add(textField, c);

        c.ipady = 0;  // reset ipady to default

        this.comboBox = new JComboBox<String>();
        this.comboBox.addItem("Index");
        this.comboBox.addItem("Type");
        this.comboBox.addItem("Name");
        this.comboBox.setFont(DEFAULT_FONT);
        c.gridx = 2;
        add(this.comboBox, c);

        this.openButton = new JButton("Open File...");
        this.openButton.addActionListener(this);
        this.openButton.setFont(DEFAULT_FONT);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        add(this.openButton, c);

        this.displayButton = new JButton("Display");
        this.displayButton.addActionListener(this);
        this.displayButton.setFont(DEFAULT_FONT);
        c.gridx = 1;
        add(this.displayButton, c);

        this.searchButton = new JButton("Search...");
        this.searchButton.addActionListener(this);
        this.searchButton.setFont(DEFAULT_FONT);
        c.gridx = 2;
        add(this.searchButton, c);


        this.fileChooser = new JFileChooser();
        this.fileChooser.setCurrentDirectory(new File("."));
    }  // end constructor

    /**
     * event handler
     **/
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == this.openButton) {
            if (this.fileChooser.showOpenDialog(this.openButton) == JFileChooser.APPROVE_OPTION) {
                this.gameFile = this.fileChooser.getSelectedFile();
                textArea.setText(null);

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);  // i wanted to see which file was currently open
                frame.setTitle("Sorcerer's Cave [" + this.gameFile.toString() + "]");

                readInFile();
            }
        } // end open button handler
        else if (source == this.searchButton) {
            textArea.setText(null);

            if (!sentinel) {  // user never entered search criteria
                this.textArea.append("You can't search for anything with the default value.\n");
            }
            else {
                search(this.comboBox.getSelectedItem().toString(), textField.getText());
            }
        } // end search button handler
        else if (source == this.displayButton) {
            textArea.setText(null);
            this.textArea.append(this.cave.toString());
        } // end display button handler
        else if (source == textField && sentinel) {
            search(this.comboBox.getSelectedItem().toString(), textField.getText());
        } // end enter pressed in text field
    } // end actionPerformed

    /**
     * Read in the file that the user chooses with game data.
     **/
    private void readInFile() {
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.gameFile));

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.startsWith("/")) {  // ignore empty lines, and comments
                    String[] parts = line.split(":");

                    for (int i = 1; i < parts.length; i++) {
                        parts[i] = parts[i].trim();  // ignore surrounding whitespace, dont care about parts[0]
                    }

                    char typeDefinition = line.charAt(0);  // find type of the line

                    switch (typeDefinition) {
                        case PARTY:  //p:<index>:<name>
                            Party party = new Party(parts[2]);

                            party.setIndex(Integer.valueOf(parts[1]));

                            this.cave.parties.add(party);
                            this.indexLookup.put(party.getIndex(), party);
                            this.nameLookup.put(party.getName(), party);

                            break;
                        case CREATURE:  //c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
                            Creature creature = new Creature(parts[3]);

                            creature.setIndex(Integer.valueOf(parts[1]));
                            creature.setType(parts[2]);
                            creature.setParty(Integer.valueOf(parts[4]));
                            creature.setEmpathy(Integer.valueOf(parts[5]));
                            creature.setFear(Integer.valueOf(parts[6]));
                            creature.setCapacity(Integer.valueOf(parts[7]));

                            this.indexLookup.put(creature.getIndex(), creature);
                            this.nameLookup.put(creature.getName(), creature);
                            this.typeLookup.put(creature.getType(), creature);

                            Party p = (Party) indexLookup.get(creature.getParty());
                            System.out.println("Creature: " + creature.getName());
                            p.creatures.add(creature);

                            break;
                        case ARTIFACT:  //a:<index>:<type>:<creature>[:<name>]
                            Artifact artifact = parts.length >= 5 ? new Artifact(parts[4]) : new Artifact(parts[2]);

                            artifact.setIndex(Integer.valueOf(parts[1]));
                            artifact.setType(parts[2]);
                            artifact.setCreature(Integer.valueOf(parts[3]));

                            this.indexLookup.put(artifact.getIndex(), artifact);
                            this.nameLookup.put(artifact.getName(), artifact);
                            this.typeLookup.put(artifact.getType(), artifact);

                            if (artifact.getCreature() == 0) {
                                this.cave.elements.add(artifact);
                            }
                            else {
                                Creature c = (Creature) this.indexLookup.get(artifact.getCreature());
                                c.artifacts.add(artifact);
                            }

                            break;
                        case TREASURE:  //t:<index>:<type>:<creature>:<weight>:<value>
                            Treasure treasure = new Treasure(parts[2]);

                            treasure.setIndex(Integer.valueOf(parts[1]));
                            treasure.setType(parts[2]);
                            treasure.setCreature(Integer.valueOf(parts[3]));
                            treasure.setWeight(Integer.valueOf(parts[4]));
                            treasure.setValue(Integer.valueOf(parts[5]));

                            this.indexLookup.put(treasure.getIndex(), treasure);
                            this.nameLookup.put(treasure.getName(), treasure);
                            this.typeLookup.put(treasure.getType(), treasure);

                            if (treasure.getCreature() == 0) {
                                this.cave.elements.add(treasure);
                            }
                            else {
                                Creature cr = (Creature) this.indexLookup.get(treasure.getCreature());
                                cr.treasures.add(treasure);
                            }

                            break;
                        default:
                            System.out.println("Not Processed: " + line);
                    }
                }  // end line filter
            }  // end file reader loop
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }  // end readInFile

    /**
     * search by name, index, or type
     */
    private void search(String type, String value) {
        this.textArea.setText(null);

        int index = -1;
        if (type.equals("Index")) {
            try {
                index = Integer.valueOf(value.trim());
            }
            catch (NumberFormatException e) {
                this.textArea.append("You didn't specify an actual index.\n");
                return;
            }
        }

        if (type.equals("Index") && this.indexLookup.containsKey(index)) {
            this.textArea.append(this.indexLookup.get(index).toString());
        }
        else if (type.equals("Name") && this.nameLookup.containsKey(value)) {
            this.textArea.append(this.nameLookup.get(value).toString());
        }
        else if (type.equals("Type") && this.typeLookup.containsKey(value)) {
            this.textArea.append(this.typeLookup.get(value).toString());
        }
        else {
            this.textArea.append("Search returned no result\n");
        }
    } // end search

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorcerer's Cave");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SorcerersCave());
        frame.pack();
        frame.setVisible(true);
    } // end main
}