package com.tsinghua.dbgroup.crowddb.crowdexec.gmodel;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by apple on 16/10/14.
 */
public class Similarity_function {
    Similarity_function(){
    }
    public float jaccard(String str1, String str2)
    {
        Set <String> set1 = new HashSet<String>();
        Set <String> set2 = new HashSet<String>();
        Set <String> set_union = new HashSet<String>();
        Set <String> set_insection = new HashSet<String>();
        set_union.clear();
        set_insection.clear();
        String[] str1_split={};
        String[] str2_split={};
        str1_split=str1.split(" ");
        str2_split=str2.split(" ");
        for(String token:str1_split){
            set1.add(token);
        }
        for(String token:str2_split){
            set2.add(token);
        }
        set_union.addAll(set1);
        set_union.addAll(set2);
        set_insection.addAll(set1);
        set_insection.retainAll(set2);
        //System.out.println(set_insection.size());
        //System.out.println(set_union.size());
        return (float) set_insection.size()/set_union.size();
    }
    public float bigram(String str1, String str2)
    {
        int i;
        String str1_append="",str2_append="";
        for(i=0;i<str1.length()-1;i++)
            str1_append=str1_append+str1.substring(i,i+2)+" ";
        for(i=0;i<str2.length()-1;i++)
            str2_append=str2_append+str2.substring(i,i+2)+" ";
        //System.out.println(str1_append);
        //System.out.println(str2_append);
        return jaccard(str1_append,str2_append);
    }
}
