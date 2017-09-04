package com.tsinghua.dbgroup.crowddb.crowdexec.gmodel;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 16/10/11.
 */
public class Table {
    Map<String,Map<String,ArrayList<String>> > all_maps=new HashMap<String,Map<String,ArrayList<String>> >();
    //<table_name1,<attr_name1,value_list><attr_name2,value_list>...><table_name2...>
    public String[] tables;
    public String[] joins;
    public String[] selections;
    public int[][] join_NUM;
    public String[][] join_attr;
    public ArrayList<ArrayList<Integer>> surround=new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> surround_left_num=new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
    public int attr_num;

    public Table(String[] tables, String[] joins, String[] selections){
        this.tables =tables ;
        this.joins =joins;
        this.selections=selections;
       join_NUM=new int[joins.length+selections.length][2];
        join_attr=new String[joins.length+selections.length][2];
    }
    public Map<String,Map<String,ArrayList<String>> > get_all_maps()
    {
        return all_maps;
    }
    public ArrayList<ArrayList<Integer>> get_surround()
    {
        return surround;
    }
    public int[][] get_joins_num()
    {
        return join_NUM;
    }
    public String[] get_joins_()
    {
        return joins;
    }
    public int get_table_index(String str)
    {
        int i;
        for(i=0;i<tables.length;i++)
        {
            if(tables[i].equals(str))
                return i;
        }
        return -1;
    }
    private void get_joins_number(String where_join, int j[],String a[])
    {

        int i,index_equal,index_dot1,index_dot2;
        String ltable="",rtable="",lattr="",rattr="";
        index_equal=where_join.indexOf('=');
        index_dot1=where_join.indexOf('.');
        index_dot2=where_join.indexOf('.',index_equal);
        ltable=where_join.substring(0,index_dot1);
        rtable=where_join.substring(index_equal+1,index_dot2);
        lattr=where_join.substring(index_dot1+1,index_equal);
        rattr=where_join.substring(index_dot2+1,where_join.length());
        //System.out.print("Attr:"+lattr+","+rattr+"\n");
        //System.out.print(rattr);
        for(i=0;i<tables.length;i++)
        {
            if(tables[i].equals(ltable)) {
                j[0] = i;
                a[0] = lattr;
            }
            if(tables[i].equals(rtable)) {
                j[1] = i;
                a[1] = rattr;
            }
        }
    }
    private  void get_surround_joins() //get indexes of table i's adjacent tables
    {
        int i,j,left,right;

        for(i=0;i<tables.length;i++)
        {
            ArrayList<Integer> temp=new ArrayList<Integer>();
            surround.add(temp);
        }
        for(i=0;i<joins.length;i++)
        {
            left=join_NUM[i][0];
            right=join_NUM[i][1];
            surround.get(left).add(right);
            surround.get(right).add(left);
        }
        for(i=0;i<tables.length;i++)
        {
            if(surround.get(i).size()==1)
            {
                surround.get(i).add(-1);
            }
        }
        for(i=0;i<surround.size();i++)
        {
            //System.out.print(surround.get(i).size());
        }
    }
    public void init_surround_left_num()
    {
        int i,j;
        int table_index=0;
        for(i=0;i<tables.length;i++) {
            ArrayList<ArrayList<ArrayList<Integer>>> temp=new ArrayList<ArrayList<ArrayList<Integer>>>();
            surround_left_num.add(temp);
        }
        for(String key: all_maps.keySet())
        {
            int temp_map_size=0;
            Map<String,ArrayList<String>> temp_map=all_maps.get(key);
            for(String skey: temp_map.keySet())
            {
                temp_map_size=temp_map.get(skey).size();//# records
                break;
            }
            table_index=get_table_index(key);
            //System.out.print( table_index+"size:"+temp_map_size);
            for(i=0;i<temp_map_size;i++)
            {
                ArrayList<ArrayList<Integer>> temp_array=new ArrayList<ArrayList<Integer>>();
                for(j=0;j<surround.get(table_index).size();j++)
                {
                    ArrayList<Integer> temp_neighbour=new ArrayList<Integer>();
                    temp_array.add(temp_neighbour);
                }
                surround_left_num.get(table_index).add(temp_array);
            }

        }

    }
    public void test_surround_left_num()
    {
        int i,j,k,temp;
        for(i=0;i<tables.length;i++) {
            for(j=0;j<surround_left_num.get(i).size();j++){
                for(k=0;k<surround_left_num.get(i).get(j).size();k++){
                    if(surround.get(i).get(k)!=-1) {
                        //System.out.print(tables[surround.get(i).get(k)] + " ");
                        //System.out.print(surround_left_num.get(i).get(j).get(k).size() + " ");
                    }
                }
                //System.out.print("###\n");
            }
            //System.out.print(tables[i]+"*****\n");
        }
    }
    public void get_joins_infomation()
    {
        int i;
        int[] j=new int[2];
        String[] str=new String[2];
        for(i=0;i<joins.length;i++)
        {
            get_joins_number(joins[i], j, str); //get joins[i]'s neighbours' information
            //System.out.print("hhh\n");
            join_NUM[i][0]=j[0];
            join_NUM[i][1]=j[1];
            join_attr[i][0]=str[0];
            join_attr[i][1]=str[1];
            //System.out.print(j[0]+" "+j[1]+'\n');
        }
        get_surround_joins();
        init_surround_left_num();
        //test_surround_left_num();
    }
    public void test_allmaps()
    {
        // System.out.println("Test Map!!!");
        for(String key:all_maps.keySet()){
            // System.out.println("table: "+key);
            Map<String,ArrayList<String>> map=new HashMap<String,ArrayList<String>>();
            map=all_maps.get(key);
            for (String key2: map.keySet()){
                System.out.print(key2+" ");
                ArrayList<String> array=new ArrayList<>();
                array=map.get(key2);
                for(int i=0;i<array.size();i++)
                    System.out.print(array.get(i)+" ");
                System.out.print('\n');
            }
        }
    }
    public void read_tables()
    {
        for (int i=0;i<tables.length-selections.length;i++) {
            // System.out.println("read: "+tables[i]);
            read_each_table(tables[i]); //table name,some problems...
        }
        for (int j=0;j<selections.length;j++)
        {
            int index0,index1;
            String s="s"+(char)(j+'0');
            Map<String,ArrayList<String>> map=new HashMap<String,ArrayList<String>>();
            ArrayList<String> temp_array=new ArrayList<String>();
            String str=selections[j];
            index0=str.indexOf('.');
            index1=str.indexOf('=');
            String str_attr=str.substring(index0+1,index1);
            String str_value=str.substring(index1+1,str.length());
            temp_array.add(str_value);
            map.put(str_attr,temp_array);
            all_maps.put(s,map);
        }
        test_allmaps();
    }
    public String getValue(String tname, String Aname,int  index)
    {
        Map<String,Map<String,ArrayList<String>> > maps=get_all_maps();
        return maps.get(tname).get(Aname).get(index);
    }
    public void read_each_table(String table_name)
    {
        Map<String,ArrayList<String>> map=new HashMap<String,ArrayList<String>>();
        String s,v;
        String[] attr={};
        String[] record;
        int i=0,j;
        TableManager databse=new TableManager();
        List<String> columns=new ArrayList<String>();
        try {
            columns = databse.getTableColumns("crowddb_dblp."+table_name);
            //columns = databse.getTableColumns(table_name);
            for (i = 0; i < columns.size(); i++) {
                if(columns.get(i).equals("id"))
                    columns.remove(i);
            }
            for (i = 0; i < columns.size(); i++) {
                //System.out.println(columns.get(i));
                map.put(columns.get(i), new ArrayList<String>());
            }
            List<Map<String, Object>> Record_temp = new ArrayList<Map<String, Object>>();
            Record_temp=databse.extractColumns("crowddb_dblp."+table_name, columns);
            //System.out.print(Record_temp.size());
            //Record_temp=databse.extractColumns(table_name, columns);
            List<HashMap<String, String>> Record = new ArrayList<HashMap<String, String>>();
            for(i=0;i<Record_temp.size();i++){
                HashMap<String, String> map_temp=new HashMap<String, String>();
                for(String key: Record_temp.get(i).keySet()){
                    //System.out.print(key+" k ");
                    v=Record_temp.get(i).get(key).toString();
                    //System.out.print(v+" & ");
                    map_temp.put(key,v);
                }
                //System.out.print("\n");
                Record.add(map_temp);
            }

            //Record = databse.extractColumns(table_name, columns);

            for (i = 0; i < Record.size(); i++) {
                for(String key: Record.get(i).keySet()) {
                    String Value;
                    Value = Record.get(i).get(key);
                     map.get(key).add(Value);
                }
            }
            all_maps.put(table_name, map);
        }catch (Exception e){
            return ;
        }

        /*String str=System.getProperty("user.dir");
        File file = new File(str,table_name+".txt");

        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                s=in.nextLine();
                i++;
                if(i==1) {
                    attr=s.split(";");
                    attr_num=attr.length;
                    for (String attr_name:attr
                         ) {
                        map.put(attr_name,new ArrayList<String>());
                    }
                }
                else{
                    record=s.split(";");
                    for(j=0;j<attr_num;j++)
                    {
                        String key=attr[j];
                        map.get(key).add(record[j]);
                    }
                }
            }
        }catch (IOException e) {
        }*/



        /**System.out.println(map.size());
        all_maps.put(table_name,map);
       System.out.println(table_name);
        for (String key : map.keySet()) {
            System.out.print(key+" :");
            Iterator it1 = map.get(key).iterator();
            while(it1.hasNext()){
                System.out.print(it1.next()+" ");
            }
            System.out.println();
        }*/
    }

}
