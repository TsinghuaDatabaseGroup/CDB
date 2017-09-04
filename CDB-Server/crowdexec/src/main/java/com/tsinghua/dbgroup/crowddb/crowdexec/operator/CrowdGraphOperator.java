package com.tsinghua.dbgroup.crowddb.crowdexec.operator;

import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;
import com.tsinghua.dbgroup.crowddb.crowdexec.gmodel.Graph;
import com.tsinghua.dbgroup.crowddb.crowdexec.gmodel.Record;
import com.tsinghua.dbgroup.crowddb.crowdexec.gmodel.Table;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C) 2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 * <p>
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 * <p>
 * Author   : XuepingWeng
 * Created  : 5/31/17 5:09 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */
public class CrowdGraphOperator extends BaseOperator implements IOperator{

    private static Logger LOG = LoggerFactory.getLogger(CrowdEQOperator.class);

    private static long INTERVAL = Integer.valueOf(GlobalConfigs.GlobalConfigs.getProperty("POLL_INTERVAL")) * 1000;

    private boolean finished = false;
    public String[] select_in_sql, selections, sql_join,joins_in_sql,sql_selection,froms_temp,joins_temp,from_in_sql,froms,project;
    public int select_len,join_len;
    public String query;
    public ArrayList<ArrayList<String>> result=new ArrayList<ArrayList<String>>();
    public CrowdGraphOperator(SqlTreeNode sqlTreeNode, String dbName) {
        super(sqlTreeNode);
        query=sqlTreeNode.getQuery();
        query=query.replace(";","");
        //System.out.print(query);
        //query="select Researcher.Name,Papers.Title from University, Researcher, Papers, Citation " +
                //"where University.Name CROWD_EQ Researcher.Affiliation and Researcher.Name crowd_eq Papers.Author and" +
                //" Papers.Title crowd_eq Citation.Title";
        //query=query.toLowerCase();
        select_len=0;
        join_len=0;
        //project=new String[]{"Researcher.Name","Papers.Title"};
        joins_in_sql=new String[10];
        //joins_in_sql= new String[]{"University.Name=Researcher.Affiliation", "Researcher.Name=Papers.Author",
               // "Papers.Title=Citation.Title"};
        sql_selection=new String[10];
        //sql_selection= new String[]{"Papers.Conference=sigmod"};
        froms_temp=new String[20];
        joins_temp=new String[20];
        //from_in_sql=new String[]{"University","Researcher","Papers","Citation"};
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_EQUAL;
        setCrowd(true);
    }

    public void init()
    {
        String select_temp, from_temp, where_temp;
        int select_index, from_index, where_index;
        select_index=query.indexOf("select");
        from_index=query.indexOf("from");
        where_index=query.indexOf("where");
        select_temp=query.substring(select_index+"select".length(),from_index);
        from_temp=query.substring(from_index+"from".length(),where_index);
        where_temp=query.substring(where_index+"where".length(),query.length());
        //System.out.print(select_temp+"\n"+from_temp+"\n"+where_temp+"\n");
        String[] from_in_sql=from_temp.split(",");
        for(int i=0; i<from_in_sql.length;i++){
            from_in_sql[i]=from_in_sql[i].replace(" ","");
        }
        select_in_sql=select_temp.split(",");
        for(int i=0; i<select_in_sql.length;i++){
            select_in_sql[i]=select_in_sql[i].replace(" ","");
        }
        String[] joins_in_sql_temp=where_temp.split("and");
        for(int i=0;i<joins_in_sql_temp.length;i++){
            String str=joins_in_sql_temp[i];
            str=str.replace(" ","");
            str=str.replace("crowd_eq","=");
            str=str.replace("CROWD_EQ","=");
            int count=0;
            for(int j=0; j<str.length();j++){
                if(str.charAt(j)=='.'){
                    count++;
                }
            }
            if(count==2){
                joins_in_sql[join_len]=str;
                join_len++;
            }
            else {
                sql_selection[select_len]=str;
                select_len++;
            }
        }
        for(int i=0;i<from_in_sql.length;i++) {
            froms_temp[i] = from_in_sql[i];
        }
        for(int i=0;i<join_len;i++){
            joins_temp[i] = joins_in_sql[i];
        }
        int j,index0,index1;
        for( j=0;j<select_len;j++) //transfer the selections to joins
        {
            String s="s"+(char)(j+'0');
            froms_temp[from_in_sql.length+j]=s;  //new tables s0,s1....

            index0=sql_selection[j].indexOf('.');
            index1=sql_selection[j].indexOf('=');
            String str_attr=sql_selection[j].substring(index0+1,index1);
            String str_table=sql_selection[j].substring(0,index0);
            String new_join=s+"."+str_attr+"="+str_table+"."+str_attr;

            joins_temp[join_len+j]=new_join;  //new joins  s0.attr=table.attr
        }
         froms=new String[from_in_sql.length+j];
         sql_join=new String[join_len+j]; //we use froms and sql_joins at last
        selections= new String[select_len];
        for(int i=0;i<froms.length;i++) {
            froms[i] = froms_temp[i];
        }
        for(int i=0;i<sql_join.length;i++) {
            sql_join[i] = joins_temp[i];
        }
        for(int i=0;i<select_len;i++) {
            selections[i] = sql_selection[i];
        }
        for(int i=0;i<froms.length;i++) {
            //System.out.print(froms[i] + "\n");
        }
        for(int i=0;i<sql_join.length;i++) {
            //System.out.print(sql_join[i] + "\n");
        }
    }

    @Override
    public boolean process() {

        //System.out.println(super.sqlTreeNode.getQuery());

        init();
        Table table=new Table(froms,sql_join,selections);
        table.read_tables();
        table.get_joins_infomation();
        Graph graph=new Graph(sql_join.length,table);
        graph.init();
        //graph.cdb();
        int i=0,order,ta,tb;
        String Aa,Ab;
        while(true){
            i++;
            ArrayList<Record> record=new ArrayList<Record>();
            if(graph.selection(record)==false) break;
            //BaseOperator operator=new BaseOperator();

            List<JudgementSchema> tasks = new ArrayList<JudgementSchema>();
            //
            ArrayList<Record> record_new=new ArrayList<Record>();
            // step1: 从数据库中取出数据拼装成List<JudgementSchema>
            // step2: 调用createAndUploadTask(schemas) 发布任务
            record_new=graph.parallel(record);
            for(i=0;i<record_new.size();i++)
            {
                order=record_new.get(i).join_order;
                ta=table.join_NUM[order][0];
                tb=table.join_NUM[order][1];
                Aa=table.join_attr[order][0];
                Ab=table.join_attr[order][1];
                String left=table.getValue(table.tables[ta],Aa,record_new.get(i).a);
                String right=table.getValue(table.tables[tb],Ab,record_new.get(i).b);
                //System.out.print(table.getValue(table.tables[ta],Aa,record_new.get(i).a));
                //System.out.print(table.getValue(table.tables[tb],Ab,record_new.get(i).b));
               // System.out.print("ccl: "+order+" "+record_new.get(i).a+" "+record_new.get(i).b+"\n");
                String code=String.valueOf(order)+","+String.valueOf(record.get(i).a)+","+String.valueOf(record.get(i).b);
                //System.out.print("code: "+code);
                JudgementSchema schema = new JudgementSchema(code,left,right);
                tasks.add(schema);
            }
            createAndUploadTask(tasks);

            long expired = System.currentTimeMillis() + INTERVAL;
            boolean running = true;

            while(running) {
                long diff = System.currentTimeMillis() - expired;
                if (diff < 0 ) continue;

                running = !super.hasTaskFinished();
                expired = System.currentTimeMillis() + INTERVAL;
            }

            super.getResult();
            HashMap<String, ? extends BaseResult> res = new HashMap<String, BaseResult>();
            res = getTask().getResults();
            for(String key:res.keySet()){
                 String [] arr = key.split(",");
                 BaseResult res_temp = res.get(key);
                 int ans = ((JudgementResult) res_temp).getAnswer();
                graph.Answer(Integer.valueOf(arr[0]).intValue(),Integer.valueOf(arr[1]).intValue(),Integer.valueOf(arr[2]).intValue(),ans);
            }
            super.setStatus(OperatorStatus.RUNNING);
            // step3: 调用super.hasTaskFinished 检查当前轮次任务是否完成? 完成则进入step4，否则继续循环
            // step4: 调用super.getResult() 获取结果
            // step5: 调用hashmap = getTask().getResults() 取回结果
            /*for(i=0;i<record_new.size();i++)
            {
                order=record_new.get(i).join_order;
                int ans=graph.crowd(order,record_new.get(i).a,record_new.get(i).b);
                graph.Answer(order,record_new.get(i).a,record_new.get(i).b,ans);
            }*/
            //System.out.print(i+"\n");
        }
        //System.out.print("Over: "+Crowd_num);
        graph.test_connect();
        graph.get_result();
        result=graph.projection(select_in_sql);
        setFinished(true);
        // step6: 调用super.setStatus(OperatorStatus.RUNNING) 重置operator状态。如果还需要迭代轮次，则返回step1，否则进入step7
        // step7: setFinished(true) 整体设置operator完成
        return true;
    }

    @Override
    public boolean hasTaskFinished() {
        return this.isFinished();
    }

    public String finish() {
        String newTableName = TableManager.TMP_DATABASE+"."+TableManager.generateTableName();
        List<String> tuples = new ArrayList<>();
        for(int i=0;i<result.size();i++)
        {
            for(int j=0;j<result.get(i).size();j++)
            {
                //System.out.print(result.get(i).get(j)+" ");
            }
            //System.out.print("\n");
        }
        for(int i=0;i<select_in_sql.length;i++)
        {
            tuples.add("attr"+String.valueOf(i)+" varchar(255)");
        }
        String values = String.join(",", tuples);
        String createtable=String.format("CREATE TABLE %s ( %s );",newTableName,values);
        execSQL(createtable);

        tuples.clear();
        for(int i=0;i<result.size();i++)
        {
            tuples.clear();
            for(int j=0;j<result.get(i).size();j++)
            {
                tuples.add("\""+result.get(i).get(j)+"\"");
            }
            String insert_data = String.join(",", tuples);
            String insertsql=String.format("INSERT INTO %s VALUES  ( %s );",newTableName,insert_data);
            execSQL(insertsql);
        }
        this.sqlTreeNode.setTableName(newTableName);
        //TODO: 将最终结果存入表中

        //return null;
         return newTableName;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
