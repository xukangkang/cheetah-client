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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((onlyTag == null) ? 0 : onlyTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThreadPark other = (ThreadPark) obj;
		if (onlyTag == null) {
			if (other.onlyTag != null)
				return false;
		} else if (!onlyTag.equals(other.onlyTag))
			return false;
		return true;
	}

}
