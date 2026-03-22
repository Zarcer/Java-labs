package cyber;

import java.io.Serializable;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Bean first = new Bean();
        Bean second = new Bean();
        Bean third = new Bean();
        first.setLink(second);
        second.setLink(third);
        third.setLink(first);
        Thread th1 = new Thread(new SleepingThread());
        th1.start();
        Thread th2 = new Thread(new SleepingThread());
        th2.start();
        Thread th3 = new Thread(new SleepingThread());
        th3.start();
        if(args.length > 0 && args[0].equals("break")){
            Thread th4 = new Thread(new BreakingThread());
            th4.start();
        }
    }
}

final class Singleton {
    private static Singleton value;
    private Singleton() {};
    public static synchronized Singleton getValue() {
        if(value==null){
            value= new Singleton();
        }
        return value;
    }
}

class Bean implements Serializable {
    private byte[] name;
    private ArrayList<Integer> justList;
    private int justNumber;
    private Bean link;
    public Bean(){
        name = new byte[5];
        justList = new ArrayList<>();
        justNumber=0;
        link=this;
    }

    public ArrayList<Integer> getJustList() {
        return justList;
    }

    public void setJustList(ArrayList<Integer> justList) {
        this.justList = justList;
    }

    public int getJustNumber() {
        return justNumber;
    }

    public void setJustNumber(int justNumber) {
        this.justNumber = justNumber;
    }

    public Bean getLink() {
        return link;
    }

    public void setLink(Bean link) {
        this.link = link;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }
}

class SleepingThread implements Runnable {
    public void run() {
        Singleton mySingleTone = Singleton.getValue();
        Bean myBean = new Bean();
        while(true){
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println("sleep interrupted");
            }
        }
    }
}

class BreakingThread implements Runnable {
    public void run() {
        ArrayList<byte[]> test = new ArrayList<>();
        while(true){
            test.add(new byte[1024*1024]);
        }
    }
}