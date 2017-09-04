package com.tsinghua.dbgroup.crowddb.crowdexec.gmodel;

public class Main {
    // public static void main(String[] args) {
    //     /**
    //     String str=System.getProperty("user.dir");
    //     System.out.print(str);
    //    File file = new File(str,"hhh.txt");
    //     try {
    //         file.createNewFile();
    //         FileWriter fileWritter = new FileWriter(file.getName(),true);
    //         fileWritter.write("jdhakjdh");
    //         fileWritter.flush();
    //         //BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    //         //bufferWritter.write("ejwdkj\nkjd");
    //         //bufferWritter.close();
    //     } catch (IOException e) {
    //
    //     }
    //      */
    //     /**ArrayList<ArrayList<Integer>> a=new ArrayList<ArrayList<Integer>>();
    //     ArrayList<ArrayList<Integer>> b=new ArrayList<ArrayList<Integer>>();
    //     ArrayList<Integer> temp=new ArrayList<Integer>();
    //     temp.add(9);temp.add(2);a.add(temp);
    //     ArrayList<Integer> temp1=new ArrayList<Integer>();
    //     temp1.add(a.get(0).get(0));
    //     b.add(temp1);
    //     a.get(0).set(0,10);
    //     System.out.print(b.get(0).get(0));*/
    //     //String[] sql={"country.school=prof.school","prof.name=paper.name","paper.title=cite.title"};
    //     //String[] froms={"country","prof","paper","cite"};
    //     String[] joins_in_sql={"University.Name=Researcher.Affiliation","Researcher.Name=Papers.Author","Papers.Title=Citation.Title"};
    //     String[] sql_selection={"Papers.Conference=sigmod"};
    //     String[] froms_temp=new String[10];
    //     String[] joins_temp=new String[10];
    //     String[] from_in_sql={"University","Researcher","Papers","Citation"};
    //
    //
    //     for(int i=0;i<from_in_sql.length;i++) {
    //         froms_temp[i] = from_in_sql[i];
    //     }
    //     for(int i=0;i<joins_in_sql.length;i++){
    //         joins_temp[i] = joins_in_sql[i];
    //     }
    //     int j,index0,index1;
    //     for( j=0;j<sql_selection.length;j++)
    //     {
    //         String s="s"+(char)(j+'0');
    //         froms_temp[from_in_sql.length+j]=s;
    //
    //         index0=sql_selection[j].indexOf('.');
    //         index1=sql_selection[j].indexOf('=');
    //         String str_attr=sql_selection[j].substring(index0+1,index1);
    //         String str_table=sql_selection[j].substring(0,index0);
    //         String new_join=s+"."+str_attr+"="+str_table+"."+str_attr;
    //
    //         joins_temp[joins_in_sql.length+j]=new_join;
    //     }
    //     String[] froms=new String[from_in_sql.length+j];
    //     String[] sql_join=new String[joins_in_sql.length+j];
    //     for(int i=0;i<froms.length;i++) {
    //         froms[i] = froms_temp[i];
    //     }
    //     for(int i=0;i<sql_join.length;i++) {
    //         sql_join[i] = joins_temp[i];
    //     }
    //     for(int i=0;i<froms.length;i++) {
    //         System.out.print(froms[i] + "\n");
    //     }
    //     for(int i=0;i<sql_join.length;i++) {
    //         System.out.print(sql_join[i] + "\n");
    //     }
    //
    //     Table table=new Table(froms,sql_join,sql_selection);
    //     table.read_tables();
    //     table.get_joins_infomation();
    //     Graph graph=new Graph(sql_join.length,table);
    //     graph.init();
    //     graph.cdb();
    //     graph.test_connect();
    //     graph.get_result();
    //     //table.test_surround_left_num();
    // }

}
