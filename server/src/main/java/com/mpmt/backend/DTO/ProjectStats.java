package com.mpmt.backend.DTO;

public class ProjectStats {
    private int totalTasks;
    private long todoCount;
    private long inProgressCount;
    private long doneCount;
    private int progress;

    // Getters et setters
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public long getTodoCount() { return todoCount; }
    public void setTodoCount(long todoCount) { this.todoCount = todoCount; }

    public long getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(long inProgressCount) { this.inProgressCount = inProgressCount; }

    public long getDoneCount() { return doneCount; }
    public void setDoneCount(long doneCount) { this.doneCount = doneCount; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}