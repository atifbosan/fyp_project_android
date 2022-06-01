package com.fyp.alphatrackerapp.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;


public class Validation {

    public static boolean validiate (String pass, String username, String email, Context cntxt){
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$";
        if(pass.matches(pattern)){
            for(int i=0;(i+3)<username.length();i++){
                if(pass.contains(username.substring(i,i+3)) || username.length()<3 || username.length()>15){
                    return false;
                }
            }
            for(int i=0;(i+3)<email.length();i++){
                if(pass.contains(email.substring(i,i+3)) || email.length()<3 || email.length()>15){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    // if you want to know which requirement was not met
    public static boolean validiate2 (String pass, Context cntxt){

        if (pass.length() < 8){
           // System.out.println("pass too short or too long");
            Toast.makeText(cntxt,"Password is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (username.length() < 3 || username.length() >15 ){
            //System.out.println("username too short or too long");
            Toast.makeText(cntxt,"Password is too short",Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (!pass.matches(".*\\d.*")){
            //System.out.println("no digits found");
            Toast.makeText(cntxt,"No digits found", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!pass.matches(".*[a-z].*")) {
            //System.out.println("no lowercase letters found");
            Toast.makeText(cntxt,"No lowercase  letter found", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (!pass.matches(".*[!@#$%^&*+=?-].*")) {
            //System.out.println("no special chars found");
            Toast.makeText(cntxt,"No special character found",Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
    }

    private static boolean containsPartOf(String pass, String username) {
        int requiredMin = 3;
        for(int i=0;(i+requiredMin)<username.length();i++){
            if(pass.contains(username.substring(i,i+requiredMin))){
                return true;
            }
        }
        return false;
    }

    public final static boolean isValidEmail(CharSequence target, Context cntxt) {
        if(!TextUtils.isEmpty(target) )
        {
            if(Patterns.EMAIL_ADDRESS.matcher(target).matches())
            {
                return true;
            }
            else
            {
                Toast.makeText(cntxt,"Invalid Email", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        else {
            Toast.makeText(cntxt,"Email field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public static boolean isPassFieldNotEmpty(String str, Context cntxt)
    {
        if(str.matches(""))
        {
            Toast.makeText(cntxt,"Password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    public static boolean isPassSame(String pass, String confPass, Context cntxt)
    {
        if(pass.equals(confPass))
        {
            return true;
        }
        else {
            Toast.makeText(cntxt,"Password not match with confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
