package com.example.team1.interface_team1;

/**
 * Created by s-tutida on 2017/08/06.
 */
public class Calc {

    int x,y,z;
    Calc(int x,int y,int z){
        this.x = x;
        this.y = y;//前後の傾斜
        this.z = z;//左右の傾斜
    }

    /**
     * 第何象限かを返す
     * @return
     */
    int getDirection(){
        if(y >= 0 && z >= 0) return 1;
        if(y >= 0 && z <= 0) return 2;
        if(y <= 0 && z <= 0) return 3;
        if(y <= 0 && z >= 0) return 4;
        if(y == 0 && z == 0) return 0;

        return -1;
    }

    /**
     * どれくらいの量かを返す
     * @return
     */
    int getAmount(){
        int amount = y * y + z * z ;

        if(amount < 50) return 0;//水の量はゼロ
        if(amount < 100) return 1;//水の量は1
        if(amount < 200) return 2;//水の量は2
        if(amount <300) return 3;

        return 4;//max 3を返す
    }


}
