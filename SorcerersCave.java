/**
 * File: SorcerersCave
 * Date: 1/6/16
 * Author: ben risher
 * Purpose: user interface for the sorcerers cave game project
 */
import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SorcerersCave extends JPanel {
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final String PARTY = "p";
    private static final String CREATURE = "c";
    private static final String ARTIFACT = "a";
    private static final String TREASURE = "t";
    private static final String NEWLINE = "\n";
    private static final String BREAK = "<br>";

    private final JFileChooser fileChooser;
    private final JButton openButton;
    private final JButton searchButton;
    private final JButton displayButton;
    private final JButton sortButton;
    private File gameFile;
    private final JTree treeArea;
    private final ToolTipTreeNode caveNode;
    private final ToolTipTreeNode orphanNode;
    private static JTextField searchField;
    private final JComboBox<String> searchSelectorBox;
    private final JComboBox<String> comparatorBox;
    private final JComboBox<String> typeSelectorBox;
    private static Boolean sentinel = false;

    private final HashMap<String, ArrayList<String>> attributesList = new HashMap<>();

    private Cave cave = new Cave();
    private ArrayList<CaveElement> typeList;

    /**
     * Constructor for Sorcerer's Cave
     */
    public SorcerersCave() {  // constructor
        setLayout(new BorderLayout());

        // for the sake of my sanity, defining all the panels here
        JPanel treePanel = new JPanel(new BorderLayout());
        JPanel jobPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JPanel sortPanel = new JPanel(new BorderLayout(5, 0));
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new GridLayout(3, 0));
        topPanel.setBorder(new EmptyBorder(5,5,5,5));
        bottomPanel.setBorder(new EmptyBorder(5,5,5,5));

        // attributes list to populate the sort comparator dropdown
        attributesList.put("Creatures", new ArrayList<String>() {
            {add("Name"); add("Age"); add("Height"); add("Weight");
            add("Empathy"); add("Fear"); add("Carrying Capacity");}} );
        attributesList.put("Treasures", new ArrayList<String>() {{add("Weight"); add("Value");}});

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        // JTree display area
        ToolTipTreeNode pseudoRootNode = new ToolTipTreeNode("I'm hidden");  // workaround for having multi-rooted jtree
        caveNode = new ToolTipTreeNode("The Cave");
        orphanNode = new ToolTipTreeNode("The Orphans");
        treeArea = new JTree(pseudoRootNode) {
            public String getToolTipText(MouseEvent evt) {
                if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
                    return null;
                }
                TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                return ((ToolTipTreeNode) curPath.getLastPathComponent()).getToolTipText();
            }
        };
        treeArea.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Cave Display"));
        treeArea.setToolTipText("");
        pseudoRootNode.add(caveNode);
        pseudoRootNode.add(orphanNode);
        treeArea.expandRow(0);
        treeArea.setRootVisible(false);
        treeArea.setShowsRootHandles(true);
        ToolTipManager.sharedInstance().registerComponent(treeArea);
        treeArea.setFont(DEFAULT_FONT);
        JScrollPane scrollPane = new JScrollPane(treeArea);
        treePanel.setPreferredSize(new Dimension(325, 400));

        // job panel, houses ... whatever im going to use to display jobs
        jobPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Current Jobs"));
        jobPanel.setPreferredSize(new Dimension(175, 400));

        // search panel, houses searchField and searchSelectorBox
        searchPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Search"));

        searchSelectorBox = new JComboBox<>();
        searchSelectorBox.addItem("Index");
        searchSelectorBox.addItem("Type");
        searchSelectorBox.addItem("Name");
        searchSelectorBox.setFont(DEFAULT_FONT);

        searchField = new JTextField("Search Target");
        searchField.setFont(DEFAULT_FONT);
        searchField.setForeground(Color.lightGray);
        searchField.addActionListener(ae -> {
            if (sentinel) {
                search(searchSelectorBox.getSelectedItem().toString(), searchField.getText());
            }
        });
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

        // sort panel, houses sortButton, comparatorBox, typeSelectorBox
        sortPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Sort"));

        comparatorBox = new JComboBox<>();
        comparatorBox.addItem("Weight");
        comparatorBox.addItem("Value");
        comparatorBox.setFont(DEFAULT_FONT);

        typeSelectorBox = new JComboBox<>();
        typeSelectorBox.addItem("Treasures");
        typeSelectorBox.addItem("Creatures");
        typeSelectorBox.addActionListener(ae -> {
            comparatorBox.removeAllItems();
            attributesList.get(typeSelectorBox.getSelectedItem()).forEach((st)->comparatorBox.addItem(st));
        });
        typeSelectorBox.setFont(DEFAULT_FONT);

        sortButton = new JButton("Sort");
        sortButton.addActionListener(ae -> {
            sort(typeSelectorBox.getSelectedItem().toString(), comparatorBox.getSelectedItem().toString(), cave);
            //TODO: need ot alter the output of the jtree
        });
        sortButton.setFont(DEFAULT_FONT);

        // control panel, houses openButton, displayButton, searchButton
        controlPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Control"));

        openButton = new JButton("Open File");
        openButton.addActionListener(ae -> {
            if (fileChooser.showOpenDialog(openButton) == JFileChooser.APPROVE_OPTION) {
                gameFile = fileChooser.getSelectedFile();
                orphanNode.removeAllChildren();
                caveNode.removeAllChildren();
                caveNode.getChildCount();
                DefaultTreeModel model = (DefaultTreeModel)treeArea.getModel();
                model.reload();
                //TODO: collapse jtree?  needed?  dunno

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);  // i wanted to see which file was currently open
                frame.setTitle("Sorcerer's Cave [" + gameFile.toString() + "]");

                if (cave.getParties().size() > 0 || cave.getElements().size() > 0) {
                    // if the cave has been populated before, use a new cave
                    // essentially, every opening of a file constitutes a new game
                    cave = new Cave();
                } readInFile(); }});
        openButton.setFont(DEFAULT_FONT);

        displayButton = new JButton("Display");
        displayButton.addActionListener(ae -> {
            System.out.println("change me!");
            //TODO: make display meaningful
        });
        displayButton.setFont(DEFAULT_FONT);

        searchButton = new JButton("Search");
        searchButton.addActionListener(ae -> {
            if (!sentinel) {  // user never entered search criteria
                JOptionPane.showMessageDialog(null, "You can't search for anything with the default value." + NEWLINE);
            }
            else {
                search(searchSelectorBox.getSelectedItem().toString(), searchField.getText());
            }});
        searchButton.setFont(DEFAULT_FONT);

        // moved all the panel.adds down here, also for sanity
        treePanel.add(scrollPane, BorderLayout.CENTER);

        // splitPane vertical separation of jobs and cave display
        JSplitPane splitPaneVertical = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneVertical.setRightComponent(treePanel);
        splitPaneVertical.setLeftComponent(jobPanel);
        splitPaneVertical.setDividerLocation(0.1f);

        topPanel.add(splitPaneVertical, BorderLayout.CENTER);

        searchPanel.add(searchSelectorBox, BorderLayout.LINE_START);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.LINE_END);

        sortPanel.add(typeSelectorBox, BorderLayout.LINE_START);
        sortPanel.add(comparatorBox, BorderLayout.CENTER);
        sortPanel.add(sortButton, BorderLayout.LINE_END);

        controlPanel.add(openButton, BorderLayout.LINE_END);
        controlPanel.add(displayButton, BorderLayout.LINE_START);

        bottomPanel.add(sortPanel);
        bottomPanel.add(searchPanel);
        bottomPanel.add(controlPanel);

        JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneHorizontal.setTopComponent(topPanel);
        splitPaneHorizontal.setBottomComponent(bottomPanel);

        add(splitPaneHorizontal, BorderLayout.CENTER);
    }  // end constructor

    /**
     * Helper for producing some constant gridbagconstraints
     */
    private GridBagConstraints getBaseGBConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        return c;
    }  // end getBaseConstraints

    private void sort(String typeSelected, String comparatorSelected, Object obj) {
        // typeSelected = (Creatures, Treasures)
        // comparatorSelected = [(Weight, Value), (Name, Age, Weight, Height, Empathy, Fear, Carrying Capacity)]

        if (obj instanceof Cave) {
            Cave c = (Cave) obj;
            sort(typeSelected, comparatorSelected, c.getParties());
        }
        else if (obj instanceof ArrayList) {
            for (Object item : ((ArrayList) obj).toArray()) {
                if (item instanceof Party) {  // parties know how to sort creatures list
                    Party p = (Party) item;
                    if (typeSelected.equals("Creatures")) {
                        p.sortCreatures(comparatorSelected);
                    }
                    sort(typeSelected, comparatorSelected, p.getCreatures());
                }
                else if (item instanceof Creature) {  // creatures know how to sort treasures list
                    Creature c = (Creature) item;
                    if (typeSelected.equals("Treasures")) {
                        c.sortTreasures(comparatorSelected);
                    }
                }
            }
        }
    }  // end sort

    /**
     * Read in the file that the user chooses with game data.
     **/
    private void readInFile() {
        String line;
        HashMap<Integer, Party> partyLinker = new HashMap<>();
        HashMap<Integer, CaveElement> linker = new HashMap<>();
        HashMap<Integer, ToolTipTreeNode> treeLinker = new HashMap<>();
        Scanner sc;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gameFile));

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.startsWith("/")) {  // ignore empty lines, and comments
                    sc = new Scanner(line).useDelimiter("\\s*:\\s*");

                    ToolTipTreeNode childNode, parentNode;
                    StringBuilder toolTip;

                    String typeDefinition = sc.next();  // find type of the line
                    int index = sc.nextInt();

                    switch (typeDefinition) {
                        case PARTY:  //p:<index>:<name>
                            String name = sc.next();

                            Party party = new Party(name);
                            party.setIndex(index);

                            childNode = new ToolTipTreeNode(party.getName());

                            cave.getParties().add(party);
                            caveNode.add(childNode);

                            partyLinker.put(party.getIndex(), party);
                            treeLinker.put(party.getIndex(), childNode);

                            break;
                        case CREATURE:  //c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>[:<age>:<height>:<weight>]
                            String type = sc.next();
                            name = sc.next();
                            int partyIndex = sc.nextInt();
                            int empathy = sc.nextInt();
                            int fear = sc.nextInt();
                            int capacity = sc.nextInt();

                            Creature creature = new Creature(name);
                            toolTip = new StringBuilder();

                            toolTip.append("<html>");
                            toolTip.append(name + ": " + BREAK);
                            toolTip.append("Empathy: " + empathy + BREAK);
                            toolTip.append("Fear: " + fear + BREAK);
                            toolTip.append("Capacity: " + capacity + BREAK);

                            creature.setIndex(index);
                            creature.setType(type);
                            creature.setParty(partyIndex);
                            creature.setEmpathy(empathy);
                            creature.setFear(fear);
                            creature.setCapacity(capacity);

                            if (sc.hasNext()) {
                                double age = sc.nextDouble();

                                creature.setAge(age);
                                toolTip.append("Age: " + age + BREAK);
                            }
                            if (sc.hasNext()) {
                                double height = sc.nextDouble();

                                creature.setHeight(height);
                                toolTip.append("Height: " + height + BREAK);
                            }
                            if (sc.hasNext()) {
                                double weight = sc.nextDouble();

                                creature.setWeight(weight);
                                toolTip.append("Weight: " + weight + BREAK);
                            }
                            toolTip.append("</html>");
                            childNode = new ToolTipTreeNode(creature.getName(), toolTip.toString());

                            if (linker.get(creature.getParty()) != null) {
                                cave.getElements().add(creature);
                                orphanNode.add(childNode);
                            }
                            else {
                                Party p = partyLinker.get(creature.getParty());  // add to structure
                                p.getCreatures().add(creature);

                                parentNode = treeLinker.get(creature.getParty());  // add to tree
                                parentNode.add(childNode);
                            }

                            linker.put(creature.getIndex(), creature);
                            treeLinker.put(creature.getIndex(), childNode);

                            break;
                        case ARTIFACT:  //a:<index>:<type>:<creature>[:<name>]
                            Artifact artifact;
                            type = sc.next();
                            int creatureIndex = sc.nextInt();

                            if (sc.hasNext()) {
                                name = sc.next();
                                artifact = new Artifact(name);
                            }
                            else {
                                artifact = new Artifact(type);
                            }

                            artifact.setIndex(index);
                            artifact.setType(type);
                            artifact.setCreature(creatureIndex);

                            childNode = new ToolTipTreeNode(artifact.getName());

                            // orphan artifacts
                            if (artifact.getCreature() == 0) {
                                cave.getElements().add(artifact);
                                orphanNode.add(childNode);
                            }
                            else {
                                Creature c = (Creature) linker.get(artifact.getCreature());
                                c.getArtifacts().add(artifact);

                                parentNode = treeLinker.get(artifact.getCreature());
                                parentNode.add(childNode);
                            }

                            treeLinker.put(artifact.getIndex(), childNode);
                            linker.put(artifact.getIndex(), artifact);

                            break;
                        case TREASURE:  //t:<index>:<type>:<creature>:<weight>:<value>
                            type = sc.next();
                            creatureIndex = sc.nextInt();
                            double weight = sc.nextDouble();
                            int value = sc.nextInt();

                            Treasure treasure = new Treasure(type);

                            toolTip = new StringBuilder();
                            toolTip.append("<html>");
                            toolTip.append("Weight: " + weight + BREAK);
                            toolTip.append("Value: " + value + BREAK);
                            toolTip.append("</html>");

                            treasure.setIndex(index);
                            treasure.setCreature(creatureIndex);
                            treasure.setWeight(weight);
                            treasure.setValue(value);

                            childNode = new ToolTipTreeNode(treasure.getType(), toolTip.toString());

                            if (treasure.getCreature() == 0) {
                                cave.getElements().add(treasure);
                                orphanNode.add(childNode);
                            }
                            else {
                                Creature cr = (Creature) linker.get(treasure.getCreature());
                                cr.getTreasures().add(treasure);

                                parentNode = treeLinker.get(treasure.getCreature());
                                parentNode.add(childNode);
                            }

                            linker.put(treasure.getIndex(), treasure);
                            treeLinker.put(treasure.getIndex(), childNode);

                            break;
                        case "j":  // TODO: fix this
                            //Job j = new Job(linker, partyLinker, jobPanel, new Scanner(line).useDelimiter("\\s*:\\s*"));
                            System.out.println("stuff again");

                            break;

                        default:
                            System.out.println("Not Processed: " + line);
                    }
                }  // end line filter
            }  // end file reader loop
            bufferedReader.close();
            DefaultTreeModel model = (DefaultTreeModel) treeArea.getModel();
            model.reload();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }  // end readInFile

    /**
     * recursive search by name, index, or type
     */
    private CaveElement search(String type, String value, Object obj) {
        int index = -1;
        if (type.equals("Index")) {
            try {
                index = Integer.valueOf(value.trim());
            }
            catch (NumberFormatException e) {
                //textArea.append("You didn't specify an actual index.\n");
                JOptionPane.showMessageDialog(null, "You didn't specify an actual index." + NEWLINE);
                return null;
            }
        }

        if (obj instanceof Cave) {  // initially, the only possible calls are a Cave or an ArrayList
            search(type, value, ((Cave) obj).getParties());
            search(type, value, ((Cave) obj).getElements());
        }
        else if (obj instanceof ArrayList) {
            for (Object item : ((ArrayList) obj).toArray()) {
                if (item instanceof Party) {
                    Party p = (Party) item;

                    if (p.getName().equals(value) && type.equals("Name") || p.getIndex() == index && type.equals("Index")) {
                        searchDisplayHelper(p);
                        return p;
                    }
                    search(type, value, p.getCreatures());
                }
                else if (item instanceof Creature) {
                    Creature c = (Creature) item;

                    if (c.getName().equals(value) && type.equals("Name") || c.getIndex() == index && type.equals("Index")) {
                        searchDisplayHelper(c);
                        return c;
                    }
                    else if (c.getType().equals(value) && type.equals("Type")) {
                        typeList.add(c);
                    }
                    search(type, value, c.getArtifacts());
                    search(type, value, c.getTreasures());
                }
                else if (item instanceof Artifact) {
                    Artifact a = (Artifact) item;

                    if (a.getName().equals(value) && type.equals("Name") && !a.getName().equals("") || a.getIndex() == index && type.equals("Index")) {
                        searchDisplayHelper(a);
                        return a;
                    }
                    else if (a.getType().equals(value) && type.equals("Type")) {
                        typeList.add(a);
                    }
                }
                else if (item instanceof Treasure) {
                    Treasure t = (Treasure) item;

                    if (t.getName().equals(value) && type.equals("Name") && !t.getName().equals("") || t.getIndex() == index && type.equals("Index")) {
                        searchDisplayHelper(t);
                        return t;
                    }
                    else if (t.getType().equals(value) && type.equals("Type")) {
                        typeList.add(t);
                    }
                }
            }
        }
        return null;
    }  // end recursive search workhorse

    /**
     * search function that sets things up for the search, then calls the recursive search
     */
    private CaveElement search(String type, String value) {
        //textArea.setText(null);
        typeList = new ArrayList<CaveElement>();

        CaveElement ce = search(type, value, cave);

        if (typeList.size() > 0) {
            searchDisplayHelper(typeList);
        }
        return ce;
    }  // end search start function

    /**
     * Format search output that is more helpful than just a name
     */
    private void searchDisplayHelper(CaveElement element) {

        //textArea.append("Index: " + element.getIndex() + NEWLINE);
        //textArea.append("Name: " + element.getName() + NEWLINE);

        if (element instanceof Party) {
            Party party = (Party) element;
            //textArea.append("Creatures: \n");
            for (Creature creature: party.getCreatures()) {
                //textArea.append("-> " + creature.getName() + NEWLINE);
            }
        }
        else if (element instanceof Creature) {
            Creature creature = (Creature) element;
            if (creature.getParty() != 0) {
                Party p = (Party) search("Index", String.valueOf(creature.getParty()), cave.getParties());
                //textArea.append("Party: " + (p != null ? p.getName() : "Creature's Party not found.") + NEWLINE);
            }
            //textArea.append("Type: " + creature.getType() + NEWLINE);
            //textArea.append("Artifacts: \n" + creature.getArtifacts().toString() + NEWLINE);
            //textArea.append("Treasures: \n" + creature.getTreasures().toString() + NEWLINE);
            //textArea.append("Traits: \n");
            //textArea.append("-> Fear: " + creature.getFear() + NEWLINE);
            //textArea.append("-> Empathy: " + creature.getEmpathy() + NEWLINE);
            //textArea.append("-> Capacity: " + creature.getCapacity() + NEWLINE);
        }
        else if (element instanceof Artifact) {
            Artifact artifact = (Artifact) element;
            //textArea.append("Owner: " + artifact.getCreature() + NEWLINE);
            //textArea.append("Type: " + artifact.getType() + NEWLINE);
        }
        else if (element instanceof Treasure) {
            Treasure treasure = (Treasure) element;
            //textArea.append("Owner: " + treasure.getCreature() + NEWLINE);
            //textArea.append("Type: " + treasure.getType() + NEWLINE);
            //textArea.append("Value: " + treasure.getValue() + NEWLINE);
            //textArea.append("Weight: " + treasure.getWeight() + NEWLINE);
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
            //textArea.append("Index: " + element.getIndex() + NEWLINE);
            //textArea.append("Name: " + element.getName() + NEWLINE + NEWLINE);
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