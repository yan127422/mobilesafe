package com.roger.mobilesafe.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.roger.mobilesafe.domain.TaskInfo;
import com.roger.mobilesafe.engine.TaskInfoEngine;

import java.util.List;

/**
 * 进程业务测试
 */
public class TaskInfoEngineTest extends AndroidTestCase{

    private static final String TAG = "TaskInfoEngineTest";

    public void testGetTaskInfos(){
        List<TaskInfo>infos =  TaskInfoEngine.getTaskInfos(getContext());
        Log.i(TAG,"size:"+infos.size());
        for(TaskInfo info:infos){
            Log.i(TAG,info.toString());
        }
    }
}
