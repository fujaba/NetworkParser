/*
   Copyright (c) 2014 Stefan 
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package de.uniks.networkparser.test.model;


public abstract class Fruit 
{

   //==========================================================================
   
   
   public void removeYou()
   {
   }

   
   //==========================================================================
   
   public static final String PROPERTY_X = "x";
   
   private double x;

   public double getX()
   {
      return this.x;
   }
   
   public void setX(double value)
   {
      if (this.x != value)
      {
         this.x = value;
      }
   }
   
   public Fruit withX(double value)
   {
      setX(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      s.append(" ").append(this.getX());
      s.append(" ").append(this.getY());
      return s.substring(1);
   }


   
   //==========================================================================
   
   public static final String PROPERTY_Y = "y";
   
   private double y;

   public double getY()
   {
      return this.y;
   }
   
   public void setY(double value)
   {
      if (this.y != value)
      {
         this.y = value;
      }
   }
   
   public Fruit withY(double value)
   {
      setY(value);
      return this;
   } 
}
