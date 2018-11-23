package org.kk.cheetah.client.assist;

public class ThreadPark {

    private Thread thread;
    private String onlyTag;

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getOnlyTag() {
        return onlyTag;
    }

    public void setOnlyTag(String onlyTag) {
        this.onlyTag = onlyTag;
    }

    @Override
    public String toString() {
        return "ThreadPark [thread=" + thread + ", onlyTag=" + onlyTag + "]";
    }

}
