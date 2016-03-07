import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * File: JobAggregator
 * Date: 2/14/16
 * Author: ben risher
 * Purpose:  collect jobs with the same creature under one heading for more friendly display
 */

/**
 * Simple wrapper to Job class to keep a creature's jobs grouped
 */
public class JobAggregator {
    public JobAggregator(HashMap<Integer, Creature> linker, HashMap<Integer, Party> partyLinker, JPanel parent, HashMap<String, ArrayList<String>> jobsHashMap, HashMap<String, HashMap<String, Integer>> maxResources, JTextArea poolDisplay) {

        for (Map.Entry<String, ArrayList<String>> entry: jobsHashMap.entrySet()) {
            JPanel creaturesJobsPanel = new JPanel();
            creaturesJobsPanel.setLayout(new BoxLayout(creaturesJobsPanel, BoxLayout.PAGE_AXIS));

            for (String line: entry.getValue()) {
                new Job(linker, partyLinker, creaturesJobsPanel, new Scanner(line).useDelimiter("\\s*:\\s*"), maxResources, poolDisplay);
            }

            creaturesJobsPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 2), entry.getKey()));
            parent.add(creaturesJobsPanel);
        }
    }
}
