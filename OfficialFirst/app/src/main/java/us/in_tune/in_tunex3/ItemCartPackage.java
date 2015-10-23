package us.in_tune.in_tunex3;

import java.util.ArrayList;

/**
 * Created by Randy on 10/13/2015.
 */

/*
@author Jay

wraper class for passing the itemCart ArrayList from one activity to another
 */
public class ItemCartPackage {
    private ArrayList<NameAndPrice> items;

    public ItemCartPackage(ArrayList<NameAndPrice> item){
        this.items = item;
    }

    /*
    Getting()

    gets the item

    @param (none)
    @return item ArrayList<NameAndPrice> items;
     */
    public ArrayList<NameAndPrice> getItems(){
        return items;
    }

    public void setter(ArrayList<NameAndPrice> a){
        items = a;
    }

    @Override
    public String toString() {

        String curString = "";
        for(int i=0; i<items.size(); i++){

            curString += items.get(i).toString();
        }

        return curString;
    }

}
