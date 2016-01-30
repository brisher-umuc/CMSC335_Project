/**
 * File: SorcerersCave
 * Date: 1/6/16
 * Author: ben risher
 * Purpose: user interface for the sorcerers cave game project
 */
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

//TODO: age, height, weight for creature??
//TODO: implement search through his array structure
//TODO: implement comparator / sort functions


public class SorcerersCave extends JPanel implements ActionListener {
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final char PARTY = 'p';
    private static final char CREATURE = 'c';
    private static final char ARTIFACT = 'a';
    private static final char TREASURE = 't';
    private static final String NEWLINE = "\n";

    private JFileChooser fileChooser;
    private JButton openButton, searchButton, displayButton, sortButton;
    private File gameFile;
    private JTextArea textArea;
    private static JTextField searchField;
    private JComboBox<String> searchSelectorBox, comparatorBox, typeSelectorBox;
    private static Boolean sentinel = false;

    private Cave cave = new Cave();
    private HashMap<Integer, CaveElement> indexLookup = new HashMap<Integer, CaveElement>();
    private HashMap<String, ArrayList<CaveElement>> typeLookup = new HashMap<String, ArrayList<CaveElement>>();
    private HashMap<String, CaveElement> nameLookup = new HashMap<String, CaveElement>();

    public SorcerersCave() {  // constructor
        setLayout(new GridBagLayout());

        GridBagConstraints c = getBaseGBConstraints(); // textarea
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        this.textArea = new JTextArea(20, 80);
        this.textArea.setFont(DEFAULT_FONT);
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        add(scrollPane, c);

        JPanel searchPanel = new JPanel();  // search panel, houses searchField and searchSelectorBox
        GridBagLayout searchGBC = new GridBagLayout();
        searchPanel.setLayout(searchGBC);
        searchPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Search"));

        c = getBaseGBConstraints();  // searchSelectorBox
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        this.searchSelectorBox = new JComboBox<String>();
        this.searchSelectorBox.addItem("Index");
        this.searchSelectorBox.addItem("Type");
        this.searchSelectorBox.addItem("Name");
        this.searchSelectorBox.setFont(DEFAULT_FONT);
        searchPanel.add(this.searchSelectorBox, c);

        c = getBaseGBConstraints();  // textfield
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridx = 1;
        c.insets = new Insets(0, 0, 5, 5);
        searchField = new JTextField("Search Target");
        searchField.setFont(DEFAULT_FONT);
        searchField.setForeground(Color.lightGray);
        searchField.addActionListener(this);
        searchField.getDocument().addDocumentListener(new DocumentListener() {  // tracks user actually using the textfield
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                searchField.setForeground(Color.black);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                searchField.setForeground(Color.black);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                sentinel = true;
                searchField.setForeground(Color.black);
            }
        });
        searchPanel.add(searchField, c);

        c = getBaseGBConstraints();
        c.gridwidth = 3;
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        this.add(searchPanel, c);  // add searchPanel to frame


        JPanel sortPanel = new JPanel();  // sort panel, houses sortButton, comparatorBox, typeSelectorBox
        GridBagLayout sortGBC = new GridBagLayout();
        sortPanel.setLayout(sortGBC);
        sortPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Sort"));

        c = getBaseGBConstraints();  // typeSelectorBox
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 5, 5, 5);
        this.typeSelectorBox = new JComboBox<String>();
        this.typeSelectorBox.addItem("Treasures");
        this.typeSelectorBox.addItem("Creatures");
        this.typeSelectorBox.addActionListener(this);
        this.typeSelectorBox.setFont(DEFAULT_FONT);
        sortPanel.add(this.typeSelectorBox, c);

        c = getBaseGBConstraints();  // comparator combo box
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 5, 5);
        this.comparatorBox = new JComboBox<String>();
        this.comparatorBox.addItem("Weight");
        this.comparatorBox.addItem("Value");
        this.comparatorBox.setFont(DEFAULT_FONT);
        //add(this.comparatorBox, c);
        sortPanel.add(this.comparatorBox, c);

        c = getBaseGBConstraints();  // sort button
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 2;
        c.insets = new Insets(0, 0, 5, 5);
        this.sortButton = new JButton("Sort");
        this.sortButton.addActionListener(this);
        this.sortButton.setFont(DEFAULT_FONT);
        sortPanel.add(this.sortButton, c);

        c = getBaseGBConstraints();  // sortPanel
        c.gridwidth = 3;
        c.gridy = 2;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        this.add(sortPanel, c);  // add panel to frame

        JPanel controlPanel = new JPanel();  // control panel, houses openButton, displayButton, searchButton
        GridBagLayout controlGBC = new GridBagLayout();
        controlPanel.setLayout(controlGBC);
        controlPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Control"));

        c = getBaseGBConstraints();  // open button
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        this.openButton = new JButton("Open File...");
        this.openButton.addActionListener(this);
        this.openButton.setFont(DEFAULT_FONT);
        controlPanel.add(this.openButton, c);

        c = getBaseGBConstraints();  // display button
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 1;
        c.insets = new Insets(0, 0, 5, 5);
        this.displayButton = new JButton("Display");
        this.displayButton.addActionListener(this);
        this.displayButton.setFont(DEFAULT_FONT);
        controlPanel.add(this.displayButton, c);

        c = getBaseGBConstraints();  // search button
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.ipady = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 2;
        c.insets = new Insets(0, 0, 5, 5);
        this.searchButton = new JButton("Search...");
        this.searchButton.addActionListener(this);
        this.searchButton.setFont(DEFAULT_FONT);
        controlPanel.add(this.searchButton, c);

        c = getBaseGBConstraints();  // controlPanel
        c.gridwidth = 3;
        c.gridy = 3;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        this.add(controlPanel, c);  // add panel to frame

        this.fileChooser = new JFileChooser();
        this.fileChooser.setCurrentDirectory(new File("."));
    }  // end constructor

    private GridBagConstraints getBaseGBConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        return c;
    }

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

                if (this.cave.getParties().size() > 0 || this.cave.getElements().size() > 0) {
                    // if the cave has been populated before, use a new cave
                    // essentially, every opening of a file constitutes a new game
                    this.cave = new Cave();
                    this.indexLookup = new HashMap<Integer, CaveElement>();
                    this.typeLookup = new HashMap<String, ArrayList<CaveElement>>();
                    this.nameLookup = new HashMap<String, CaveElement>();
                }
                readInFile();
            }
        } // end open button handler
        else if (source == this.searchButton) {
            textArea.setText(null);

            if (!sentinel) {  // user never entered search criteria
                this.textArea.append("You can't search for anything with the default value." + NEWLINE);
            }
            else {
                search(this.searchSelectorBox.getSelectedItem().toString(), searchField.getText());
            }
        } // end search button handler
        else if (source == this.displayButton) {
            textArea.setText(null);
            this.textArea.append(this.cave.toString().replaceAll("\n\n", "\n"));
        } // end display button handler
        else if (source == searchField && sentinel) {  // can just hit enter after typing in searchField
            search(this.searchSelectorBox.getSelectedItem().toString(), searchField.getText());
        } // end enter pressed in text field
        else if (source == this.typeSelectorBox) {
            this.comparatorBox.removeAllItems();

            if (this.typeSelectorBox.getSelectedItem().equals("Creatures")) {
                this.comparatorBox.addItem("Name");
                this.comparatorBox.addItem("Age");
                this.comparatorBox.addItem("Height");
                this.comparatorBox.addItem("Weight");
                this.comparatorBox.addItem("Empathy");
                this.comparatorBox.addItem("Fear");
                this.comparatorBox.addItem("Carrying Capacity");
            }
            else if (this.typeSelectorBox.getSelectedItem().equals("Treasures")) {
                this.comparatorBox.addItem("Weight");
                this.comparatorBox.addItem("Value");
            }
        }  // end type selector handler
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

                            this.cave.getParties().add(party);
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

                            if (this.typeLookup.containsKey(creature.getType())) {
                                this.typeLookup.get(creature.getType()).add(creature);
                            }
                            else {
                                ArrayList<CaveElement> elementArrayList = new ArrayList<CaveElement>();
                                elementArrayList.add(creature);
                                this.typeLookup.put(creature.getType(), elementArrayList);
                            }

                            if (creature.getParty() == 0) {
                                this.cave.getElements().add(creature);
                            }
                            else {
                                Party p = (Party) indexLookup.get(creature.getParty());
                                p.getCreatures().add(creature);
                            }

                            break;
                        case ARTIFACT:  //a:<index>:<type>:<creature>[:<name>]
                            Artifact artifact = parts.length >= 5 ? new Artifact(parts[4]) : new Artifact(parts[2]);

                            artifact.setIndex(Integer.valueOf(parts[1]));
                            artifact.setType(parts[2]);
                            artifact.setCreature(Integer.valueOf(parts[3]));

                            this.indexLookup.put(artifact.getIndex(), artifact);
                            this.nameLookup.put(artifact.getName(), artifact);

                            // multiples of the same type
                            if (this.typeLookup.containsKey(artifact.getType())) {
                                this.typeLookup.get(artifact.getType()).add(artifact);
                            }
                            else {
                                ArrayList<CaveElement> elementArrayList = new ArrayList<CaveElement>();
                                elementArrayList.add(artifact);
                                this.typeLookup.put(artifact.getType(), elementArrayList);
                            }

                            // orphan artifacts
                            if (artifact.getCreature() == 0) {
                                this.cave.getElements().add(artifact);
                            }
                            else {
                                Creature c = (Creature) this.indexLookup.get(artifact.getCreature());
                                c.getArtifacts().add(artifact);
                            }

                            break;
                        case TREASURE:  //t:<index>:<type>:<creature>:<weight>:<value>
                            Treasure treasure = new Treasure(parts[2]);

                            treasure.setIndex(Integer.valueOf(parts[1]));
                            treasure.setCreature(Integer.valueOf(parts[3]));
                            treasure.setWeight(Double.valueOf(parts[4]));
                            treasure.setValue(Integer.valueOf(parts[5]));

                            this.indexLookup.put(treasure.getIndex(), treasure);
                            this.nameLookup.put(treasure.getName(), treasure);

                            if (this.typeLookup.containsKey(treasure.getType())) {
                                this.typeLookup.get(treasure.getType()).add(treasure);
                            }
                            else {
                                ArrayList<CaveElement> elementArrayList = new ArrayList<CaveElement>();
                                elementArrayList.add(treasure);
                                this.typeLookup.put(treasure.getType(), elementArrayList);
                            }

                            if (treasure.getCreature() == 0) {
                                this.cave.getElements().add(treasure);
                            }
                            else {
                                Creature cr = (Creature) this.indexLookup.get(treasure.getCreature());
                                cr.getTreasures().add(treasure);
                            }

                            break;
                        default:
                            System.out.println("Not Processed: " + line);
                    }
                }  // end line filter
            }  // end file reader loop
            bufferedReader.close();
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
            this.searchDisplayHelper(this.indexLookup.get(index));
        }
        else if (type.equals("Name") && this.nameLookup.containsKey(value)) {
            this.searchDisplayHelper(this.nameLookup.get(value));
        }
        else if (type.equals("Type") && this.typeLookup.containsKey(value)) {
            this.searchDisplayHelper(this.typeLookup.get(value));
        }
        else {
            this.textArea.append("Search returned no result\n");
        }
    } // end search

    /**
     * Format search output that is more helpful than just a name
     */
    private void searchDisplayHelper(CaveElement element) {
        this.textArea.append("Index: " + element.getIndex() + NEWLINE);
        this.textArea.append("Name: " + element.getName() + NEWLINE);

        if (element instanceof Party) {
            Party party = (Party) element;
            this.textArea.append("Creatures: \n");
            for (Creature creature: party.getCreatures()) {
                this.textArea.append("-> " + creature.getName() + NEWLINE);
            }
        }
        else if (element instanceof Creature) {
            Creature creature = (Creature) element;
            if (creature.getParty() != 0) {
                Party p = (Party) this.indexLookup.get(creature.getParty());
                this.textArea.append("Party: " + p.getName() + NEWLINE);
            }
            this.textArea.append("Type: " + creature.getType() + NEWLINE);
            this.textArea.append("Artifacts: \n" + creature.getArtifacts().toString() + NEWLINE);
            this.textArea.append("Treasures: \n" + creature.getTreasures().toString() + NEWLINE);
            this.textArea.append("Traits: \n");
            this.textArea.append("-> Fear: " + creature.getFear() + NEWLINE);
            this.textArea.append("-> Empathy: " + creature.getEmpathy() + NEWLINE);
            this.textArea.append("-> Capacity: " + creature.getCapacity() + NEWLINE);
        }
        else if (element instanceof Artifact) {
            Artifact artifact = (Artifact) element;
            this.textArea.append("Owner: " + artifact.getCreature() + NEWLINE);
            this.textArea.append("Type: " + artifact.getType() + NEWLINE);
        }
        else if (element instanceof Treasure) {
            Treasure treasure = (Treasure) element;
            this.textArea.append("Owner: " + treasure.getCreature() + NEWLINE);
            this.textArea.append("Type: " + treasure.getType() + NEWLINE);
            this.textArea.append("Value: " + treasure.getValue() + NEWLINE);
            this.textArea.append("Weight: " + treasure.getWeight() + NEWLINE);
        }
    } // end display helper for single instance items

    /**
     * Format search output that is more helpful than just a name
     */
    private void searchDisplayHelper(ArrayList<CaveElement> elementArrayList) {
        // i kept this display relatively simple, since it's returning an unknown number of values
        // i think the user could use this as a point to start drilling down
        // i.e. search for type=woman, then use a more specific search for more information
        for (CaveElement element: elementArrayList) {
            this.textArea.append("Index: " + element.getIndex() + NEWLINE);
            this.textArea.append("Name: " + element.getName() + NEWLINE + NEWLINE);
        }
    } // end display helper for multiple elements

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorcerer's Cave");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SorcerersCave());
        frame.pack();
        frame.setVisible(true);
    } // end main
}