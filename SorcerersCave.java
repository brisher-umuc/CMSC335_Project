/**
 * File: SorcerersCave
 * Date: 1/6/16
 * Author: ben risher
 * Purpose: user interface for the sorcerers cave game project
 */
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SorcerersCave extends JPanel {
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final String PARTY = "p";
    private static final String CREATURE = "c";
    private static final String ARTIFACT = "a";
    private static final String TREASURE = "t";
    private static final String JOB = "j";
    private static final String NEWLINE = "\n";
    private static final String BREAK = "<br>";
    public static final int BUTTON_WIDTH = 150;
    private static final long serialVersionUID = 4444L;

    private final JFileChooser fileChooser;
    private final JButton openButton;
    private File gameFile;
    private final JTree treeDisplay;
    private ToolTipTreeNode caveNode, orphanNode;
    private static JTextField searchField;
    private final JComboBox<String> searchSelectorBox,comparatorBox, typeSelectorBox;
    private static Boolean sentinel = false;
    private JPanel jobPanel, topPanel;
    private JTextArea poolDisplay;
    private JScrollPane jobScrollPane;

    private final HashMap<String, ArrayList<String>> attributesList = new HashMap<>();

    private Cave cave = new Cave();
    private ArrayList<CaveElement> typeList;

    /**
     * Constructor for Sorcerer's Cave
     */
    public SorcerersCave() {  // constructor
        setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        //setPreferredSize(new Dimension(1100, 900));
        JPanel bottomPanel;

        topPanel = new JPanel(new GridBagLayout());
        bottomPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(5,5,5,5));
        bottomPanel.setBorder(new EmptyBorder(5,5,5,5));

        JButton searchButton, displayButton, sortButton;

        // attributes list to populate the sort comparator dropdown
        ArrayList<String> creatureList = new ArrayList<>();
        creatureList.add("Name");
        creatureList.add("Age");
        creatureList.add("Height");
        creatureList.add("Weight");
        creatureList.add("Empathy");
        creatureList.add("Fear");
        creatureList.add("Carrying Capacity");
        attributesList.put("Creatures", creatureList);

        ArrayList<String> treasureList = new ArrayList<>();
        treasureList.add("Weight");
        treasureList.add("Value");

        attributesList.put("Treasures", treasureList);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        // top panel ===================================================================================================
        // JTree display area
        ToolTipTreeNode pseudoRootNode = new ToolTipTreeNode("I'm hidden");  // workaround for having multi-rooted jtree
        caveNode = new ToolTipTreeNode("The Cave");
        orphanNode = new ToolTipTreeNode("The Orphans");
        treeDisplay = new JTree(pseudoRootNode) {
            public static final long serialVersionUID = 98432; // ND: anonymous inner class!

            public String getToolTipText(MouseEvent evt) {
                if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
                    return null;
                }
                TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                return ((ToolTipTreeNode) curPath.getLastPathComponent()).getToolTipText();
            }
        };
        treeDisplay.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Cave Display"));
        treeDisplay.setToolTipText("");
        pseudoRootNode.add(caveNode);
        pseudoRootNode.add(orphanNode);
        treeDisplay.expandRow(0);
        treeDisplay.setRootVisible(false);
        treeDisplay.setShowsRootHandles(true);
        ToolTipManager.sharedInstance().registerComponent(treeDisplay);
        treeDisplay.setFont(DEFAULT_FONT);
        JScrollPane treeScrollPane = new JScrollPane(treeDisplay);
        treeScrollPane.setPreferredSize(new Dimension(350, 500));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        topPanel.add(treeScrollPane, gbc);


        // job panel, houses ... whatever im going to use to display jobs
        jobPanel = new JPanel();
        jobPanel.setLayout(new GridLayout(0, 1));
        jobPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Current Jobs"));
        jobScrollPane = new JScrollPane(jobPanel);


        jobScrollPane.setPreferredSize(new Dimension(700, 500));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 4;
        topPanel.add(jobScrollPane, gbc);

        // add toppanel
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(topPanel, gbc);
        // end top panel ===============================================================================================

        // bottom panel ===============================================================================================
        poolDisplay = new JTextArea();
        poolDisplay.setRows(7);
        poolDisplay.setEditable(false);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        poolDisplay.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), "Resource Pools"));
        bottomPanel.add(poolDisplay, gbc);

        // search panel, houses searchField, searchButton and searchSelectorBox
        searchSelectorBox = new JComboBox<>();
        searchSelectorBox.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        searchSelectorBox.addItem("Index");
        searchSelectorBox.addItem("Type");
        searchSelectorBox.addItem("Name");
        searchSelectorBox.setFont(DEFAULT_FONT);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0,5,5,5);
        bottomPanel.add(searchSelectorBox, gbc);

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
        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(searchField, gbc);

        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        searchButton.addActionListener(ae -> {
            if (!sentinel) {  // user never entered search criteria
                JOptionPane.showMessageDialog(null, "You can't search for anything with the default value." + NEWLINE);
            }
            else {
                search(searchSelectorBox.getSelectedItem().toString(), searchField.getText());
            }});
        searchButton.setFont(DEFAULT_FONT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 2;
        gbc.gridx = 6;
        bottomPanel.add(searchButton, gbc);

        // sort panel, houses sortButton, comparatorBox, typeSelectorBox
        comparatorBox = new JComboBox<>();
        comparatorBox.addItem("Weight");
        comparatorBox.addItem("Value");
        comparatorBox.setFont(DEFAULT_FONT);
        comparatorBox.addActionListener(ae -> {
            if (comparatorBox.getItemCount() > 0) {
                String s = getJTreeState();
                populateCave();
                setJTreeState(s);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(comparatorBox, gbc);

        typeSelectorBox = new JComboBox<>();
        typeSelectorBox.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        typeSelectorBox.addItem("Treasures");
        typeSelectorBox.addItem("Creatures");
        typeSelectorBox.addActionListener(ae -> {
            comparatorBox.removeAllItems();
            attributesList.get(typeSelectorBox.getSelectedItem().toString()).forEach(comparatorBox::addItem);
        });
        typeSelectorBox.setFont(DEFAULT_FONT);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 5, 5);
        bottomPanel.add(typeSelectorBox, gbc);

        sortButton = new JButton("Sort");
        sortButton.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        sortButton.addActionListener(ae -> {
            sort(typeSelectorBox.getSelectedItem().toString(), comparatorBox.getSelectedItem().toString(), cave);
        });
        sortButton.setFont(DEFAULT_FONT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 1;
        gbc.gridx = 6;
        bottomPanel.add(sortButton, gbc);

        // control panel, houses openButton, displayButton, searchButton

        openButton = new JButton("Open File");
        openButton.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        openButton.addActionListener(ae -> {
            if (fileChooser.showOpenDialog(openButton) == JFileChooser.APPROVE_OPTION) {
                gameFile = fileChooser.getSelectedFile();
                orphanNode.removeAllChildren();
                caveNode.removeAllChildren();
                caveNode.getChildCount();
                DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
                model.reload();

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);  // i wanted to see which file was currently open
                frame.setTitle("Sorcerer's Cave [" + gameFile.toString() + "]");

                if (cave.getParties().size() > 0 || cave.getElements().size() > 0) {
                    // if the cave has been populated before, use a new cave
                    // essentially, every opening of a file constitutes a new game
                    cave = new Cave();
                    jobPanel.removeAll();  // have to update the jobpanel
                    jobPanel.repaint();
                    jobPanel.revalidate();
                } readInFile(); }});
        openButton.setFont(DEFAULT_FONT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 5, 5);
        gbc.gridy = 3;
        gbc.gridx = 6;
        bottomPanel.add(openButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(new JLabel(), gbc);

        displayButton = new JButton("Display");
        displayButton.addActionListener(ae -> {
            JTextArea textArea = new JTextArea(cave.toString());
            JScrollPane sp = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            sp.setPreferredSize( new Dimension( 500, 500 ) );
            JOptionPane.showMessageDialog(null, sp);
        });
        displayButton.setFont(DEFAULT_FONT);
        displayButton.setPreferredSize(new Dimension(BUTTON_WIDTH, 26));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0,5,0,5);
        bottomPanel.add(displayButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_END;
        this.add(bottomPanel, gbc);

        // end bottom panel ============================================================================================

    }  // end constructor

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
        populateCave();
    }  // end sort

    /**
     * tooltip builder for Creature type
     */
    private String buildTooltip(Creature c) {
        StringBuilder tt = new StringBuilder();

        tt.append("<html>");
        tt.append(c.getName() + ": " + BREAK);
        tt.append("Empathy: " + c.getEmpathy() + BREAK);
        tt.append("Fear: " + c.getFear() + BREAK);
        tt.append("Capacity: " + c.getCapacity() + BREAK);
        tt.append("Age: " + c.getAge() + BREAK);
        tt.append("Height: " + c.getHeight() + BREAK);
        tt.append("Weight: " + c.getWeight() + BREAK);
        tt.append("</html>");

        return tt.toString();
    }

    /**
     * tooltip builder for Treasure type
     */
    private String buildTooltip(Treasure t) {
        StringBuilder tt = new StringBuilder();

        tt.append("<html>");
        tt.append("Weight: " + t.getWeight() + BREAK);
        tt.append("Value: " + t.getValue() + BREAK);
        tt.append("</html>");

        return tt.toString();
    }

    /**
     * Displays the currently selected sorter value for each Treasure.
     *
     * Ex.  User has <Treasure> and <Weight> Selected as sort parameters, each
     *      Treasure will have a (W: XX.X) added to its display in the tree.
     *
     * The display's letter is the first letter of the value selected [W]eight, [H]eight, etc...
     */
    private String buildAdditionalInfo(Treasure t) {
        String s = "";
        Method method;

        char firstChar = comparatorBox.getSelectedItem().toString().charAt(0);

        if ((firstChar != 'V' && firstChar != 'W') || typeSelectorBox.getSelectedItem().equals("Creatures")) {
            return "";
        }

        try {
            method = t.getClass().getMethod("get" + firstChar);
            s += " (" + firstChar + ": " + method.invoke(t) + ")";
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * Displays the currently selected sorter value for each Creature.
     *
     * Ex.  User has <Creature> and <Age> Selected as sort parameters, each
     *      Treasure will have a (A: XX.X) added to its display in the tree.
     *
     * The display's letter is the first letter of the value selected [A]ge, [H]eight, etc...
     */
    private String buildAdditionalInfo(Creature c) {
        String s = "";
        Method method;

        char firstChar = comparatorBox.getSelectedItem().toString().charAt(0);

        if (firstChar == 'V' || firstChar == 'N' || typeSelectorBox.getSelectedItem().equals("Treasures")) {
            return "";
        }

        try {
            method = c.getClass().getMethod("get" + firstChar);
            s += " (" + firstChar + ": " + method.invoke(c) + ")";
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * Populate a new JTree based off of the underlying multitree structure
     *
     * This is called when a user
     *      1:  presses <Sort>
     *      2:  presses <Open File>
     *      3:  presses <Search>
     */
    private void populateCave() {
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();
        caveNode = new ToolTipTreeNode("The Cave");
        orphanNode = new ToolTipTreeNode("The Orphans");
        root.add(caveNode);
        root.add(orphanNode);

        String tooltip = "";

        for (Party p: cave.getParties()) {  // parties and children
            ToolTipTreeNode localParty = new ToolTipTreeNode(p.getName());
            caveNode.add(localParty);

            for (Creature c : p.getCreatures()) {

                tooltip = buildTooltip(c);

                ToolTipTreeNode localCreature = new ToolTipTreeNode(c.getName() + buildAdditionalInfo(c), tooltip);
                localParty.add(localCreature);

                for (Artifact a: c.getArtifacts()) {
                    ToolTipTreeNode localArtifact = new ToolTipTreeNode(a.getName());
                    localCreature.add(localArtifact);
                }
                for (Treasure t: c.getTreasures()) {
                    tooltip = buildTooltip(t);

                    ToolTipTreeNode localTreasure = new ToolTipTreeNode(t.getName() + buildAdditionalInfo(t), tooltip);
                    localCreature.add(localTreasure);
                }
            }
        }
        populateOrphans();

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();
    }

    /**
     * Populate the orphan nodes of the existing JTree
     *
     * This is called anytime a call to populateCave is made
     */
     private void populateOrphans() {
        String tooltip = "";

        for (CaveElement ce: cave.getElements()) {  // orphans
            if (ce instanceof Treasure) {
                tooltip = buildTooltip((Treasure) ce);
                ToolTipTreeNode localTreasure = new ToolTipTreeNode(ce.getName() + buildAdditionalInfo((Treasure) ce), tooltip);
                orphanNode.add(localTreasure);
            }
            else if (ce instanceof Creature) {
                Creature c = (Creature) ce;
                tooltip = buildTooltip(c);
                ToolTipTreeNode localCreature = new ToolTipTreeNode(ce.getName() + buildAdditionalInfo(c), tooltip);

                for (Artifact a: c.getArtifacts()) {
                    ToolTipTreeNode localArtifact = new ToolTipTreeNode(a.getName());
                    localCreature.add(localArtifact);
                }
                for (Treasure t: c.getTreasures()) {
                    tooltip = buildTooltip(t);
                    ToolTipTreeNode localTreasure = new ToolTipTreeNode(t.getName() + buildAdditionalInfo(t), tooltip);
                    localCreature.add(localTreasure);
                }

                orphanNode.add(localCreature);
            }
        }
    }

    /**
     * Read in the file that the user chooses with game data.
     **/
    private void readInFile() {
        String line;
        HashMap<Integer, Party> partyLinker = new HashMap<>();
        HashMap<Integer, Creature> creatureLinker = new HashMap<>();
        Scanner sc;
        HashMap<String, ArrayList<String>> jobsHashMap = new HashMap<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gameFile));

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.startsWith("/")) {  // ignore empty lines, and comments
                    sc = new Scanner(line).useDelimiter("\\s*:\\s*");

                    String typeDefinition = sc.next();  // find type of the line
                    int index = sc.nextInt();

                    switch (typeDefinition) {
                        case PARTY:  //p:<index>:<name>
                            String name = sc.next();

                            Party party = new Party(name);
                            party.setIndex(index);

                            cave.getParties().add(party);
                            partyLinker.put(party.getIndex(), party);

                            break;
                        case CREATURE:  //c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>[:<age>:<height>:<weight>]
                            String type = sc.next();
                            name = sc.next();
                            int partyIndex = sc.nextInt();
                            int empathy = sc.nextInt();
                            int fear = sc.nextInt();
                            int capacity = sc.nextInt();

                            Creature creature = new Creature(name, index, type, partyIndex, empathy, fear, capacity);

                            if (sc.hasNext()) {
                                double age = sc.nextDouble();
                                creature.setAge(age);
                            }
                            if (sc.hasNext()) {
                                double height = sc.nextDouble();
                                creature.setHeight(height);
                            }
                            if (sc.hasNext()) {
                                double weight = sc.nextDouble();
                                creature.setWeight(weight);
                            }

                            if (partyLinker.get(creature.getParty()) == null) {
                                cave.getElements().add(creature);
                            }
                            else {
                                Party p = partyLinker.get(creature.getParty());  // add to structure
                                p.getCreatures().add(creature);
                            }

                            creatureLinker.put(creature.getIndex(), creature);

                            break;
                        case ARTIFACT:  //a:<index>:<type>:<creature>[:<name>]
                            Artifact artifact;
                            type = sc.next();
                            int creatureIndex = sc.nextInt();

                            if (sc.hasNext()) {
                                name = sc.next();
                                artifact = new Artifact(name, index, type, creatureIndex);
                            }
                            else {
                                artifact = new Artifact(type, index, type, creatureIndex);
                            }

                            // orphan artifacts
                            if (creatureLinker.get(artifact.getCreature()) == null) {
                                cave.getElements().add(artifact);
                            }
                            else {
                                Creature c = creatureLinker.get(artifact.getCreature());
                                c.getArtifacts().add(artifact);

                                Party p = partyLinker.get(c.getParty());
                                ResourcePool pool = p.getPool();

                                pool.setParty(p);

                                //
                                if (pool.getResource(artifact.getType()) != null) {
                                    pool.getResource(artifact.getType()).incrementCount();
                                }
                                else {
                                    Resource r = new Resource(artifact.getType());
                                    r.incrementCount();

                                    pool.setResource(artifact.getType(), r);
                                }
                            }

                            break;
                        case TREASURE:  //t:<index>:<type>:<creature>:<weight>:<value>
                            type = sc.next();
                            creatureIndex = sc.nextInt();
                            double weight = sc.nextDouble();
                            int value = sc.nextInt();

                            Treasure treasure = new Treasure(type, index, creatureIndex, weight, value);

                            if (creatureLinker.get(treasure.getCreature()) == null) {
                                cave.getElements().add(treasure);
                            }
                            else {
                                Creature cr = creatureLinker.get(treasure.getCreature());
                                cr.getTreasures().add(treasure);
                            }

                            break;
                        case JOB:
                            sc.next();  // dump the job name
                            Creature worker = creatureLinker.get(sc.nextInt());

                            if (jobsHashMap.get(worker.getName()) != null) {
                                jobsHashMap.get(worker.getName()).add(line);
                            }
                            else {
                                ArrayList<String> localList = new ArrayList<>();
                                localList.add(line);
                                jobsHashMap.put(worker.getName(), localList);
                            }

                            break;
                        default:
                            System.out.println("Not Processed: " + line);
                    }
                }  // end line filter
            }  // end file reader loop
            bufferedReader.close();

            if (jobsHashMap.size() > 0) {
                HashMap<String, HashMap<String, Integer>> maxResources = new HashMap<>();

                for (Party p: cave.getParties()) {
                    HashMap<String, Integer> localMap = new HashMap<>();

                    for (Map.Entry<String, Resource> entry: p.getPool().getProperties().entrySet()) {
                        localMap.put(entry.getKey(), entry.getValue().getCount());
                    }
                    maxResources.put(p.getName(), localMap);
                }
                StringBuilder sb = new StringBuilder();

                for (Party p: cave.getParties()) {
                    try {
                        ResourcePool rp = p.getPool();
                        if (rp != null) {
                            sb.append(rp.toString());
                        }
                    }
                    catch (NullPointerException e) {
                    }
                }

                poolDisplay.setText(sb.toString());

                new JobAggregator(creatureLinker, partyLinker, jobPanel, jobsHashMap, maxResources, poolDisplay);
            }

            populateCave();
            DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
            model.reload();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }  // end readInFile

    /**
     * helper to establish jtree state after certain actions
     */
    private String getJTreeState() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < treeDisplay.getRowCount(); ++i) {
            if (treeDisplay.isExpanded(i)) {
                sb.append(i).append(",");
            }
        }
        return sb.toString();
    }

    /**
     * helper to reestablish jtree state after certain actions
     */
    private void setJTreeState(String s) {
        String[] indices = s.split(",");
        for (String st: indices) {
            if (!st.isEmpty()) {
                int row = Integer.parseInt(st);
                treeDisplay.expandRow(row);
            }
        }
    }

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
                        displaySearchResults(p);
                        return p;
                    }
                    search(type, value, p.getCreatures());
                }
                else if (item instanceof Creature) {
                    Creature c = (Creature) item;

                    if (c.getName().equals(value) && type.equals("Name") || c.getIndex() == index && type.equals("Index")) {
                        displaySearchResults(c);
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
                        displaySearchResults(a);
                        return a;
                    }
                    else if (a.getType().equals(value) && type.equals("Type")) {
                        typeList.add(a);
                    }
                }
                else if (item instanceof Treasure) {
                    Treasure t = (Treasure) item;

                    if (t.getName().equals(value) && type.equals("Name") && !t.getName().equals("") || t.getIndex() == index && type.equals("Index")) {
                        displaySearchResults(t);
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
        typeList = new ArrayList<>();

        CaveElement ce = search(type, value, cave);

        if (typeList.size() > 0) {
            displaySearchResults();
        }

        return ce;
    }  // end search start function

    /**
     * display results from pressing <Search> in the JTree, with applicable attributes
     */
    private void displaySearchResults(Party party) {
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();

        ToolTipTreeNode partyNode = new ToolTipTreeNode(party.getName());
        ToolTipTreeNode indexNode = new ToolTipTreeNode("Index: " + String.valueOf(party.getIndex()));

        root.add(partyNode);
        partyNode.add(indexNode);

        for (Creature c: party.getCreatures()) {
            ToolTipTreeNode creatureNode = new ToolTipTreeNode("Creature: " + c.getName());
            partyNode.add(creatureNode);
        }

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();
    }

    /**
     * display results from pressing <Search> in the JTree, with applicable attributes
     */
    private void displaySearchResults(Creature creature) {
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();

        String tooltip = buildTooltip(creature);

        ToolTipTreeNode creatureNode = new ToolTipTreeNode(creature.getName(), tooltip);
        creatureNode.add(new ToolTipTreeNode("Index: " + creature.getIndex()));
        creatureNode.add(new ToolTipTreeNode("Type: " + creature.getType()));
        creatureNode.add(new ToolTipTreeNode("Party: " + creature.getParty()));
        creatureNode.add(new ToolTipTreeNode("Empathy: " + creature.getEmpathy()));
        creatureNode.add(new ToolTipTreeNode("Fear: " + creature.getFear()));
        creatureNode.add(new ToolTipTreeNode("Capacity: " + creature.getCapacity()));
        creatureNode.add(new ToolTipTreeNode("Height: " + creature.getHeight()));
        creatureNode.add(new ToolTipTreeNode("Weight: " + creature.getWeight()));
        creatureNode.add(new ToolTipTreeNode("Age: " + creature.getAge()));

        root.add(creatureNode);

        for (Artifact a: creature.getArtifacts()) {
            creatureNode.add(new ToolTipTreeNode("Artifact: " + a.getName()));
        }
        for (Treasure t: creature.getTreasures()) {
            creatureNode.add(new ToolTipTreeNode("Treasure: " + t.getName()));
        }

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();
    }

    /**
     * display results from pressing <Search> in the JTree, with applicable attributes
     */
    private void displaySearchResults() {
        // i kept this display relatively simple, since it's returning an unknown number of values
        // i think the user could use this as a point to start drilling down
        // i.e. search for type=woman, then use a more specific search for more information
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();

        String tooltip = "";

        for (CaveElement element: typeList) {
            if (element instanceof Creature) {
                 tooltip = buildTooltip((Creature) element);
            }
            else if (element instanceof Treasure) {
                tooltip = buildTooltip((Treasure) element);
            }
            ToolTipTreeNode elementNode = new ToolTipTreeNode(element.getName(), tooltip);
            elementNode.add(new ToolTipTreeNode("Index: " + element.getIndex()));
            root.add(elementNode);
        }

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();
    }

    /**
     * display results from pressing <Search> in the JTree, with applicable attributes
     */
    private void displaySearchResults(Artifact artifact) {
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();

        ToolTipTreeNode artifactNode = new ToolTipTreeNode(artifact.getName());
        artifactNode.add(new ToolTipTreeNode("Index: " + artifact.getIndex()));
        artifactNode.add(new ToolTipTreeNode("Type: " + artifact.getType()));
        artifactNode.add(new ToolTipTreeNode("Creature: " + artifact.getCreature()));

        root.add(artifactNode);

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();
    }

    /**
     * display results from pressing <Search> in the JTree, with applicable attributes
     */
    private void displaySearchResults(Treasure treasure) {
        ToolTipTreeNode root = (ToolTipTreeNode) treeDisplay.getModel().getRoot();
        root.removeAllChildren();

        String tooltip = buildTooltip(treasure);

        ToolTipTreeNode treasureNode = new ToolTipTreeNode(treasure.getName(), tooltip);
        treasureNode.add(new ToolTipTreeNode("Index: " + treasure.getIndex()));
        treasureNode.add(new ToolTipTreeNode("Type: " + treasure.getType()));
        treasureNode.add(new ToolTipTreeNode("Creature: " + treasure.getCreature()));
        treasureNode.add(new ToolTipTreeNode("Weight: " + treasure.getWeight()));
        treasureNode.add(new ToolTipTreeNode("Value: " + treasure.getValue()));

        root.add(treasureNode);

        DefaultTreeModel model = (DefaultTreeModel) treeDisplay.getModel();
        model.reload();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Sorcerer's Cave");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new SorcerersCave());
                frame.pack();
                frame.setVisible(true);
            }
        });
    } // end main
}