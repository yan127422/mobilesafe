package com.roger.mobilesafe.domain;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

/**
 * 进程信息
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private String packName;
    private long memSize;//占用内存大小
    private boolean isSystem;//是否是系统进程
    private boolean checked;//是否选中

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getFormatMemSize(Context context){
        return Formatter.formatFileSize(context,memSize);
    }
    @Override
    public String toString() {
        return "TaskInfo{" +
                "name='" + name + '\'' +
                ", memSize=" + memSize +
                ", isSystem=" + isSystem +
                ", packName='" + packName + '\'' +
                '}';
    }
}
