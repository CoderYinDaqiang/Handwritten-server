package com.yun.server.JunitTest;

import com.yun.server.utils.SecurityUtils;
import org.junit.Test;

public class SecurityUtilsTest  {


    @Test
    public void testtoMD5(){
        String pw = "3467FGHZ67DSASHJ";
        System.out.println(pw.length());
        String s = SecurityUtils.toMD5(pw);
        System.out.println(s.length());
    }
}
