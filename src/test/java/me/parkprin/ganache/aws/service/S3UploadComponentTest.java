package me.parkprin.ganache.aws.service;

import me.parkprin.ganache.ApplicationTest;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class S3UploadComponentTest extends ApplicationTest {

    @Test
    public void 날짜의_현재_월을_추출한다(){
        LocalDate localDate = LocalDate.now();
        Assert.assertEquals(localDate.getYear(), 2021);
        Assert.assertEquals(localDate.getMonth().getValue(), 4);
        System.out.println(localDate.getDayOfMonth());
    }
}
