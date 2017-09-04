/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 4:25 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */


package com.tsinghua.dbgroup.crowddb.crowdplat.core;

import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;

import java.util.HashMap;


public class TaskConfigure {

    /*
    * Request Urls
    * */
    private static final String SERVER_HOST = GlobalConfigs.GlobalConfigs.getProperty("ASSIGNMENT_SERVER_URL");
    public static final String UPLOAD_TASK_URL = SERVER_HOST + "/upload/";
    public static final String CHECK_STATUS_URL = SERVER_HOST + "/check/";
    public static final String DOWNLOAD_RESULTS_URL = SERVER_HOST + "/results/";

    /*
    * Task Options
    * */
    public static HashMap<String, String> OPTIONS;

    /*
    * Task Templates
    * */
    public static final String JUDGEMENT_TEMPLATE = "Please judge whether %s is %s %s ？";
    public static final String COLLECT_TEMPLATE = "Please fill those blanks with appropriate tags for attribute %s";
    public static final String IN_TEMPLATE = "Please choose options for this question";
    public static final String COLLECT_IN_TEMPLATE = "Please choose best tags in candidates for attribute %s";
    public static final String FILL_TEMPLATE = "Please fill the %s for entity %s";
    public static final String SINGLE_LABEL_TEMPLATE = "Please tag single label for %s in this picture";
    public static final String MULT_LABEL_TEMPLATE = "Please tag multiple labels for %s in this picture";
    public static HashMap<TaskType, String> COMPARISION_WORDS;

    static {
        packetOptions();
        initComparsionWords();
    }

    private static void packetOptions() {
        OPTIONS = new HashMap<>();
        OPTIONS.put("task_id",          "");
        OPTIONS.put("task_category",    "");
        OPTIONS.put("task_type",        "");
        OPTIONS.put("title",            "Please compare those two entities cksjkkjsdfs");
        OPTIONS.put("description",      "Please compare those two entities");
        OPTIONS.put("keywords",         "Comparison");
        OPTIONS.put("platform",        (String) GlobalConfigs.GlobalConfigs.getOrDefault("DEFAULT_PLATFORM", "AMT"));
        OPTIONS.put("reward",           (String) GlobalConfigs.GlobalConfigs.getOrDefault("MAX_ASSIGNMENT", "REWARD"));
        OPTIONS.put("duration",         "120");
        OPTIONS.put("approval_delay",   "15");
        OPTIONS.put("max_assignments",  (String) GlobalConfigs.GlobalConfigs.getOrDefault("MAX_ASSIGNMENT", "5"));
        OPTIONS.put("lifetime",         "15");
        OPTIONS.put("q_type",           "FREE");
    }

    private static void initComparsionWords() {
        COMPARISION_WORDS = new HashMap<>();
        COMPARISION_WORDS.put(TaskType.CROWD_EQUAL, "equal to");
        COMPARISION_WORDS.put(TaskType.CROWD_NOT_EQUAL, "not equal to");
        COMPARISION_WORDS.put(TaskType.CROWD_GT, "greater than");
        COMPARISION_WORDS.put(TaskType.CROWD_GE, "greater than or equal to");
        COMPARISION_WORDS.put(TaskType.CROWD_LT, "smaller than");
        COMPARISION_WORDS.put(TaskType.CROWD_LE, "smaller than or equal to");
    }
 }
