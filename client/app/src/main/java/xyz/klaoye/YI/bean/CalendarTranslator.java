package xyz.klaoye.YI.bean;

import android.content.Context;

import java.util.Arrays;
import java.util.Calendar;

import cn.hutool.core.date.chinese.GanZhi;
import xyz.klaoye.YI.R;

public class CalendarTranslator {
    Context context;

    final String[] TIAN_GAN;
    final String[] DI_ZHI;
    String[] WHELLS = new String[60];

    public CalendarTranslator(Context context) {
        this.context = context;
        TIAN_GAN = context.getResources().getStringArray(R.array.tian_gan);
        DI_ZHI = context.getResources().getStringArray(R.array.di_zhi);
        for (int i = 0; i < 60; i++) {
            WHELLS[i] = TIAN_GAN[i % 10] + DI_ZHI[i % 12];
            //System.out.println(i);
        }
        System.out.println(Arrays.toString(WHELLS));
    }

    public String[] getGanZhi(Calendar calendar) {
        final int Y = calendar.get(Calendar.YEAR);
        final int M = calendar.get(Calendar.MONTH);
        final int D = calendar.get(Calendar.DATE);
        final int H = calendar.get(Calendar.HOUR);
        final int C = Y / 100 + 1;
        String[] ganZhi = new String[4];

        int yearSort = (Y % 60) - 4;
        ganZhi[0] = WHELLS[yearSort];

        int point = 0;
        switch (yearSort % 10) {
            case 9:
            case 4:
                point = 10;
                break;
            case 5:
            case 0:
                point = 2;
                break;
            case 6:
            case 1:
                point = 4;
                break;
            case 7:
            case 2:
                point = 6;
                break;
            case 8:
            case 3:
                point = 7;
                break;
        }
        ganZhi[1] = TIAN_GAN[(M + point) % 10] + DI_ZHI[(M + 1) % 12];

        return ganZhi;
    }
}
