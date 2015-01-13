/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.yimei.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SetUtils {
    
    public static <T> void randFill(List<T> set, List<T> universal, int size) {
    	List<T> diff = new ArrayList<T>(universal);
    	diff.removeAll(set);
    	
    	int n = set.size();
    	
    	Collections.shuffle(diff);
    	
//    	System.out.println(set.size() + ", " + size);
    	
    	for (int i = 0; i < size - n; i++) {
//    		System.out.println("i = " + i + ", ele = " + diff.get(i));
    		set.add(diff.get(i));
    	}
    }
    
    
    
    ///////////////////////
    
    public static <T> List<T> intersection(List<T> set1, List<T> set2) {
    	List<T> intSet = new ArrayList<T>(set1);
    	intSet.retainAll(set2);
    	
    	return intSet;
    }
    
    public static <T> List<T> union(List<T> set1, List<T> set2) {
    	List<T> unionSet = new ArrayList<T>(set1);
    	unionSet.addAll(set2);
    	
    	return unionSet;
    }
    
    public static <T> List<T> difference(List<T> set1, List<T> set2) {
    	List<T> diff = new ArrayList<T>(set1);
    	diff.removeAll(set2);
    	
    	return diff;
    }
    
    //////////////////////
}
