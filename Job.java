import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * File: Job
 * Date: 2/6/16
 * Author: ben risher
 * Purpose:
 */
class Job extends CaveElement implements Runnable {
    static Random randomNumber = new Random();
    JPanel parent;
    Creature worker = null;
    int jobIndex;
    long jobTime;
    String jobName = "";
    JProgressBar progressBar = new JProgressBar ();
    boolean goFlag = true, noKillFlag = true;
    JButton jbGo   = new JButton ("Stop");
    JButton jbKill = new JButton ("Cancel");
    Status status = Status.SUSPENDED;

    HashMap<Integer, Creature> linker;
    HashMap<Integer, Party> partyLinker;

    enum Status {RUNNING, SUSPENDED, WAITING, DONE};

    public Job (HashMap<Integer, Creature> hc, HashMap<Integer, Party> hp, JPanel cv, Scanner sc) {

        parent = cv;
        linker = hc;
        partyLinker = hp;
        sc.next (); // dump first field, j
        jobIndex = sc.nextInt();
        jobName = sc.next();
        int target = sc.nextInt();
        worker = linker.get(target);
        jobTime = (int)(sc.nextDouble());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        parent.setLayout(new BoxLayout(parent, BoxLayout.PAGE_AXIS));

        JPanel topJobPanel = new JPanel(new BorderLayout());
        JPanel bottomJobPanel = new JPanel(new BorderLayout());

        JPanel totalPanel = new JPanel(new BorderLayout());

        topJobPanel.add(progressBar, BorderLayout.PAGE_END);
        topJobPanel.add(new JLabel(jobName, SwingConstants.CENTER), BorderLayout.LINE_END);

        bottomJobPanel.add(jbGo, BorderLayout.LINE_START);
        bottomJobPanel.add(jbKill, BorderLayout.LINE_END);

        totalPanel.add(topJobPanel, BorderLayout.PAGE_START);
        totalPanel.add(bottomJobPanel, BorderLayout.PAGE_END);
        totalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        parent.add(totalPanel);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));

        (new Thread(this, worker.getName() + " " + jobName)).start();


        jbGo.addActionListener(e -> toggleGoFlag());
        jbKill.addActionListener(e -> setKillFlag ());

    } // end constructor

//     JLabel jln = new JLabel (worker.name);
    // following is text alignment relative to icon
//     jln.setHorizontalTextPosition (SwingConstants.CENTER);
//     jln.setHorizontalAlignment (SwingConstants.CENTER);
//     parent.jrun.add (jln);

    public void toggleGoFlag () {
        goFlag = !goFlag; // ND; should be synced, and notify waiting sync in running loop
    } // end method toggleRunFlag

    public void setKillFlag () {
        noKillFlag = false;
        jbKill.setBackground (Color.red);
    } // end setKillFlag

    void showStatus (Status st) {
        status = st;
        switch (status) {
            case RUNNING:
                jbGo.setBackground (Color.green);
                jbGo.setText ("Running");
                break;
            case SUSPENDED:
                jbGo.setBackground (Color.yellow);
                jbGo.setText ("Suspended");
                break;
            case WAITING:
                jbGo.setBackground (Color.orange);
                jbGo.setText ("Waiting turn");
                break;
            case DONE:
                jbGo.setBackground (Color.red);
                jbGo.setText ("Done");
                break;
        } // end switch on status
    } // end showStatus

    public void run () {
        long time = System.currentTimeMillis();
        long startTime = time;
        long stopTime = time + 1000 * jobTime;
        double duration = stopTime - time;

        synchronized (this.partyLinker.get(worker.getParty())) {
            while (worker.busyFlag) {
                showStatus (Status.WAITING);
                try {
                    this.partyLinker.get(worker.getParty()).wait();
                }
                catch (InterruptedException e) {
                } // end try/catch block
            } // end while waiting for worker to be free
            worker.busyFlag = true;
        } // end sychronized on worker

        while (time < stopTime && noKillFlag) {
            try {
                Thread.sleep (100);
            } catch (InterruptedException e) {}
            if (goFlag) {
                showStatus (Status.RUNNING);
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

        showStatus (Status.DONE);
        synchronized (this.partyLinker.get(worker.getParty())) {
            worker.busyFlag = false;
            this.partyLinker.get(worker.getParty()).notifyAll();
        }

    } // end method run - implements runnable

    public String toString () {
        String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, worker.getIndex(), jobTime);
        return sr;
    } //end method toString

} // end class Job