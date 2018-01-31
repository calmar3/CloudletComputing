package cloudlet;

import config.AppConfiguration;
import event.AbstractEvent;
import event.CompletionEvent;
import event.EventQueue;
import task.AbstractTask;
import task.TaskClassOne;
import task.TaskClassTwo;
import utils.Distributions;

import java.util.ArrayList;
import java.util.List;

public class Cloudlet {

    /* Number of Class 1 Tasks*/
    private Integer n1;

    /* Number of Class 2 Tasks*/
    private Integer n2;

    /* Number of Class 1 Tasks completed*/
    private Integer classOneCompletion;

    /* Number of Class 2 Tasks completed*/
    private Integer classTwoCompletion;

    /* Distributions Library*/
    private Distributions distributions;

    /* List of Task assigned */
    private List<AbstractTask> taskList;

    /* Event queue */
    private EventQueue eventQueue;

    /* Percentage of Task Class Two */
    private double percentage2Preemption;

    /* Total number of Task Class Two preempted*/
    private int totalClassTwoPreemption;

    /* Total number of Task Class Two assigned to Cloudlet*/
    private int totalClassTwoAssigned;



    public Cloudlet() {
        this.n1 = 0;
        this.n2 = 0;
        this.classOneCompletion = 0;
        this.classTwoCompletion = 0;
        this.distributions = Distributions.getInstance();
        this.taskList = new ArrayList<>();
        this.eventQueue = EventQueue.getInstance();
        this.percentage2Preemption = 0.0;

    }

    public void assignServer(AbstractTask task){
        if (task instanceof TaskClassOne){
            task.setServiceTime(distributions.exponential(AppConfiguration.CLOUDLET_M1,4));
        }
        else{
            task.setServiceTime(distributions.exponential(AppConfiguration.CLOUDLET_M2,5));
        }
        this.incrementPopulation(task);
        taskList.add(task);
        AbstractEvent toPush = new CompletionEvent(task);
        eventQueue.addEvent(toPush);
    }

    public AbstractTask stopTask(double swappedTime) {
        TaskClassTwo toStop = null;
        toStop = (TaskClassTwo) findTaskWithMaxCompletionTime();

        if (toStop!=null) {
            taskList.remove(toStop);

            eventQueue.dropElement(new CompletionEvent(toStop));

            toStop.setSwapped(true);
            this.n2--;

            this.totalClassTwoPreemption++;
            updatePercentage2Preemption();

            toStop.setSwapTime(swappedTime);

        }

        return toStop;
    }

    protected AbstractTask findTaskWithMaxCompletionTime() {
        double maxTime = 0.0;
        AbstractTask toStop = null;

        for (AbstractTask task : taskList) {
            if (task instanceof TaskClassTwo){
                if (maxTime<= task.getCompletionTime()) {
                    maxTime = task.getCompletionTime();
                    toStop = task;
                }
            }
        }
        return toStop;
    }

    public void handleCompletion(AbstractTask task) {
        taskList.remove(task);
        if (task instanceof TaskClassOne){
            this.n1--;
            this.classOneCompletion++;
        }
        else{
            this.n2--;
            this.classTwoCompletion++;
        }
    }

    private void incrementPopulation(AbstractTask task){
        if (task instanceof TaskClassOne) {
            this.n1++;
        }
        else {
            this.n2++;
            this.totalClassTwoAssigned++;
        }
    }


    private void updatePercentage2Preemption() {
        this.percentage2Preemption = (double) this.totalClassTwoPreemption / (double) this.totalClassTwoAssigned;
    }


    /**
     * Getter and Setter
     */
    public Integer getN1() {
        return n1;
    }

    public double getPercentage2Preemption() {
        return percentage2Preemption;
    }

    public Integer getTotalClassTwoPreemption() {
        return totalClassTwoPreemption;
    }

    public Integer getTotalClassTwoAssigned() {
        return totalClassTwoAssigned;
    }

    public Integer getN2() {
        return n2;
    }

    public Integer getClassOneCompletion() {
        return classOneCompletion;
    }

    public Integer getClassTwoCompletion() {
        return classTwoCompletion;
    }

}
