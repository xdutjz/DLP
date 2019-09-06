package com.nds.dlp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountDistance {

    //input longitude and latitude
    //count with given coordinate, include TagNo, TagLat and TagLon
    //count closest Tag, then use TagNo count closest cell tower
    public ArrayList distanceCount(double givenLon, double givenLat){

        ArrayList returnArraylist = new ArrayList();

        //Get Tag no
        ArrayList TagNoToUse = GetTagNoToUse(givenLon, givenLat);
        /*for (int x=0; x<TagNoToUse.size();x++){
            System.out.println((int)TagNoToUse.get(x));
        }*/

        double closestCellTowerDistance = 10000;

        for (int x=0;x<TagNoToUse.size();x++){
            try {
                JSONReader reader = new JSONReader(new FileReader("/sdcard/test/cellxian.json"));
                reader.startArray();
                while(reader.hasNext()) {
                    String eachCell = reader.readString();
                    GetCell getCell = JSON.parseObject(eachCell, GetCell.class);
                    //At here we get supposed Tag
                    if (getCell.getTagno() == (int)TagNoToUse.get(x)){
                        double NowCellTowerDistance = CountOfCoordinate(givenLon, givenLat,
                                getCell.getLon(), getCell.getLat());
                        System.out.println(getCell.getCell() + "distance" + NowCellTowerDistance);
                        if (closestCellTowerDistance - NowCellTowerDistance > 1E-16 ){
                            returnArraylist.clear();
                            closestCellTowerDistance = NowCellTowerDistance;
                            returnArraylist.add(getCell.getArea());
                            returnArraylist.add(getCell.getCell());
                            returnArraylist.add(getCell.getNet());
                            returnArraylist.add(getCell.getRadio());
                            returnArraylist.add(getCell.getLon());
                            returnArraylist.add(getCell.getLat());
                            returnArraylist.add(getCell.getTagno());
                        }
                    }
                    //System.out.println(getCell.getMcc());
                }
                reader.endArray();
                reader.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //System.out.println(  TagNoToUse.get(x) + " this is using Tag no ");
        }

        return returnArraylist;

    }

    //Actual Count
    private double CountOfCoordinate(double Lon1, double Lat1, double Lon2, double Lat2){
        double sqrt = Math.sqrt((Math.pow(Lon1 - Lon2, 2)) + (Math.pow(Lat1 - Lat2, 2)));
        return sqrt;
    }

    //Get TagNo
    private ArrayList GetTagNoToUse(double givenLonToGetTag, double givenLatToGetTag){

        ArrayList finalTagNo = new ArrayList<>();

        double[] ArrayLon = new double[]{108.775, 108.825, 108.875, 108.925, 108.975,
                108.775, 108.825, 108.875, 108.925, 108.975,
                108.775, 108.825, 108.875, 108.925, 108.975,
                108.775, 108.825, 108.875, 108.925, 108.975};
        double[] ArrayLat = new double[]{34.025, 34.025, 34.025, 34.025, 34.025,
                34.075, 34.075, 34.075, 34.075, 34.075,
                34.125, 34.125, 34.125, 34.125, 34.125,
                34.175, 34.175, 34.175, 34.175, 34.175};

        double FinalDistance = CountOfCoordinate(givenLonToGetTag, givenLatToGetTag,
                ArrayLon[0], ArrayLat[0]);

        for (int TagNo=0;TagNo<20;TagNo++){
            double NowDistance = CountOfCoordinate(givenLonToGetTag, givenLatToGetTag,
                    ArrayLon[TagNo], ArrayLat[TagNo]);

            //if NowDistance < FinalDistance, set FinalDistance = NowDistance,
            //Clear Final Tag No list and add Now Tag No to Last
            if (FinalDistance - NowDistance >= 1E-13){
                FinalDistance = NowDistance;
                finalTagNo.clear();
                finalTagNo.add(TagNo);
                //System.out.println("this is shortest， Now " + NowDistance + " Final " + FinalDistance);
            }

            //if Now Distance == Final Distance, add Now Tag No to List
            else if (Math.abs(NowDistance - FinalDistance) < 1E-13){
                finalTagNo.add(TagNo);
                //System.out.println("equals now " + NowDistance + " Final " + FinalDistance);
            }
            //else {System.out.println("Now Distance larger Final Distance");}
        }

        return finalTagNo;
    }
}
