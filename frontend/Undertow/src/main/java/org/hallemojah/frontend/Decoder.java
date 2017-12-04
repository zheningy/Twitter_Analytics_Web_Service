package org.hallemojah.frontend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class Decoder{

    private static int getHexValue(byte b) {
        if ('0' <= b && b <= '9') {
            return b - 0x30;
        } else if ('A' <= b && b <= 'F') {
            return b - 0x37;
        } else if ('a' <= b && b <= 'f') {
            return b - 0x57;
        }
        return -1;
    }
    private static int getSum(char[] c){
        int sum = 0;
        for (int i = 0; i < 8 ; i ++)
        {
            if (c[i] == '1')
            {
                sum = sum + (1<<(7-i));
                //System.out.println("sum:"+sum);
            }
        }
        return sum;
    }
    private static char getC(char[] c){
        int sum = 0, len = 0;
        char num='0';
        len = c.length;
        for (int i = 0; i < len ; i ++)
        {
            if (c[i] == '1')
            {
                sum = sum + (1<<(len-i-1));
                //System.out.println("sum:"+sum);
            }
        }
        switch(sum){
            case 0: num = '0';break;case 1: num = '1';break;
            case 2: num = '2';break;case 3: num = '3';break;
            case 4: num = '4';break;case 5: num = '5';break;
            case 6: num = '6';break;case 7: num = '7';break;
            case 8: num = '8';break;case 9: num = '9';break;
            case 10: num = 'a';break;case 11: num = 'b';break;
            case 12: num = 'c';break;case 13: num = 'd';break;
            case 14: num = 'e';break;case 15: num = 'f';break;
        }
        return num;

    }
    private static char getCheck(char[] c){
        int sum = 0, len = 0;
        len = c.length;
        for (int i = 0; i < len ; i ++)
        {
            if (c[i] == '1')
            {
                sum++;
                //System.out.println("sum:"+sum);
            }
        }
        if (sum%2 == 0) return '0';
        else return '1';
    }
    public static String enAndDe(String choice, String strInput){

        String str = "0x294acd760xfe823fc00x82bc20fd0xba60aeac0xbaf12ee20xbad82eeb0x82c420d00xfeaabfd70x37801e0x807624430x80146b50x8036a0750xed018e040x8032a0520xdd0106ea0x809020690x9c0116f80x1f90204a0x8db00720x1a80bfff0x688020920x1fb2aeb20x2a82ea20x4802e9e0x30a020bd0x13433fea0xf879a0250xe281253c0x386ba9e90xcb8ac96a0x3bb6b9480x3cb69f31";
        String strr = "CC Team is awesome!";
        String[] strArray = {"","","",""};
        int strExtenLength = 0, flag = 0;
        String dOrE = "encode";
        String st = null, st1 = null;
        int l = 0;
        int count = 0;
        String output = "";
        //decode

        char d[]=new char[1024], d1[]=new char[1024],cc[]=new char[8];
        char [][]matrix= new char[32][32];
        char [][]newmatrix1= new char[25][25];
        int index = 0 ,index1 = 0, indexy = 0, indexz = 0, a =0, b=0;
        int indexi[] = new int [3];
        int indexj[] = new int [3];
        int size = 0;
        char com[] = {'1','1','1','0','1','1','0','0','0','0','0','1','0','0','0','1'};
        dOrE = choice;
        if (dOrE == "encode"){
            strr = strInput;
            int li = strr.length();
            //System.out.println(li);

            if (li <=14)
            {
                byte[] chars = strr.getBytes(StandardCharsets.UTF_8);
                st1 = Integer.toBinaryString(li);
                cc = st1.toCharArray();
                for (int jj = 0; jj < 8-st1.length(); jj++)
                {
                    d1[index1] = '0';
                    index1++;
                }
                for (int kk = 0; kk < st1.length(); kk++)
                {
                    d1[index1] = cc[kk];
                    index1++;
                }
                for ( int ii = 0 ; ii < chars.length; ii++)
                {
                    st1 = Integer.toBinaryString(chars[ii]);
                    cc = st1.toCharArray();
                    for (int jj = 0; jj < 8-st1.length(); jj++)
                    {
                        d1[index1] = '0';
                        index1++;
                    }
                    for (int kk = 0; kk < st1.length(); kk++)
                    {
                        d1[index1] = cc[kk];
                        index1++;
                    }
                    for (int kk = 0; kk < 7; kk++)
                    {
                        d1[index1] = '0';
                        index1++;
                    }
                    d1[index1] = getCheck(cc);
                    index1++;
                    //System.out.println(st1);
                }
                //System.out.println(d1);
                newmatrix1[0][0]='1';newmatrix1[0][1]='1';newmatrix1[0][2]='1';newmatrix1[0][3]='1';newmatrix1[0][4]='1';newmatrix1[0][5]='1';newmatrix1[0][6]='1';newmatrix1[0][7]='0';
                newmatrix1[1][0]='1';newmatrix1[1][1]='0';newmatrix1[1][2]='0';newmatrix1[1][3]='0';newmatrix1[1][4]='0';newmatrix1[1][5]='0';newmatrix1[1][6]='1';newmatrix1[1][7]='0';
                newmatrix1[2][0]='1';newmatrix1[2][1]='0';newmatrix1[2][2]='1';newmatrix1[2][3]='1';newmatrix1[2][4]='1';newmatrix1[2][5]='0';newmatrix1[2][6]='1';newmatrix1[2][7]='0';
                newmatrix1[3][0]='1';newmatrix1[3][1]='0';newmatrix1[3][2]='1';newmatrix1[3][3]='1';newmatrix1[3][4]='1';newmatrix1[3][5]='0';newmatrix1[3][6]='1';newmatrix1[3][7]='0';
                newmatrix1[4][0]='1';newmatrix1[4][1]='0';newmatrix1[4][2]='1';newmatrix1[4][3]='1';newmatrix1[4][4]='1';newmatrix1[4][5]='0';newmatrix1[4][6]='1';newmatrix1[4][7]='0';
                newmatrix1[5][0]='1';newmatrix1[5][1]='0';newmatrix1[5][2]='0';newmatrix1[5][3]='0';newmatrix1[5][4]='0';newmatrix1[5][5]='0';newmatrix1[5][6]='1';newmatrix1[5][7]='0';
                newmatrix1[6][0]='1';newmatrix1[6][1]='1';newmatrix1[6][2]='1';newmatrix1[6][3]='1';newmatrix1[6][4]='1';newmatrix1[6][5]='1';newmatrix1[6][6]='1';newmatrix1[6][7]='0';
                newmatrix1[7][0]='0';newmatrix1[7][1]='0';newmatrix1[7][2]='0';newmatrix1[7][3]='0';newmatrix1[7][4]='0';newmatrix1[7][5]='0';newmatrix1[7][6]='0';newmatrix1[7][7]='0';

                newmatrix1[0][14]='1';newmatrix1[0][15]='1';newmatrix1[0][16]='1';newmatrix1[0][17]='1';newmatrix1[0][18]='1';newmatrix1[0][19]='1';newmatrix1[0][20]='1';newmatrix1[0][13]='0';
                newmatrix1[1][14]='1';newmatrix1[1][15]='0';newmatrix1[1][16]='0';newmatrix1[1][17]='0';newmatrix1[1][18]='0';newmatrix1[1][19]='0';newmatrix1[1][20]='1';newmatrix1[1][13]='0';
                newmatrix1[2][14]='1';newmatrix1[2][15]='0';newmatrix1[2][16]='1';newmatrix1[2][17]='1';newmatrix1[2][18]='1';newmatrix1[2][19]='0';newmatrix1[2][20]='1';newmatrix1[2][13]='0';
                newmatrix1[3][14]='1';newmatrix1[3][15]='0';newmatrix1[3][16]='1';newmatrix1[3][17]='1';newmatrix1[3][18]='1';newmatrix1[3][19]='0';newmatrix1[3][20]='1';newmatrix1[3][13]='0';
                newmatrix1[4][14]='1';newmatrix1[4][15]='0';newmatrix1[4][16]='1';newmatrix1[4][17]='1';newmatrix1[4][18]='1';newmatrix1[4][19]='0';newmatrix1[4][20]='1';newmatrix1[4][13]='0';
                newmatrix1[5][14]='1';newmatrix1[5][15]='0';newmatrix1[5][16]='0';newmatrix1[5][17]='0';newmatrix1[5][18]='0';newmatrix1[5][19]='0';newmatrix1[5][20]='1';newmatrix1[5][13]='0';
                newmatrix1[6][14]='1';newmatrix1[6][15]='1';newmatrix1[6][16]='1';newmatrix1[6][17]='1';newmatrix1[6][18]='1';newmatrix1[6][19]='1';newmatrix1[6][20]='1';newmatrix1[6][13]='0';
                newmatrix1[7][14]='0';newmatrix1[7][15]='0';newmatrix1[7][16]='0';newmatrix1[7][17]='0';newmatrix1[7][18]='0';newmatrix1[7][19]='0';newmatrix1[7][20]='0';newmatrix1[7][13]='0';

                newmatrix1[14][0]='1';newmatrix1[14][1]='1';newmatrix1[14][2]='1';newmatrix1[14][3]='1';newmatrix1[14][4]='1';newmatrix1[14][5]='1';newmatrix1[14][6]='1';newmatrix1[14][7]='0';
                newmatrix1[15][0]='1';newmatrix1[15][1]='0';newmatrix1[15][2]='0';newmatrix1[15][3]='0';newmatrix1[15][4]='0';newmatrix1[15][5]='0';newmatrix1[15][6]='1';newmatrix1[15][7]='0';
                newmatrix1[16][0]='1';newmatrix1[16][1]='0';newmatrix1[16][2]='1';newmatrix1[16][3]='1';newmatrix1[16][4]='1';newmatrix1[16][5]='0';newmatrix1[16][6]='1';newmatrix1[16][7]='0';
                newmatrix1[17][0]='1';newmatrix1[17][1]='0';newmatrix1[17][2]='1';newmatrix1[17][3]='1';newmatrix1[17][4]='1';newmatrix1[17][5]='0';newmatrix1[17][6]='1';newmatrix1[17][7]='0';
                newmatrix1[18][0]='1';newmatrix1[18][1]='0';newmatrix1[18][2]='1';newmatrix1[18][3]='1';newmatrix1[18][4]='1';newmatrix1[18][5]='0';newmatrix1[18][6]='1';newmatrix1[18][7]='0';
                newmatrix1[19][0]='1';newmatrix1[19][1]='0';newmatrix1[19][2]='0';newmatrix1[19][3]='0';newmatrix1[19][4]='0';newmatrix1[19][5]='0';newmatrix1[19][6]='1';newmatrix1[19][7]='0';
                newmatrix1[20][0]='1';newmatrix1[20][1]='1';newmatrix1[20][2]='1';newmatrix1[20][3]='1';newmatrix1[20][4]='1';newmatrix1[20][5]='1';newmatrix1[20][6]='1';newmatrix1[20][7]='0';
                newmatrix1[13][0]='0';newmatrix1[13][1]='0';newmatrix1[13][2]='0';newmatrix1[13][3]='0';newmatrix1[13][4]='0';newmatrix1[13][5]='0';newmatrix1[13][6]='0';newmatrix1[13][7]='0';

                newmatrix1[6][8]='1';newmatrix1[6][9]='0';newmatrix1[6][10]='1';newmatrix1[6][11]='0';newmatrix1[6][12]='1';
                newmatrix1[8][6]='1';newmatrix1[9][6]='0';newmatrix1[10][6]='1';newmatrix1[11][6]='0';newmatrix1[12][6]='1';
                //System.out.println(index1);

                int i = 0; int j = 0;
                for (j = 20; j>=13;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 20; i>=8; i--)
                        {
                            if (indexy<index1) {
                                newmatrix1[i][j]=d1[indexy];
                                //System.out.println("!");
                                indexy++;}
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                        }
                    }
                    else
                    {
                        for (i = 8; i <= 20; i++)
                        {
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                for (j = 12; j >= 8;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 20; i>=0; i--)
                        {
                            if (i == 6) continue;
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                    else
                    {
                        for (i = 0; i<=20; i++)
                        {
                            if (i == 6) continue;
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                for (j = 7; j>= 0;j--)
                {
                    if (j == 6) continue;
                    if (j % 2 == 0)
                    {
                        for (i = 12; i >= 8; i--)
                        {
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                    else
                    {
                        for (i = 8; i <= 12; i++)
                        {
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                int num =0, num1 =0,flag3 =0, iiii = 0, x = 0,flag4 = 0, indexout = 0;
                char []word = new char [4];
                char s = '0';
                // for (int kkk =0; kkk<21; kkk++) System.out.println(newmatrix1[kkk]);
                System.out.print("0x");
                output+="0x";
                for (int kkk =0; kkk<21; kkk++)
                {
                    for (int jjj =0 ;jjj<21; jjj++)
                    {

                        if (iiii == 3)
                        {
                            word[iiii] = newmatrix1[kkk][jjj];
                            s=getC(word);
                            if (flag3 != 0 || s!='0'){
                                flag3 = 1;
                                System.out.print(s);
                                output+=s;
                            }
                            iiii=0;

                            num ++;
                            if (num%8 == 0)
                            {
                                if (441-num*4<32 && flag4 == 0)
                                {
                                    x = (441-num*4)%4;
                                    for (int ij = 0; ij < 4-x; ij++)
                                    {
                                        //System.out.println("num:"+num);
                                        word[ij] = '0';
                                    }
                                    iiii = 4-x;
                                    //System.out.print(newmatrix1[kkk][jjj]);
                                    //word[iiii] = newmatrix1[kkk][jjj];
                                    //iiii ++;
                                    flag4 = 1;
                                    System.out.print("0x");
                                    output+="0x";
                                    continue;
                                }
                                System.out.print("0x");
                                output+="0x";
                                flag3 = 0;
                            }
                            continue;
                        }
                        word[iiii] = newmatrix1[kkk][jjj];
                        iiii ++;

                    }
                }
                System.out.println("\n");

            }
            if (li >14)
            {
                byte[] chars = strr.getBytes(StandardCharsets.UTF_8);
                st1 = Integer.toBinaryString(li);
                cc = st1.toCharArray();
                index1 = 0;
                for (int jj = 0; jj < 8-st1.length(); jj++)
                {
                    d1[index1] = '0';
                    index1++;
                }
                for (int kk = 0; kk < st1.length(); kk++)
                {
                    d1[index1] = cc[kk];
                    index1++;
                }
                for ( int ii = 0 ; ii < chars.length; ii++)
                {
                    st1 = Integer.toBinaryString(chars[ii]);
                    cc = st1.toCharArray();
                    for (int jj = 0; jj < 8-st1.length(); jj++)
                    {
                        d1[index1] = '0';
                        index1++;
                    }
                    for (int kk = 0; kk < st1.length(); kk++)
                    {
                        d1[index1] = cc[kk];
                        index1++;
                    }
                    for (int kk = 0; kk < 7; kk++)
                    {
                        d1[index1] = '0';
                        index1++;
                    }
                    d1[index1] = getCheck(cc);
                    index1++;
                    //      System.out.println(st1);
                }
                // System.out.println(d1);
                newmatrix1[0][0]='1';newmatrix1[0][1]='1';newmatrix1[0][2]='1';newmatrix1[0][3]='1';newmatrix1[0][4]='1';newmatrix1[0][5]='1';newmatrix1[0][6]='1';newmatrix1[0][7]='0';
                newmatrix1[1][0]='1';newmatrix1[1][1]='0';newmatrix1[1][2]='0';newmatrix1[1][3]='0';newmatrix1[1][4]='0';newmatrix1[1][5]='0';newmatrix1[1][6]='1';newmatrix1[1][7]='0';
                newmatrix1[2][0]='1';newmatrix1[2][1]='0';newmatrix1[2][2]='1';newmatrix1[2][3]='1';newmatrix1[2][4]='1';newmatrix1[2][5]='0';newmatrix1[2][6]='1';newmatrix1[2][7]='0';
                newmatrix1[3][0]='1';newmatrix1[3][1]='0';newmatrix1[3][2]='1';newmatrix1[3][3]='1';newmatrix1[3][4]='1';newmatrix1[3][5]='0';newmatrix1[3][6]='1';newmatrix1[3][7]='0';
                newmatrix1[4][0]='1';newmatrix1[4][1]='0';newmatrix1[4][2]='1';newmatrix1[4][3]='1';newmatrix1[4][4]='1';newmatrix1[4][5]='0';newmatrix1[4][6]='1';newmatrix1[4][7]='0';
                newmatrix1[5][0]='1';newmatrix1[5][1]='0';newmatrix1[5][2]='0';newmatrix1[5][3]='0';newmatrix1[5][4]='0';newmatrix1[5][5]='0';newmatrix1[5][6]='1';newmatrix1[5][7]='0';
                newmatrix1[6][0]='1';newmatrix1[6][1]='1';newmatrix1[6][2]='1';newmatrix1[6][3]='1';newmatrix1[6][4]='1';newmatrix1[6][5]='1';newmatrix1[6][6]='1';newmatrix1[6][7]='0';
                newmatrix1[7][0]='0';newmatrix1[7][1]='0';newmatrix1[7][2]='0';newmatrix1[7][3]='0';newmatrix1[7][4]='0';newmatrix1[7][5]='0';newmatrix1[7][6]='0';newmatrix1[7][7]='0';

                newmatrix1[0][18]='1';newmatrix1[0][19]='1';newmatrix1[0][20]='1';newmatrix1[0][21]='1';newmatrix1[0][22]='1';newmatrix1[0][23]='1';newmatrix1[0][24]='1';newmatrix1[0][17]='0';
                newmatrix1[1][18]='1';newmatrix1[1][19]='0';newmatrix1[1][20]='0';newmatrix1[1][21]='0';newmatrix1[1][22]='0';newmatrix1[1][23]='0';newmatrix1[1][24]='1';newmatrix1[1][17]='0';
                newmatrix1[2][18]='1';newmatrix1[2][19]='0';newmatrix1[2][20]='1';newmatrix1[2][21]='1';newmatrix1[2][22]='1';newmatrix1[2][23]='0';newmatrix1[2][24]='1';newmatrix1[2][17]='0';
                newmatrix1[3][18]='1';newmatrix1[3][19]='0';newmatrix1[3][20]='1';newmatrix1[3][21]='1';newmatrix1[3][22]='1';newmatrix1[3][23]='0';newmatrix1[3][24]='1';newmatrix1[3][17]='0';
                newmatrix1[4][18]='1';newmatrix1[4][19]='0';newmatrix1[4][20]='1';newmatrix1[4][21]='1';newmatrix1[4][22]='1';newmatrix1[4][23]='0';newmatrix1[4][24]='1';newmatrix1[4][17]='0';
                newmatrix1[5][18]='1';newmatrix1[5][19]='0';newmatrix1[5][20]='0';newmatrix1[5][21]='0';newmatrix1[5][22]='0';newmatrix1[5][23]='0';newmatrix1[5][24]='1';newmatrix1[5][17]='0';
                newmatrix1[6][18]='1';newmatrix1[6][19]='1';newmatrix1[6][20]='1';newmatrix1[6][21]='1';newmatrix1[6][22]='1';newmatrix1[6][23]='1';newmatrix1[6][24]='1';newmatrix1[6][17]='0';
                newmatrix1[7][18]='0';newmatrix1[7][19]='0';newmatrix1[7][20]='0';newmatrix1[7][21]='0';newmatrix1[7][22]='0';newmatrix1[7][23]='0';newmatrix1[7][24]='0';newmatrix1[7][17]='0';

                newmatrix1[18][0]='1';newmatrix1[18][1]='1';newmatrix1[18][2]='1';newmatrix1[18][3]='1';newmatrix1[18][4]='1';newmatrix1[18][5]='1';newmatrix1[18][6]='1';newmatrix1[18][7]='0';
                newmatrix1[19][0]='1';newmatrix1[19][1]='0';newmatrix1[19][2]='0';newmatrix1[19][3]='0';newmatrix1[19][4]='0';newmatrix1[19][5]='0';newmatrix1[19][6]='1';newmatrix1[19][7]='0';
                newmatrix1[20][0]='1';newmatrix1[20][1]='0';newmatrix1[20][2]='1';newmatrix1[20][3]='1';newmatrix1[20][4]='1';newmatrix1[20][5]='0';newmatrix1[20][6]='1';newmatrix1[20][7]='0';
                newmatrix1[21][0]='1';newmatrix1[21][1]='0';newmatrix1[21][2]='1';newmatrix1[21][3]='1';newmatrix1[21][4]='1';newmatrix1[21][5]='0';newmatrix1[21][6]='1';newmatrix1[21][7]='0';
                newmatrix1[22][0]='1';newmatrix1[22][1]='0';newmatrix1[22][2]='1';newmatrix1[22][3]='1';newmatrix1[22][4]='1';newmatrix1[22][5]='0';newmatrix1[22][6]='1';newmatrix1[22][7]='0';
                newmatrix1[23][0]='1';newmatrix1[23][1]='0';newmatrix1[23][2]='0';newmatrix1[23][3]='0';newmatrix1[23][4]='0';newmatrix1[23][5]='0';newmatrix1[23][6]='1';newmatrix1[23][7]='0';
                newmatrix1[24][0]='1';newmatrix1[24][1]='1';newmatrix1[24][2]='1';newmatrix1[24][3]='1';newmatrix1[24][4]='1';newmatrix1[24][5]='1';newmatrix1[24][6]='1';newmatrix1[24][7]='0';
                newmatrix1[17][0]='0';newmatrix1[17][1]='0';newmatrix1[17][2]='0';newmatrix1[17][3]='0';newmatrix1[17][4]='0';newmatrix1[17][5]='0';newmatrix1[17][6]='0';newmatrix1[17][7]='0';

                newmatrix1[16][16]='1';newmatrix1[16][17]='1';newmatrix1[16][18]='1';newmatrix1[16][19]='1';newmatrix1[16][20]='1';
                newmatrix1[17][16]='1';newmatrix1[17][17]='0';newmatrix1[17][18]='0';newmatrix1[17][19]='0';newmatrix1[17][20]='1';
                newmatrix1[18][16]='1';newmatrix1[18][17]='0';newmatrix1[18][18]='1';newmatrix1[18][19]='0';newmatrix1[18][20]='1';
                newmatrix1[19][16]='1';newmatrix1[19][17]='0';newmatrix1[19][18]='0';newmatrix1[19][19]='0';newmatrix1[19][20]='1';
                newmatrix1[20][16]='1';newmatrix1[20][17]='1';newmatrix1[20][18]='1';newmatrix1[20][19]='1';newmatrix1[20][20]='1';


                newmatrix1[6][8]='1';newmatrix1[6][9]='0';newmatrix1[6][10]='1';newmatrix1[6][11]='0';newmatrix1[6][12]='1';newmatrix1[6][13]='0';newmatrix1[6][14]='1';newmatrix1[6][15]='0';newmatrix1[6][16]='1';
                newmatrix1[8][6]='1';newmatrix1[9][6]='0';newmatrix1[10][6]='1';newmatrix1[11][6]='0';newmatrix1[12][6]='1';newmatrix1[13][6]='0';newmatrix1[14][6]='1';newmatrix1[15][6]='0';newmatrix1[16][6]='1';
                // System.out.println(index1);
                indexz = 0;indexy = 0;
                int i = 0; int j = 0;
                for (j = 24; j>=17;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 24; i>=8; i--)
                        {
                            if (i>=16 && i<=20 && j<=20 && j>=16) continue;
                            if (indexy<index1) {
                                newmatrix1[i][j]=d1[indexy];
                                //System.out.println("!");
                                indexy++;}
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                        }
                    }
                    else
                    {
                        for (i = 8; i <= 24; i++)
                        {
                            if (i>=16 && i<=20 && j<=20 && j>=16) continue;
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                for (j = 16; j >= 8;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 24; i>=0; i--)
                        {
                            if (i == 6) continue;
                            if (i>=16 && i<=20 && j<=20 && j>=16) continue;
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                    else
                    {
                        for (i = 0; i<=24; i++)
                        {
                            if (i == 6) continue;
                            if (i>=16 && i<=20 && j<=20 && j>=16) continue;
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                for (j = 7; j>= 0;j--)
                {
                    if (j == 6) continue;
                    if (j % 2 == 0)
                    {
                        for (i = 16; i >= 8; i--)
                        {
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                    else
                    {
                        for (i = 8; i <= 16; i++)
                        {
                            if (indexy<index1) newmatrix1[i][j]=d1[indexy];
                            else
                            {
                                if (indexz==16)
                                {
                                    indexz = 0;
                                }
                                newmatrix1[i][j]=com[indexz];
                                indexz++;
                            }
                            indexy++;
                        }
                    }
                }
                int num =0, num1 =0,flag3 =0, iiii = 0, x = 0,flag4 = 0;
                char []word = new char [4];
                char s = '0';

                // for (int kkk =0; kkk<25; kkk++) System.out.println(newmatrix1[kkk]);
                System.out.print("0x");
                output+="0x";
                for (int kkk =0; kkk<25; kkk++)
                {
                    for (int jjj =0 ;jjj<25; jjj++)
                    {

                        if (iiii == 3)
                        {
                            word[iiii] = newmatrix1[kkk][jjj];
                            s=getC(word);
                            if (flag3 != 0 || s!='0'){
                                flag3 = 1;
                                System.out.print(s);
                                output+=s;
                            }
                            iiii=0;

                            num ++;
                            if (num%8 == 0)
                            {
                                if (625-num*4<32 && flag4 == 0)
                                {
                                    x = (625-num*4)%4;
                                    for (int ij = 0; ij < 4-x; ij++)
                                    {
                                        word[ij] = '0';
                                    }
                                    iiii = 4-x;
                                    flag4 = 1;
                                    System.out.print("0x");
                                    output+="0x";
                                    continue;
                                }
                                System.out.print("0x");
                                output+=s;
                                flag3 = 0;
                            }
                            continue;
                        }
                        word[iiii] = newmatrix1[kkk][jjj];
                        iiii ++;
                    }
                }
                System.out.println("\n");

            }
        }
        if (dOrE == "decode"){
            str = strInput;
            strArray = str.split("0x");
            for (int j = 1; j < strArray.length; j++)
            {
                byte[] encodedChars = strArray[j].getBytes(StandardCharsets.UTF_8);
                int n = strArray[j].length();

                for (int p = 0 ; p < 8-n; p++)
                {
                    for (int i = 0; i < 4 ; i ++)
                    {
                        d[index] = '0';
                        index++;
                    }
                }
                for (int k = 0; k < n; k++)
                {
                    st = Integer.toBinaryString(getHexValue(encodedChars[k]));
                    //System.out.println(st);
                    l = st.length();
                    char c[]=new char[l];
                    int m = 4 - l;
                    c = st.toCharArray();
                    for (int i = 0; i < m; i++)
                    {
                        d[index] = '0';
                        index ++;
                    }
                    for (int i =0; i < l; i++)
                    {
                        d[index] = c[i];
                        index++;
                    }
                }

            }
            for (int i=0; i<32; i++)
            {
                for (int j=0; j<32; j++)
                {
                    matrix[i][j] = d[i*32 +j];
                }
                System.out.println(matrix[i]);
            }

            for (int i=0; i<=25; i++)
            {
                for (int j =0; j<=25; j++)
                {
                    if ( matrix[i][j]=='1' && matrix[i][j+1]=='1' && matrix[i][j+2]=='1' && matrix[i][j+3]=='1' && matrix[i][j+4]=='1' && matrix[i][j+5]=='1' && matrix[i][j+6]=='1' &&
                            matrix[i+1][j]=='1' && matrix[i+1][j+1]=='0' && matrix[i+1][j+2]=='0' && matrix[i+1][j+3]=='0' && matrix[i+1][j+4]=='0' && matrix[i+1][j+5]=='0' && matrix[i+1][j+6]=='1' &&
                            matrix[i+2][j]=='1' && matrix[i+2][j+1]=='0' && matrix[i+2][j+2]=='1' && matrix[i+2][j+3]=='1' && matrix[i+2][j+4]=='1' && matrix[i+2][j+5]=='0' && matrix[i+2][j+6]=='1' &&
                            matrix[i+3][j]=='1' && matrix[i+3][j+1]=='0' && matrix[i+3][j+2]=='1' && matrix[i+3][j+3]=='1' && matrix[i+3][j+4]=='1' && matrix[i+3][j+5]=='0' && matrix[i+3][j+6]=='1' &&
                            matrix[i+4][j]=='1' && matrix[i+4][j+1]=='0' && matrix[i+4][j+2]=='1' && matrix[i+4][j+3]=='1' && matrix[i+4][j+4]=='1' && matrix[i+3][j+5]=='0' && matrix[i+4][j+6]=='1' &&
                            matrix[i+5][j]=='1' && matrix[i+5][j+1]=='0' && matrix[i+5][j+2]=='0' && matrix[i+5][j+3]=='0' && matrix[i+5][j+4]=='0' && matrix[i+5][j+5]=='0' && matrix[i+5][j+6]=='1' &&
                            matrix[i+6][j]=='1' && matrix[i+6][j+1]=='1' && matrix[i+6][j+2]=='1' && matrix[i+6][j+3]=='1' && matrix[i+6][j+4]=='1' && matrix[i+6][j+5]=='1' && matrix[i+6][j+6]=='1' )
                    {
                        flag ++;
                        indexi[a]= i;
                        indexj[b]= j;
                        System.out.println(i+" "+j);
                        a++;b++;
                        if (flag == 3) break;
                    }
                }
                if (flag == 3) break;
            }

            int pattern = 0;
            int indexw = 0, len = 0, flag1 = 0, flag2 = 0;
            char word[] = new char [8];
            int i = 0, j =0, intw = 0;
            char tmp[][] = new char [32][32];
            char newmatrix[][] = new char [32][32];
            size = indexi[2]-indexi[0]+7;
            int maxindex = indexi[2]+6;
            int maxi = 0, maxj = 0;

            if (indexi[0] == indexi[1] && indexi[1]<indexi[2])
            {
                if (indexj[1] == indexj[2]) {pattern = 1;}
                else {pattern = 0; }
            }
            if (indexi[2] == indexi[1])
            {
                if(indexj[0] != indexj[1]) {pattern = 2;maxi = indexi[0]; maxj = indexj[1];System.out.println("2!"+maxi+" "+maxj);}
                else {pattern = 3;}
            }
            for (i = 0; i < size ; i++)
            {
                for (j = 0; j < size ; j++)
                {
                    tmp[i][j] = matrix[i+maxi][j+maxj];
                }
            }



            if (pattern == 0)
            {
                for (i = 0; i < size ; i++)
                {
                    for (j = 0; j < size ; j++)
                    {
                        newmatrix[i][j] = tmp[i][j];
                    }
                }
            }
            if (pattern == 1)
            {
                for (i = 0; i < size ; i++)
                {
                    for (j = 0; j < size ; j++)
                    {
                        newmatrix[size - j-1][i] = tmp[i][j];
                    }
                }
            }
            if (pattern == 2)
            {
                for (i = 0; i < size ; i++)
                {
                    for (j = 0; j < size ; j++)
                    {
                        newmatrix[size - i-1][size - j-1] = tmp[i][j];
                    }
                }
            }
            if (pattern == 3)
            {
                for (i = 0; i < size ; i++)
                {
                    for (j = 0; j < size ; j++)
                    {
                        newmatrix[j][size - i-1] = tmp[i][j];
                    }
                }
            }
            for (i = 0; i < size ; i++)
            {
                System.out.println(newmatrix[i]);}

            if (size == 21)
            {
                for (j = 20; j>=13;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 20; i>=8; i--)
                        {
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                // System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//{System.out.print();

                            }
                        }

                    }
                    else
                    {
                        for (i = 8; i <= 20; i++)
                        {
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }

                    }
                    if (flag2 == 1) break;
                }
                for (j = 12; j >= 8;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 20; i>=0; i--)
                        {
                            if (i == 6) continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }
                    }
                    else
                    {
                        for (i = 0; i<=20; i++)
                        {
                            if (i == 6) continue;
                            word[indexw] = matrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }
                    }
                    if (flag2 == 1) break;

                }
                for (j = 7; j>= 0;j--)
                {
                    if (j == 6) continue;
                    if (j % 2 == 0)
                    {
                        for (i = 12; i >= 8; i--)
                        {
                            if (i == 7) continue;
                            word[indexw] = matrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }
                    }
                    else
                    {
                        for (i = 8; i <= 12; i++)
                        {
                            //if (i == 7) continue;
                            word[indexw] = matrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }
                    }
                    if (flag2 == 1) break;
                }

            }

            if (size == 25)
            {
                for (j = 24; j>= 17;j--)
                {
                    if (j % 2 == 0)
                    {
                        //System.out.println(j%2);
                        for (i = 24; i>=8; i--)
                        {
                            if (i>=16 && i<=20 && j<=20) continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                //convert(word);
                                if (count == 0) len = getSum(word);
                                //System.out.println(len);}
                                count ++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));

                            }
                        }

                    }
                    else
                    {
                        //System.out.println(j%2);
                        for (i = 8; i<=24; i++)
                        {
                            if (i>=16 && i<=20 && j <= 20) continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                count++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));
                                //convert(word);
                            }
                        }
                    }
                    if (flag2 == 1) break;

                }


                for (j = 16; j>=8;j--)
                {
                    if (j % 2 == 0)
                    {
                        for (i = 24; i>= 0; i--)
                        {
                            if (i == 6) continue;
                            if (i <= 20 && i >=16 && j <= 20 && j >=16)  continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                count++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);// System.out.print((char) getSum(word));
                                //convert(word);
                            }
                        }
                    }
                    else
                    {
                        for (i = 0; i<=24; i++)
                        {
                            if (i == 6) continue;
                            if (i <= 20 && i >=16 && j <= 20 && j >=16)  continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                count++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));
                                //convert(word);
                            }
                        }
                    }
                    if (flag2 == 1) break;
                }
                for (j = 7; j>=0;j--)
                {
                    if (j == 6) continue;
                    if (j % 2 == 0)
                    {
                        for (i = 16; i>=8; i--)
                        {
                            //if (i == 6) continue;
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                count++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));
                                //convert(word);
                            }
                        }
                    }
                    else
                    {
                        for (i = 8; i<=16; i++)
                        {
                            word[indexw] = newmatrix[i][j];
                            indexw++;
                            if (indexw == 8)
                            {
                                indexw = 0;
                                count++;
                                if (count > 2*len +1) {flag2 = 1;break;}
                                if (count%2 == 0) output+=(char) getSum(word);//System.out.print((char) getSum(word));
                            }
                        }
                    }
                    if (flag2 == 1) break;
                }

            }
        }

        return output;

    }


    public static void main(String args[]){
        // System.out.println("!");
        System.out.println(enAndDe("decode","0xe124dfcc0xd10450660x902657750xf003176f0xd124d76e0x8d4650640xbc221fd90xa803001e0x88cb10e40x7419048f0x34819ef70x3444e75e0x9a1b343f0x700180090x2ff55fd10xcc1090490xddd4d7640xe5d4d76e0x95d457490xfc1650550x8ff31ff10x530f77aa0x5e976d710xe12731420xaddb35bb0xe8061ea60x8a902ae10x9e87928a0xa8ff30c40xfa0301830x1f3635430xb6db863f"));
    }
}