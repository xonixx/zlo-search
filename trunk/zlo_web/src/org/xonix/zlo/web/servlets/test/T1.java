package org.xonix.zlo.web.servlets.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

/**
 * Author: Vovan
 * Date: 22.01.2008
 * Time: 4:32:05
 */
public class T1{
 public static void main(String[] args) {
   int x = 5, y = 7;
   swap(x, y);
   System.out.println("x = " + x + ", y = " + y );
 }
 static void swap(int a, int b){
   int c = a;
   a = b;
   b = c;
//     ArrayList.sort()
     new Analyzer() {

         public TokenStream tokenStream(String fieldName, Reader reader) {
             return null;
         }
     };
 }
}

