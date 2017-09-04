package com.tsinghua.dbgroup.crowddb.crowdexec.gmodel;
import com.tsinghua.dbgroup.crowddb.crowdexec.gmodel.Record;
import java.util.*;
/**
 * Created by apple on 16/10/14.
 */
public class Graph {
    static int MAX=5000;
    public boolean flag=false;
    public int Crowd_num;
    public int Join_num;
    public boolean if_edge_visited[][][];
    public boolean connect[][][];
    public float sim_edge[][][];
    public boolean asked_edge[][][];
    public int[][] join_NUM;   //join_NUM[0][0/1] correspond to indexes of join 0's  tables besides '='
    public String[][] join_attr; //join_attr[0][0/1] correspond to strings of join 0's  attrs besides '='
    public Table table;
    public int table_begin;
    Map<String,Map<String,ArrayList<String>> > all_maps=new HashMap<String,Map<String,ArrayList<String>> >();
    public ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> surround_left_num=new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
    //surround_left_num[table i][record j][surround table k][indexes of table k 's records]
    ArrayList<ArrayList<Integer>> surround=new ArrayList<ArrayList<Integer>>();   //table对应的邻居table编号
    public List<List<Result>> result_list=new ArrayList<List<Result>>();
    public Graph(int NUM, Table table) {
        table_begin=0;
        Crowd_num=0;
        Join_num = NUM;
        sim_edge=new float[Join_num][MAX][MAX];
        asked_edge=new boolean[Join_num][MAX][MAX];
        if_edge_visited=new boolean[Join_num][MAX][MAX];
        connect=new boolean[Join_num][MAX][MAX];
        this.table=table;
        this.surround_left_num=table.surround_left_num;
        this.surround=table.get_surround();
        this.join_NUM=table.join_NUM;
        this.join_attr=table.join_attr;
        this.all_maps=table.all_maps;
    }
    private void remove(ArrayList<Integer> l, int tar)
    {
        l.remove(l.indexOf(tar));
    }
    public int get_join_order(int ta,int tb,int[] table_order)
    {
        int i,temp;
        for(i=0;i<Join_num;i++) {
            if (ta == join_NUM[i][0] && tb == join_NUM[i][1]){

                return i;
            }
            if(tb == join_NUM[i][0] && ta == join_NUM[i][1]){
                temp=table_order[0];
                table_order[0]=table_order[1];
                table_order[1]=temp;
                return i;
            }
        }
        return 0;
    }
    public int surround_index(int surround_join_index, int table_index)  //table_index是surround_join_index的第几个邻居,都是table编号
    {
        int i;
        for(i=0;i<surround.get(surround_join_index).size();i++){
            if(table_index==surround.get(surround_join_index).get(i)){
                return i;
            }
        }
        return 0;
    }
    private void get_attr_num(String join, String[] attr_str, String[] join_str)
    {
        int index_equal,index_dot1,index_dot2;
        index_equal=join.indexOf('=');
        index_dot1=join.indexOf('.');
        index_dot2=join.indexOf('.',index_equal);
        attr_str[0]=join.substring(index_dot1+1,index_equal);
        attr_str[1]=join.substring(index_dot2+1,join.length());
        join_str[0]=join.substring(0,index_dot1);
        join_str[1]=join.substring(index_equal+1,index_dot2);
    }
    private void Compute_similarity(ArrayList<String> list1, ArrayList<String> list2, int order)
    {
        int i,j;
        int t0,t1;
        String s1,s2;
        float score=0;
        t0=join_NUM[order][0];
        t1=join_NUM[order][1];
        //System.out.print(t0+" "+t1+"\n");
        Similarity_function fun=new Similarity_function();
        //System.out.print(list1.size()+" "+list2.size()+"\n");
        for(i=0;i<list1.size();i++) {
            for (j = 0; j < list2.size(); j++) {
                s1 = list1.get(i);
                s2 = list2.get(j);

                score = fun.bigram(s1, s2);
                sim_edge[order][i][j] = score;
                int index0 = surround_index(t0, t1);
                int index1 = surround_index(t1, t0);
                //System.out.print(index0+" "+index1+"\n");
                if(score>0.3) {
                    surround_left_num.get(t0).get(i).get(index0).add(j);
                    surround_left_num.get(t1).get(j).get(index1).add(i);
                    //System.out.print(t0+" "+j+"  | "+t1+" "+i+s1+" vs "+s2+"\n");
                    //surround_left_num.get(t0).get(j);
                    //surround_left_num.get(t1).get(i);
                    //java_integer_add(t0, j, index0, 1);
                    //java_integer_add(t1, i, index1, 1);
                }
                //System.out.println(s1+" "+s2+" "+score);
                //System.out.println(i+" "+j+" "+score);
            }
        }
    }
    public void init()
    {
        int i,j;
        String[] attr_str,join_str;
        String [] joins =table.get_joins_();
        Map<String,Map<String,ArrayList<String>> > all_maps=table.get_all_maps();
        ArrayList<String> list1;
        ArrayList<String> list2;
        int[][] joins_num=table.get_joins_num();
        for(i=0;i<joins.length;i++)
        {

            attr_str=new String[2];
            join_str=new String[2];
            get_attr_num(joins[i],attr_str,join_str);
            list1=all_maps.get(join_str[0]).get(attr_str[0]);
            list2=all_maps.get(join_str[1]).get(attr_str[1]);
            Compute_similarity(list1,list2,i);
            //list1.clear();
            //list2.clear();
            //System.out.print(join_str[0]+" "+attr_str[0]+" "+join_str[1]+" "+attr_str[1]);
        }
    }

    private float Expectation(int join_order, int ra, int rb)
    {
        boolean if_edge_tablea=false,if_edge_tableb=false;
        float exp=0,pro_a=1,pro_b=1;
        int i,j,ta,tb,table,ra_num=0,rb_num=0,index,size_a=0,size_b=0;
        ta=join_NUM[join_order][0];
        tb=join_NUM[join_order][1];
        for(i=0;i<surround.get(ta).size();i++){
            table=surround.get(ta).get(i);
            if(table==-1){
                if_edge_tablea=true;
                continue;
            }
            index=surround_index(ta,table);
            ra_num+=surround_left_num.get(ta).get(ra).get(index).size();
        }
        index=surround_index(ta,tb);
        size_a=surround_left_num.get(ta).get(ra).get(index).size();
       // System.out.print("size_a: "+size_a);
        for(i=0;i<size_a;i++){
            pro_a=pro_a*(1-sim_edge[join_order][ra][surround_left_num.get(ta).get(ra).get(index).get(i)]);
        }



        for(i=0;i<surround.get(tb).size();i++){
            table=surround.get(tb).get(i);
            if(table==-1){
                if_edge_tableb=true;
                continue;
            }
            index=surround_index(tb,table);
            rb_num+=surround_left_num.get(tb).get(rb).get(index).size();
        }
        index=surround_index(tb,ta);

        size_b=surround_left_num.get(tb).get(rb).get(index).size();
       // System.out.print("size_b: "+size_b+"\n");
        for(i=0;i<size_b;i++){
            pro_b=pro_b*(1-sim_edge[join_order][rb][surround_left_num.get(tb).get(rb).get(index).get(i)]);
        }
        if(if_edge_tablea==true)ra_num+=rb_num;
        if(if_edge_tableb==true)rb_num+=ra_num;
        exp=pro_a*ra_num/size_a+pro_b*rb_num/size_b;
        //System.out.print(join_order+" "+ra+" "+rb+" "+exp+"\n");
        return exp;
    }
    private void recursion(int t_center, int r, int t_neighbour)
    {
        //System.out.print("%%%"+t_center+" "+r+" "+t_neighbour+"\n");
        int i,table,j,index,r_surround,index_surround,j_order;
        int[] table_order=new int[2];
        for(i=0;i<surround.get(t_center).size();i++){
            table=surround.get(t_center).get(i);
            if(table==-1)
                return;
            if(table!=t_neighbour){
                index=surround_index(t_center,table);
                for(j=0;j<surround_left_num.get(t_center).get(r).get(index).size();j++){
                    r_surround=surround_left_num.get(t_center).get(r).get(index).get(j);
                    index_surround=surround_index(table,t_center);
                    //surround_left_num.get(table).get(r_surround).get(index_surround).remove(r);
                    remove(surround_left_num.get(table).get(r_surround).get(index_surround),r);
                    table_order[0]=r;
                    table_order[1]=r_surround;
                    //System.out.print("Table_order"+" "+table_order[0]+" "+table_order[1]+"\n");
                    j_order=get_join_order(t_center,table,table_order);
                    asked_edge[j_order][table_order[0]][table_order[1]]=true;
                    //System.out.print("Recursion"+j_order+" "+table_order[0]+" "+table_order[1]+"\n");
                    connect[j_order][table_order[0]][table_order[1]]=false;
                    if( surround_left_num.get(table).get(r_surround).get(index_surround).size()==0)
                        recursion(table,r_surround,t_center);
                }
            }
        }
    }
    public void Answer(int join_order, int ra, int rb, int ans)
    {
        //System.out.print("Answer: "+join_order+" "+ra+" "+rb+" "+ans+"\n");
        int t0,t1,index0,index1;
        if (ans==1){
            //System.out.print("Answer: "+join_order+" "+ra+" "+rb+"\n");
            asked_edge[join_order][ra][rb]=true;
            connect[join_order][ra][rb]=true;
            return;
        }
        else {
            asked_edge[join_order][ra][rb]=true;
            connect[join_order][ra][rb]=false;
            t0=join_NUM[join_order][0];
            t1=join_NUM[join_order][1];
            index0=surround_index(t0,t1);
            //System.out.print(surround_left_num.get(t0).get(ra).get(index0).size());
            //surround_left_num.get(t0).get(ra).get(index0).remove(rb);
            remove(surround_left_num.get(t0).get(ra).get(index0),rb);
            if(surround_left_num.get(t0).get(ra).get(index0).size()==0)
                recursion(t0,ra,t1);
            index1=surround_index(t1,t0);
            //surround_left_num.get(t1).get(rb).get(index1).remove(ra);
            remove(surround_left_num.get(t1).get(rb).get(index1),ra);
            if(surround_left_num.get(t1).get(rb).get(index1).size()==0)
                recursion(t1,rb,t0);

        }
    }
    public int crowd(int join_order, int ra, int rb)
    {
        if(join_order==0&&ra==11&&rb==11)
            return 1;
        if(join_order==2&&ra==2&&rb==4)
            return 0;
        if(sim_edge[join_order][ra][rb]>0.5) {
            return 1;
        }
        if(sim_edge[join_order][ra][rb]<=0.5) {
            return 0;
        }
        return 0;
    }
    public boolean selection(ArrayList<Record> record)
    {
        int i,j,k;
        boolean ans, f=false;
        float exp,max=-1;
        int join_order=0,ra=0,rb=0;
        //ArrayList<Record> record=new ArrayList<Record>();
        for(i=0;i<Join_num;i++)
        {
            for(j=0;j<MAX;j++){
                for (k=0;k<MAX;k++){

                    if(sim_edge[i][j][k]>0.3&&asked_edge[i][j][k]==false){
                        f=true;
                        exp=Expectation(i,j,k);
                        Record temp=new Record();
                        temp.exp=exp;
                        temp.join_order=i;
                        temp.a=j;
                        temp.b=k;
                        record.add(temp);
                        /**if(max<=exp){
                            max=exp;
                            join_order=i;
                            ra=j;
                            rb=k;
                        }*/
                    }
                }
            }
        }
        if(f==false) {
            //System.out.print("%%%%%%%\n");
            return false;
        }
        Collections.sort(record, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                if(o1.exp<o2.exp)
                    return 1;
                else return -1;
            }
        });
        //System.out.print("parallel\n");
       // parallel(record);

        /**System.out.print(join_order+" "+ra+" "+rb+"\n");
        ans=crowd(join_order,ra,rb);
        Crowd_num++;
        Answer(join_order,ra,rb,ans);*/
        return true;
    }
    public void if_conncted(int table_id, int record_id, int aim_table, int aim_r,boolean[] visited_table)
    {
        int i,j,index,r,t,j_order;
        int[] table_order=new int[2];
        if(table_id==aim_table && record_id==aim_r)
        {
            flag=true;
            return;
        }
        else {
            for(i=0;i<surround.get(table_id).size();i++){
                t=surround.get(table_id).get(i);
                if(t==-1)continue;
                if(visited_table[t]==true)
                    continue;
                visited_table[t]=true;
                index=surround_index(table_id,t);
                for(j=0;j<surround_left_num.get(table_id).get(record_id).get(index).size();j++)
                {
                    r=surround_left_num.get(table_id).get(record_id).get(index).get(j);
                    table_order[0]=record_id;
                    table_order[1]=r;
                    j_order=get_join_order(table_id,t,table_order);
                    if(sim_edge[j_order][table_order[0]][table_order[1]]>0.3) {
                        if_conncted(t, r, aim_table, aim_r,visited_table);
                    }
                    //else return false;
                }
            }
        }
    }
    private boolean if_deduced(Record x, Record y)
    {
        int i;
        boolean[] visited_table=new boolean[table.tables.length];
        if(x.join_order==y.join_order)
            return false;
        else {
            visited_table[join_NUM[x.join_order][0]]=true;
            flag=false;
            if_conncted(join_NUM[x.join_order][0],x.a,join_NUM[y.join_order][0],y.a,visited_table);
            if(flag==true)
            {
                return true;
            }
            else{
                //System.out.print("no connect"+join_NUM[x.join_order][0]+" "+(x.a+1)+" "+join_NUM[y.join_order][0]+" "+(y.a+1)+"\n");
                return false;
            }
        }
    }
    public ArrayList<Record> parallel(ArrayList<Record> record_candidate)
    {
        int i,j;
        int order,ta,tb;
        String Aa,Ab;
        boolean f=false;
        boolean ans;
        ArrayList<Record> record=new ArrayList<Record>();
        record.add(record_candidate.get(0));
        for(i=1;i<record_candidate.size();i++)
        {
            f=false;
            for(j=0;j<record.size();j++){
                if(if_deduced(record_candidate.get(i),record.get(j))==true)
                {
                    f=true;
                    break;
                }
            }
            if(f==false)
                record.add(record_candidate.get(i));
        }
        //System.out.print(record.size()+" size \n");
        /**
        BaseOperator operator=new BaseOperator();
         List<JudgementSchema> tasks = new ArrayList<JudgementSchema>;

         */

        /**for(i=0;i<record.size();i++)
        {
            order=record.get(i).join_order;
            ta=join_NUM[order][0];
            tb=join_NUM[order][1];
            Aa=join_attr[order][0];
            Ab=join_attr[order][1];
            String left=table.getValue(table.tables[ta],Aa,record.get(i).a);
            String right=table.getValue(table.tables[tb],Ab,record.get(i).b);
            System.out.print(table.getValue(table.tables[ta],Aa,record.get(i).a));
            System.out.print(table.getValue(table.tables[tb],Ab,record.get(i).b));
            System.out.print("ccl: "+order+" "+record.get(i).a+" "+record.get(i).b+"\n");
            String code=String.valueOf(order)+String.valueOf(record.get(i).a)+String.valueOf(record.get(i).b);
            System.out.print("code: "+code);

             //JudgementSchema schema = new JudgementSchema(code,left,right);
             //tasks.add(schema);

            //ans=crowd(order,record.get(i).a,record.get(i).b);
            //Crowd_num++;
           // if(asked_edge[record.get(i).join_order][record.get(i).a][record.get(i).b]==false)
            //Answer(order,record.get(i).a,record.get(i).b,ans);
        }*/
        /**
         operator.createAndUploadTask(tasks);
         */
        return record;
    }
    public void test_connect()
    {
        int i,j,k;
        for(i=0;i<Join_num;i++){
            for(j=0;j<MAX;j++){
                for(k=0;k<MAX;k++){
                    if(connect[i][j][k]==true)
                    System.out.print(i+" "+j+" "+k+"\n");
                }
            }
        }
    }
    private void search(int table_id, int record_id,Result[] temp_result,int len,boolean[] visited_table)
    {
        int i,j,index,r,t,j_order;
        int[] table_order=new int[2];
        /*if(len==table.tables.length)
        {
            List<Result> value=new ArrayList<Result>();
            value=Arrays.asList(temp_result);
            result_list.add(value);
            System.out.print("Add One!\n");
            return;
        }*/
        Result temp=new Result();
        temp.table_order=table_id;
        temp.record_order=record_id;
        temp_result[len]=temp;
        //System.out.print("id: "+table_id+" "+record_id+" result size: "+len+" #table: "+table.tables.length+"\n");

        for(i=0;i<surround.get(table_id).size();i++){
            t=surround.get(table_id).get(i);
            //System.out.print(t+"\n");
            if(t==-1){
                //System.out.print(table_id+" Boarder! "+"\n");
                if(table_id!=table_begin){
                    List<Result> value=new ArrayList<Result>();
                    for(int l=0;l<len+1;l++) {
                        value.add(temp_result[l]);
                    }
                    result_list.add(value);
                    //value.clear();
                    //System.out.print("Add One!\n");
                    for(int l=0;l<value.size();l++)
                    {
                        //System.out.print(value.get(l).record_order+" ");
                    }
                    //System.out.print("\n");
                    //break;
                }
                return;
            }
            if(visited_table[t]==true)
                continue;
            visited_table[t]=true;
            index=surround_index(table_id,t);
            //System.out.print("size: "+surround_left_num.get(table_id).get(record_id).get(index).size());
            for(j=0;j<surround_left_num.get(table_id).get(record_id).get(index).size();j++)
            {
                r=surround_left_num.get(table_id).get(record_id).get(index).get(j);
                //System.out.print(r+" ");
                table_order[0]=record_id;
                table_order[1]=r;
                j_order=get_join_order(table_id,t,table_order);
                if(connect[j_order][table_order[0]][table_order[1]]==true) {
                    search(t, r, temp_result,len+1, visited_table);
                }
            }
            visited_table[t]=false;
        }
    }
    public void get_result()
    {
        int i,j;
        boolean[] visited_table=new boolean[table.tables.length];
        for(i=0;i<surround_left_num.get(table_begin).size();i++)
        {
            //System.out.print(i);
            for(j=0;j<table.tables.length;j++)
                visited_table[j]=false;
            visited_table[0]=true;
            Result[] temp_result=new Result[table.tables.length];
            search(table_begin,i,temp_result,0,visited_table);
        }

    }



    public ArrayList<ArrayList<String>> projection(String[] project)
    {
        int i,j,k;
        String str,str_tname,str_attr,record;
        int index,tindex,index_record;
        ArrayList<ArrayList<String>> result=new ArrayList<ArrayList<String>>();
        //String[] projects={"Researcher.Name","Papers.Title"};
        for(i=0;i<result_list.size();i++){
            ArrayList<String> result_record=new ArrayList<>();
            for(j=0;j<project.length;j++){
                str=project[j];
                index=str.indexOf('.');
                str_tname=str.substring(0,index);
                str_attr=str.substring(index+1,str.length());
                tindex=table.get_table_index(str_tname);
                for(k=0;k<result_list.get(i).size();k++)
                {
                    //System.out.print(result_list.get(i).get(k).table_order+" ");
                    if(tindex==result_list.get(i).get(k).table_order){
                        index_record=result_list.get(i).get(k).record_order;
                        record=all_maps.get(str_tname).get(str_attr).get(index_record);
                        //System.out.print(record+" * ");
                        result_record.add(record);
                    }
                }

            }
            //System.out.print("\n");
            result.add(result_record);
        }
        return result;
    }
    /*public void cdb()
    {
        int i=0;
        while(true){
            i++;
            if(selection()==false) break;
            //System.out.print(i+"\n");
        }
        System.out.print("Over: "+Crowd_num);
    }*/
}