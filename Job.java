import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * File: Job
 * Date: 2/6/16
 * Author: ben risher
 * Purpose:
 */
class Job extends CaveElement implements Runnable {
    JPanel parent;
    Creature creature = null;
    private final Party party;
    int jobIndex, target;
    long jobTime;
    String jobName = "";
    JProgressBar progressBar = new JProgressBar ();
    boolean goFlag = true, noKillFlag = true;
    JButton jbGo   = new JButton ("Stop");
    JButton jbKill = new JButton ("Cancel");
    Status status = Status.SUSPENDED;
    JTextArea  poolDisplay;
    GridBagConstraints gbc;
    public static final int FIELDWIDTH = 160;

    HashMap<Integer, Creature> linker;
    HashMap<Integer, Party> partyLinker;
    HashMap<String, Integer> requirements = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> maxResources;

    enum Status {RUNNING, SUSPENDED, RESOURCEWAIT, CREATUREWAIT, STARTED, FINISHED, CANTPROCEED};

    public Job (HashMap<Integer, Creature> hc, HashMap<Integer, Party> hp, JPanel cv, Scanner sc, HashMap<String, HashMap<String, Integer>> maximums, JTextArea pD) {
        sc.next (); // dump first field, j

        parent = cv;
        linker = hc;
        partyLinker = hp;
        maxResources = maximums;
        poolDisplay = pD;

        jobIndex = sc.nextInt();
        jobName = sc.next();
        target = sc.nextInt();
        jobTime = (int)(sc.nextDouble());

        creature = linker.get(target);
        party = this.partyLinker.get(creature.getParty());

        while (sc.hasNext()) {  // resource requirements, probably set them as some kind of attribute
            String type = sc.next();
            if (type.endsWith("s")) {
                type = type.substring(0, type.length() - 1);
            }
            int amount = sc.nextInt();

            if (amount == 0) {  // if requirement for a type is 0, don't care about tracking it
                continue;
            }

            requirements.put(type, amount);
        }

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        JPanel thisJobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel nameLabel = new JLabel(jobName + ": " );
        nameLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        nameLabel.setPreferredSize(new Dimension(100, 35));
        thisJobPanel.add(nameLabel);

        progressBar.setBorder(new EmptyBorder(0, 0, 0, 5));
        progressBar.setPreferredSize(new Dimension(100, 35));
        thisJobPanel.add(progressBar);

        jbGo.setPreferredSize(new Dimension(275, 35));
        thisJobPanel.add(jbGo);

        jbKill.setPreferredSize(new Dimension(125, 35));
        thisJobPanel.add(jbKill);

        thisJobPanel.setPreferredSize(new Dimension(640, 30));

        parent.add(thisJobPanel);

        (new Thread(this, creature.getName() + " " + jobName)).start();

        jbGo.addActionListener(e -> toggleGoFlag());
        jbKill.addActionListener(e -> setKillFlag ());

    } // end constructor

    public void toggleGoFlag () {
        goFlag = !goFlag; // ND; should be synced, and notify waiting sync in running loop
    } // end method toggleRunFlag

    public void setKillFlag () {
        noKillFlag = false;
        jbKill.setBackground (Color.red);
    } // end setKillFlag

    void showStatus (Status st) {
        status = st;
        String rsb;
        switch (status) {
            case RUNNING:
                jbGo.setBackground (Color.green);
                rsb = "Holding -> ";

                for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                    Resource res = party.getPool().getResource(entry.getKey());

                    if (res != null) {
                        res.decrementCount(entry.getValue());
                        rsb += String.valueOf(res.getName().charAt(0)) + ": " + entry.getValue() + " ";
                    }
                }
                jbGo.setText(rsb);
                break;
            case SUSPENDED:
                jbGo.setBackground (Color.yellow);
                jbGo.setText ("Suspended");
                break;
            case RESOURCEWAIT:
                jbGo.setBackground (Color.orange);
                rsb = "Waiting -> ";
                for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                    Resource res = party.getPool().getResource(entry.getKey());
                    if (res != null) {
                        rsb += String.valueOf(res.getName().charAt(0)) + ": " + entry.getValue() + " ";
                    }
                }
                jbGo.setText (rsb);
                break;
            case CANTPROCEED:
                jbGo.setBackground (Color.red);
                jbGo.setText ("Can't Proceed");
                break;
            case STARTED:
                jbGo.setBackground(Color.CYAN);
                jbGo.setText("Started");
                break;
            case FINISHED:
                jbGo.setBackground(Color.MAGENTA);
                jbGo.setText("Done");
                break;
            case CREATUREWAIT:
                jbGo.setBackground(Color.orange);
                jbGo.setText("Waiting");
                break;
        } // end switch on status
    } // end showStatus

    public void run () {
        long time = System.currentTimeMillis();
        long startTime = time;
        long stopTime = time + 1000 * jobTime;
        double duration = stopTime - time;

        synchronized (party.getPool()) {  // lock the pool
            while (true) {
                boolean sentry = true;
                boolean killSwitch = false;

                for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                    Resource res = party.getPool().getResource(entry.getKey());
                    if (res == null) {
                        // unmet requirement, i.e. the pool never even got an object of this type, so it has 0, and there's a requirement for it
                        killSwitch = true;
                    }
                    if (res != null && entry.getValue() > res.getCount()) {  // requirement higher than available resource
                        sentry = false;
                    }

                    if (res != null && entry.getValue() > maxResources.get(party.getName()).get(entry.getKey())) {
                        killSwitch = true;  // the requirement is higher than the maximum possible amount of that resource
                    }
                }

                if (killSwitch) {
                    showStatus(Status.CANTPROCEED);
                    return;
                } else if (sentry && ! creature.holdsResources) {  // sentry never got flagged false, can safely get all things
                    showStatus(Status.RUNNING);
                    creature.holdsResources = true;

                    StringBuilder sb = new StringBuilder();

                    for (String st : poolDisplay.getText().split("\n")) {
                        if (st.startsWith(party.getName())) {
                            sb.append(party.getPool().toString());
                        }
                        else {
                            sb.append(st + "\n");
                        }
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            poolDisplay.setText(sb.toString());
                        }
                    });
                    break;
                } else {
                    try {
                        showStatus(Status.RESOURCEWAIT);
                        party.getPool().wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }  // end pool sync

        synchronized (creature) {
            while (creature.busyFlag) {
                showStatus (Status.CREATUREWAIT);
                try {
                    creature.wait();  // if the Creature is busy already, chill out and wait til you're called with a notify
                }
                catch (InterruptedException e) {
                } // end try/catch block
            } // end while waiting for creature to be free
            creature.busyFlag = true;
        } // end sychronized on creature


        while (time < stopTime && noKillFlag) {
            try {
                Thread.sleep (100);
            } catch (InterruptedException e) {}
                if (goFlag) {
                    time += 100;
                    progressBar.setValue((int) (((time - startTime) / duration) * 100));
                } else {
                    showStatus (Status.SUSPENDED); // should wait here, not busy looop
            } // end if stepping
        } // end runninig

        if (!noKillFlag) {
            progressBar.setValue(0);
        }
        else {
            progressBar.setValue (100);
        }

        synchronized (party.getPool()) {
            for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                Resource res = party.getPool().getResource(entry.getKey());
                if (res != null) {
                    res.incrementCount(entry.getValue());
                }
            }
            creature.holdsResources = false;

            StringBuilder sb = new StringBuilder();

            for (String st : poolDisplay.getText().split("\n")) {
                if (st.startsWith(party.getName())) {
                    sb.append(party.getPool().toString());
                }
                else {
                    sb.append(st + "\n");
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    poolDisplay.setText(sb.toString());
                }
            });
            party.getPool().notifyAll();  // all
        }  // end pool sync


        showStatus(Status.FINISHED);
        synchronized (creature) {
            creature.busyFlag = false;
            showStatus(Status.FINISHED);
            creature.notifyAll();
        }


    } // end method run - implements runnable

    public String toString () {
        String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, creature.getIndex(), jobTime);
        return sr;
    } //end method toString

} // end class Job