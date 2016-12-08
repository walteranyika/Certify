/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.walter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Walter
 */
public class Item {
   private final IntegerProperty pos;
   private final StringProperty names; 
   public Item( int pos,String names) {
        this.names = new SimpleStringProperty(names);
        this.pos = new SimpleIntegerProperty(pos);       
    }
       
    public StringProperty namesProperty() {
        return names;
    }

    public IntegerProperty posProperty() {
        return pos;
    }
    
    public String getNames() {
        return this.names.get();
    }

    public int getPos() {
        return this.pos.get();
    }
    
}
