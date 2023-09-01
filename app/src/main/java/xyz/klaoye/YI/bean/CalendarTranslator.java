package xyz.klaoye.YI.bean;

import android.content.Context;

import java.util.Calendar;

import cn.hutool.core.date.chinese.GanZhi;
import xyz.klaoye.YI.R;

public class CalendarTranslator {
    Context context;

    final String[] TIAN_GAN;
    final String[] DI_ZHI;

    public CalendarTranslator(Context context) {
        this.context = context;
        TIAN_GAN = context.getResources().getStringArray(R.array.tian_gan);
        DI_ZHI = context.getResources().getStringArray(R.array.di_zhi);
    }

    public String[] getGanZhi(Calendar calendar) {
        final int Y = calendar.get(Calendar.YEAR);
        final int M = calendar.get(Calendar.MONTH);
        final int D = calendar.get(Calendar.DATE);
        final int H = calendar.get(Calendar.HOUR);
        final int C = Y / 100 + 1;
        String[] ganZhi = new String[4];

        int cacheNum, ganYear, ganMonth, ganDay, ganHour;
        int[] monthMask = {-3, -5, -7, -9, 0};
        cacheNum = (Y % 60) - 4;
        if (cacheNum < 0) {
            cacheNum += 60;
        }
        ganYear = cacheNum % 10;

        ganZhi[0] = TIAN_GAN[ganYear] + DI_ZHI[cacheNum % DI_ZHI.length];//年
        cacheNum = ganYear % 5;//循环表 monthMask
        ganMonth = (monthMask[cacheNum] + ganYear) % 10;
        if (ganMonth < 0) ganMonth += 10;
        ganZhi[1] = TIAN_GAN[ganMonth] + DI_ZHI[M];

        return ganZhi;
    }
}
